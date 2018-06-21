package com.graly.erp.pur.receipt.date;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class POReceiptDateQueryEntryPage extends SectionEntryPage {
	public POReceiptDateQueryEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public POReceiptDateQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new POReceiptDateQuerySection(new EntityTableManager(adTable));
//		masterSection.setWhereClause(" 1<>1 ");
	}
}
