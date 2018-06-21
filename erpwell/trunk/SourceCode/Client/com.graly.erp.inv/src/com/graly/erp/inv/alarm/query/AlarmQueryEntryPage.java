package com.graly.erp.inv.alarm.query;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class AlarmQueryEntryPage extends SectionEntryPage {
	public AlarmQueryEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public AlarmQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new AlarmQuerySection(new EntityTableManager(adTable));
//		masterSection.setWhereClause(" 1<>1 ");
	}
}
