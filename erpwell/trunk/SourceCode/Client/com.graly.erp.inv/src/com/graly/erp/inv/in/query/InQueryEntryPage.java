package com.graly.erp.inv.in.query;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class InQueryEntryPage extends SectionEntryPage {

	public InQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public InQueryEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new InQuerySection(new EntityTableManager(adTable));
	}
}
