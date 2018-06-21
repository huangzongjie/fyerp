package com.graly.erp.pdm.material.firstbom;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.forms.MDSashForm;
import com.graly.framework.runtime.Framework;



public class FirstBomSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(FirstBomSection.class);
	protected ToolItem itemBarcode;
	protected VStorageMaterial selectedLine;
	protected ToolItem itemFinancialOverseas;
	protected ToolItem itemFinancialOverseasDetail;
	protected SashForm sashForm;
	
	protected FirstBomResultSection firstBomResultSection;
	protected MaterialImportSection materialImportSection;
	
	protected IManagedForm form;
	public FirstBomSection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause("1<>1");//刚打开时显示空内容
	}

	@Override
	public void createContents(IManagedForm form, Composite parent) {
		FormToolkit toolkit = form.getToolkit();
		sashForm = new MDSashForm(parent, SWT.NULL);
		sashForm.setData("form", form); //$NON-NLS-1$
		toolkit.adapt(sashForm, false, false);
		sashForm.setMenu(parent.getMenu());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		createMaterialSectionContent(form, sashForm);
		createWorkShopSectionContent(form, sashForm);
		sashForm.setOrientation(SWT.VERTICAL);
//		createToolBarActions(form);
	}
	
	protected void createMaterialSectionContent(IManagedForm form, Composite parent) {
		materialImportSection = new MaterialImportSection(getTableManager(),this);
		materialImportSection.setWhereClause(this.getWhereClause());
		materialImportSection.createContents(form, parent);
	}
	
	protected void createWorkShopSectionContent(IManagedForm form, Composite parent) {
		ADTable wsScheduleTable = getAdTableByName("TempFirstBom");
		EntityTableManager tableManager = new EntityTableManager(wsScheduleTable);
		firstBomResultSection = new FirstBomResultSection(tableManager, null);
		firstBomResultSection.createContents(form, parent);
	}
	
	protected ADTable getAdTableByName(String tableName) {
		ADTable adTable = null;;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0, tableName);
		} catch(Exception e) {
			logger.error("WorkShopScheduleQuerySection : getAdTableByName()", e);
		}
		return adTable;
	}
	
	

	public FirstBomResultSection getFirstBomResultSection() {
		return firstBomResultSection;
	}

	public void setFirstBomResultSection(FirstBomResultSection firstBomResultSection) {
		this.firstBomResultSection = firstBomResultSection;
	}
}
