package com.graly.erp.inv.material.movesum;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.views.TableListManager;

public class MoveSumEntryPage extends SectionEntryPage {
	
	public MoveSumEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public MoveSumEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MoveSumSection(new TableListManager(adTable));
		masterSection.setWhereClause(" 1 <> 1 ");
	}
}
