package com.graly.erp.wip.workcenter.schedule.purchase3;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class PurchaseMaterialEntryPage extends SectionEntryPage {

	public PurchaseMaterialEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public PurchaseMaterialEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new PurchaseMaterialSection(new EntityTableManager(adTable));
		masterSection.setWhereClause("");
	}

}
