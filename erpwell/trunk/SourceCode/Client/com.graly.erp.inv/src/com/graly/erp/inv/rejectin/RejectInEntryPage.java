package com.graly.erp.inv.rejectin;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.erp.inv.otherin.OtherInEntryPage;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Message;

public class RejectInEntryPage extends OtherInEntryPage {

	public RejectInEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public RejectInEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new RejectInSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docType in ('RIN') ");
	}
	
	// 重载实现标题的提示信息的改变inv.reject_in
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		this.form = managedForm;
		Composite body = managedForm.getForm().getBody();
		
		ADTable adTable = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
		try{
			String editorTitle = String.format(Message.getString("common.editor"),
					Message.getString("inv.reject_in"));
			((EntityEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
		}
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createSection(adTable);
		masterSection.createContents(form, body);
		setFocus();
	}

}
