package com.graly.erp.inv.barcode;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.ui.util.Message;

public class BarcodeEntryPage extends SectionEntryPage {
	private static final Logger logger = Logger.getLogger(BarcodeEntryPage.class);
	
	public BarcodeEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public BarcodeEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		((EntityEditor)this.getEditor()).setEditorTitle(Message.getString("inv.barcode_manager"));
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new IqcSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('APPROVED') ");
	}
}
