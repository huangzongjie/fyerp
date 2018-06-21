package com.graly.erp.inv.outserialquery;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.ui.util.Message;

public class OutserialQueryEntryPage extends EntityEntryPage {
	private static final Logger logger = Logger.getLogger(OutserialQueryEntryPage.class);
	private ADTable table;
	private OutserialQuerySection outserialQuerySection;
	protected IManagedForm form;
	
	public OutserialQueryEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public OutserialQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
//		super.createFormContent(managedForm);
		this.form = managedForm;
		Composite body = managedForm.getForm().getBody();
		
		try{
			table = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
			((EntityEditor)this.getEditor()).setEditorTitle(Message.getString("inv.selectbylot"));
		} catch (Exception e){
			logger.error("Error At SearchByLotEntryPage.createFormContent() Method :" + e);
		}
		outserialQuerySection = new OutserialQuerySection(table);
		outserialQuerySection.createContents(form, body);
	}

	@Override
	public void dispose() {
		outserialQuerySection.disposeContent();
	}

	@Override
	public void setFocus() {
		outserialQuerySection.setFocus();
	}
}
