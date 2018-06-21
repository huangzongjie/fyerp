package com.graly.erp.inv.material;

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



public class MaterialNewSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MaterialNewSection.class);
	protected ToolItem itemBarcode;
	protected VStorageMaterial selectedLine;
	protected ToolItem itemFinancialOverseas;
	protected ToolItem itemFinancialOverseasDetail;
	protected SashForm sashForm;
	
	protected WorkShopStorageSection workShopStorageSection;
	protected MaterialSection materialSection;
	
	protected IManagedForm form;
	public MaterialNewSection(EntityTableManager tableManager) {
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
//		ADTable wsScheduleTable = getAdTableByName("WorkShopScheduleQuery");
//		EntityTableManager tableManager = new EntityTableManager(wsScheduleTable);
		materialSection = new MaterialSection(getTableManager(),this);
		materialSection.setWhereClause(this.getWhereClause());
		materialSection.createContents(form, parent);
	}
	
	protected void createWorkShopSectionContent(IManagedForm form, Composite parent) {
		ADTable wsScheduleTable = getAdTableByName("INVWorkShopStorage");
		EntityTableManager tableManager = new EntityTableManager(wsScheduleTable);
		workShopStorageSection = new WorkShopStorageSection(tableManager, null);
//		noScheduleSection.setWhereClause("1<>1");
		workShopStorageSection.createContents(form, parent);
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

	public WorkShopStorageSection getWorkShopStorageSection() {
		return workShopStorageSection;
	}

	public void setWorkShopStorageSection(
			WorkShopStorageSection workShopStorageSection) {
		this.workShopStorageSection = workShopStorageSection;
	}
}
