package com.graly.erp.inv.transfer.to.cana;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class TransferToCanaEntryPage extends SectionEntryPage {
	private static final Logger logger = Logger.getLogger(TransferToCanaEntryPage.class);
	
	public TransferToCanaEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public TransferToCanaEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		try {
			masterSection = new TransferToCanaSection(new EntityTableManager(adTable));
			masterSection.setWhereClause(" docStatus in ('APPROVED', 'DRAFTED')");
		} catch(Exception e) {
			logger.error("Error at TransferEntryPage : createSection()", e);
		}
	}

}
