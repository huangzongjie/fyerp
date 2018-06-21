package com.graly.erp.inv.material.nomove;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.views.TableListManager;

public class NoMoveEntryPage extends SectionEntryPage {
	
	public NoMoveEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public NoMoveEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new NoMoveSection(new TableListManager(adTable));
		masterSection.setWhereClause(" 1 <> 1 ");
	}

}