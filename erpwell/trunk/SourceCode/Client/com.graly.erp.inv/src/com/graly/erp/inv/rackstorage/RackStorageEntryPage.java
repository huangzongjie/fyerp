package com.graly.erp.inv.rackstorage;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class RackStorageEntryPage extends SectionEntryPage {
	private static final Logger logger = Logger.getLogger(RackStorageEntryPage.class);
	
	public RackStorageEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	public RackStorageEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name, table);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new RackStorageSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" 1=1 ");
	}
}
