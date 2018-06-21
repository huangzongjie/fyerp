package com.graly.erp.inv.racklot;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class RackLotEntryPage extends SectionEntryPage {
	private static final Logger logger = Logger.getLogger(RackLotEntryPage.class);
	
	public RackLotEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	public RackLotEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name, table);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new RackLotSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" 1=1 ");
	}
}
