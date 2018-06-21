package com.graly.erp.inv.sum.bj.inout;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class BJInOutQueryEntryPage extends SectionEntryPage {

	public BJInOutQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public BJInOutQueryEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new BJInOutQuerySection(new EntityTableManager(adTable));
	}
}
