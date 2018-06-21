package com.graly.erp.pdm.batchbom;

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

public class BatchBomEntryPage extends FormPage {
	private static final Logger logger = Logger.getLogger(BatchBomEntryPage.class);
	
	protected BatchBomSection batchBomSection;
	protected IManagedForm form;
	
	public BatchBomEntryPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public BatchBomEntryPage(String id, String title) {
		super(id, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		Composite body = managedForm.getForm().getBody();
		form = managedForm;
		
		ADTable adTable = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
		createSection(adTable);
		batchBomSection.createContents(form, body);
		try{
			String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
			((EntityEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
		}
	}

	private void createSection(ADTable adTable) {
		batchBomSection = new BatchBomSection(adTable);
	}
}
