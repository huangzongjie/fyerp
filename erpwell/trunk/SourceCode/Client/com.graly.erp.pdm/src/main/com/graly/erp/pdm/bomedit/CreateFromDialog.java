package com.graly.erp.pdm.bomedit;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class CreateFromDialog extends InClosableTitleAreaDialog {
	private static final String TABLE_NAME = "BASMaterial";
	protected ADManager adManager;
	protected PDMManager pdmManager;
	protected Material material;
	protected FormToolkit toolkit;
	protected SearchField sfMaterial;
//	protected Text txtMaterialId;
	private List<Bom> boms;

	public CreateFromDialog(Material material, FormToolkit toolkit) {
		super(UI.getActiveShell());
		this.material = material;
		this.toolkit = toolkit;
	}
	
	@Override
    protected Control createDialogArea(Composite composite) {
		setTitle(Message.getString("pdm.select_material_bom"));
		Composite parent = toolkit.createComposite(composite, SWT.BORDER);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		try {
			if(adManager == null) 
				adManager = Framework.getService(ADManager.class);
			ADRefTable refTable = new ADRefTable();
			refTable.setKeyField("objectRrn");
			refTable.setValueField("materialId");
			ADTable adTable = adManager.getADTable(0L, TABLE_NAME);
			refTable.setTableRrn(adTable.getObjectRrn());
			
			String whereClause = "";
			if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
				whereClause = refTable.getWhereClause();
			}
			sfMaterial = new SearchField("materialId", adTable, refTable, whereClause, SWT.BORDER);
			sfMaterial.setLabel(Message.getString("pdm.material_id"));
			sfMaterial.createContent(parent, toolkit);
			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return parent;
    }

	@Override
	protected void okPressed() {
		try {
			Material material = null;
			if(sfMaterial.getData() instanceof Material) {
				material = (Material)sfMaterial.getData();
			} else {
				if("".equals(sfMaterial.getText()))
					return;
				String materialId = sfMaterial.getText();
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				String whereClause = " materialId = '" + materialId + "' ";
				List<Material> ms = adManager.getEntityList(Env.getOrgRrn(),
						Material.class, 1, whereClause, null);
				if(ms == null || ms.size() == 0) {
					UI.showError(String.format(Message.getString("sal.materialid_is_not_exist"), materialId));
					return;
				}
				material = ms.get(0);
			}
			if(pdmManager == null)
				pdmManager = Framework.getService(PDMManager.class);
			boms = pdmManager.getChildrenBoms(material.getObjectRrn(), BigDecimal.ONE);
			if(boms == null || boms.size() == 0) {
				UI.showError(String.format(Message.getString("sal.has_not_bom"), material.getMaterialId()));
				return;
			}
			doBomsContent(material);
			super.okPressed();
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void doBomsContent(Material bomMaterial) {
		for(Bom bom : boms) {
			bom.setObjectRrn(null);
			bom.setDbaMark("CPOYFROM_materialParentRrn:"+bom.getParentRrn()+".CPOYFROM_materialParentVerion:"+bom.getParentVersion());
			bom.setParentRrn(material.getObjectRrn());
		}
	}
	
	public List<Bom> getBoms() {
		return boms;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
}
