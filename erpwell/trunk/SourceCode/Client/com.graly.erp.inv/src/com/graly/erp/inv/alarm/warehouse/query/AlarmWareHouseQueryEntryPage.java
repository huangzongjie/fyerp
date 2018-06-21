package com.graly.erp.inv.alarm.warehouse.query;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class AlarmWareHouseQueryEntryPage extends SectionEntryPage {
	public AlarmWareHouseQueryEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public AlarmWareHouseQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new AlarmWareHouseQuerySection(new EntityTableManager(adTable));
//		masterSection.setWhereClause(" 1<>1 ");
	}
}
