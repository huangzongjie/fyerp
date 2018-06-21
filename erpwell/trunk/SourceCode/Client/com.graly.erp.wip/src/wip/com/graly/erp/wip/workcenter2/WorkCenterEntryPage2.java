package com.graly.erp.wip.workcenter2;

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
import com.graly.framework.security.model.WorkCenter;

public class WorkCenterEntryPage2 extends FormPage {
	private static final Logger logger = Logger.getLogger(WorkCenterEntryPage2.class);
	public static final String TABLE_NAME = "WIPWorkCenter";
	protected ADTable adTable;
	protected WorkCenterSection2 wcSection;
	
	public WorkCenterEntryPage2(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public WorkCenterEntryPage2(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {		
		getAdTableOfWorkCenter();
		try{
			String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
			((EntityEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
			logger.error("Error At WorkCenterEntryPage.createFormContent(): " + e);
		}
		Composite body = managedForm.getForm().getBody();
		configureBody(body);
		
		wcSection = new WorkCenterSection2(((EntityEditorInput)this.getEditor().getEditorInput()).getTable(), adTable);
		wcSection.setIManagedForm(managedForm);
		wcSection.createContent(body);

		setFocus();
	}

	public WorkCenter getWorkCenter() {
		return wcSection.getWorkCenter();
	}

	public void setWorkCenter(WorkCenter workCenter) {
		if(workCenter != null) {
			wcSection.setWorkCenter(workCenter);
		}
	}
	
	public void refresh() {
		try {
			wcSection.refresh();
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
