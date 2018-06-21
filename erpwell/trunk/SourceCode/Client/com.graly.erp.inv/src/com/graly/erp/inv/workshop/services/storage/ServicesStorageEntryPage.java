package com.graly.erp.inv.workshop.services.storage;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class ServicesStorageEntryPage extends SectionEntryPage {
	private static final Logger logger = Logger.getLogger(ServicesStorageEntryPage.class);
	
	public ServicesStorageEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public ServicesStorageEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		try {
			masterSection = new ServicesStorageSection(new EntityTableManager(adTable));
			masterSection.setWhereClause(" docStatus in ('APPROVED', 'DRAFTED') ");
		} catch(Exception e) {
			logger.error("Error at TransferEntryPage : createSection()", e);
		}
	}

}
