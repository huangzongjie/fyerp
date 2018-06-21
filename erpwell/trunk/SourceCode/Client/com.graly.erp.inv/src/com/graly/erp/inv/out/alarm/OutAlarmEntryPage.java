package com.graly.erp.inv.out.alarm;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class OutAlarmEntryPage extends SectionEntryPage {
	public OutAlarmEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public OutAlarmEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new OutAlarmSection(new EntityTableManager(adTable));
//		masterSection.setWhereClause(" 1<>1 ");
	}
}
