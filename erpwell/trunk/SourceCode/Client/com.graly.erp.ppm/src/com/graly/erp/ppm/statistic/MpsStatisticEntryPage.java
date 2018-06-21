package com.graly.erp.ppm.statistic;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class MpsStatisticEntryPage extends SectionEntryPage {

	public MpsStatisticEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public MpsStatisticEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MpsStatisticSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" 1 <> 1 ");
	}
}
