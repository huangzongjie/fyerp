package com.graly.erp.pur.lot.query;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MerchandiseBatchPage extends FormPage {
	private static final Logger logger = Logger.getLogger(MerchandiseBatchPage.class);
	public static final String TABLE_NAME = "MerchandiseBatch";
	protected ADTable adTable;
	protected MerchandiseBatchSection mbatchSection;
	
	public MerchandiseBatchPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public MerchandiseBatchPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {		
		getAdTableOfWorkCenter();
		try{
			String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
			((EntityEditor)this.getEditor()).setEditorTitle("商品批次查询");
		} catch (Exception e){
			logger.error("Error At WorkCenterEntryPage.createFormContent(): " + e);
		}
		Composite body = managedForm.getForm().getBody();
		configureBody(body);
		
		mbatchSection = new MerchandiseBatchSection(((EntityEditorInput)this.getEditor().getEditorInput()).getTable(), adTable);
		mbatchSection.setIManagedForm(managedForm);
		mbatchSection.createContent(body);

		setFocus();
	}


 

	
	public MerchandiseBatchSection getMbatchSection() {
		return mbatchSection;
	}

	public void setMbatchSection(MerchandiseBatchSection mbatchSection) {
		this.mbatchSection = mbatchSection;
	}

	public void refresh() {
		try {
			mbatchSection.refresh();
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("WorkCenterEntryPage : refresh()", e);
		}
	}
	
	protected void getAdTableOfWorkCenter() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = (ADTable)entityManager.getADTable(0, TABLE_NAME);				
			}
		} catch(Exception e) {
			logger.error("InvMaterialSection : getAdTableOfInvMaterial()", e);
		}
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
