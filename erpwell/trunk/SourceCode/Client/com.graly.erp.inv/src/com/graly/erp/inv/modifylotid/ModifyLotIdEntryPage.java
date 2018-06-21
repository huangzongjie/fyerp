package com.graly.erp.inv.modifylotid;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.ui.util.Message;

public class ModifyLotIdEntryPage extends FormPage {
	private static final Logger logger = Logger.getLogger(ModifyLotIdEntryPage.class);
	private ADTable table;
	private ModifyLotIdSection searchByLotSection;
	protected IManagedForm form;
	
	public ModifyLotIdEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public ModifyLotIdEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		this.form = managedForm;
		Composite body = managedForm.getForm().getBody();//获得当前form的body
		
		try{
			table = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
			((EntityEditor)this.getEditor()).setEditorTitle(Message.getString("inv.modify_lotid"));
		} catch (Exception e){
			logger.error("Error At SearchByLotEntryPage.createFormContent() Method :" + e);
		}
		searchByLotSection = new ModifyLotIdSection(table);
		searchByLotSection.createContents(form, body);
	}

	@Override
	public void dispose() {
		searchByLotSection.disposeContent();
	}

	@Override
	public void setFocus() {
		searchByLotSection.setFocus();
	}
}
