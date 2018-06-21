package com.graly.erp.wip.workcenter.schedule.purchase2;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.forms.MDSashForm;
import com.graly.framework.runtime.Framework;



//4+4用起来后，3+3就没必要进行使用
public class PurchaseMaterialMainSection2 extends MasterSection {
	private static final Logger logger = Logger.getLogger(PurchaseMaterialMainSection2.class);
	protected ToolItem itemBarcode;
	protected ToolItem itemPreview;
	protected ToolItem itemNote;
	protected Label labe;
	protected SashForm sashForm;
	public PurchaseMaterialMainSection2(EntityTableManager tableManager) {
		super(tableManager);
//		setWhereClause("1<>1");//刚打开时显示空内容
	}

	protected void refreshSection() {
		refresh();
	}
	 
	@Override
	public void createContents(IManagedForm form, Composite parent) {
		FormToolkit toolkit = form.getToolkit();
		sashForm = new MDSashForm(parent, SWT.NULL);
		sashForm.setData("form", form); //$NON-NLS-1$
		toolkit.adapt(sashForm, false, false);
		sashForm.setMenu(parent.getMenu());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		createSectionContent(form, sashForm);
		sashForm.setOrientation(SWT.VERTICAL);
	}
	
	protected PurchaseMaterialSection2 materialSection;
	protected PurImportSection importSection;
	protected void createSectionContent(IManagedForm form, Composite parent) {
		materialSection = new PurchaseMaterialSection2(getTableManager(),this);
		materialSection.setWhereClause(this.getWhereClause());
		materialSection.createContents(form, parent);
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
}
