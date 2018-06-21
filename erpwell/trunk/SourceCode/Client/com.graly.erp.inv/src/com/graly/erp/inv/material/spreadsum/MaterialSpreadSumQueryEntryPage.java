package com.graly.erp.inv.material.spreadsum;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class MaterialSpreadSumQueryEntryPage extends SectionEntryPage {

	public MaterialSpreadSumQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public MaterialSpreadSumQueryEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MaterialSpreadSumQuerySection(new EntityTableManager(adTable));
	}
}
