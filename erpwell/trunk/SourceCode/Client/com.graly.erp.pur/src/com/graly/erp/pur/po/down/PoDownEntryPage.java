package com.graly.erp.pur.po.down;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class PoDownEntryPage extends SectionEntryPage {
	public PoDownEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public PoDownEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new PoDownSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" 1 <> 1 ");
	}
 
}
