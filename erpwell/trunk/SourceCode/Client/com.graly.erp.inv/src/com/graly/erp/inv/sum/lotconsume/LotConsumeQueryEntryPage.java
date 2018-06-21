package com.graly.erp.inv.sum.lotconsume;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class LotConsumeQueryEntryPage extends SectionEntryPage {

	public LotConsumeQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public LotConsumeQueryEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new LotConsumeQuerySection(new EntityTableManager(adTable));
	}
}
