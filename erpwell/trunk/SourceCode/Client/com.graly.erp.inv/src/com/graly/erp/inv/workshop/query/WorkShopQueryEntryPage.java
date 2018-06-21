package com.graly.erp.inv.workshop.query;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class WorkShopQueryEntryPage extends SectionEntryPage {

	public WorkShopQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public WorkShopQueryEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new WorkShopQuerySection(new EntityTableManager(adTable));
	}
}
