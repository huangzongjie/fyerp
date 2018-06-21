package com.graly.erp.inv.split;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class SplitEntryPage extends FormPage {
	private static final Logger logger = Logger.getLogger(SplitEntryPage.class);
	private SplitSection splitSection;
	protected IManagedForm form;

	public SplitEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		this.form = managedForm;
		
		Composite body = managedForm.getForm().getBody();//获得当前form的body
		ADTable table = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
		try{
			String editorTitle = String.format(Message.getString("common.editor"),
					I18nUtil.getI18nMessage(table, "label"));
			((EntityEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
			logger.error("Error At SplitEntryPage.createFormContent(): ", e);
		}
		splitSection = new SplitSection(table);
		splitSection.createContents(managedForm, body);
	}

	@Override
	public void dispose() {
		splitSection.disposeContent();
	}

	@Override
	public void setFocus() {
		splitSection.setFocus();
	}
}
