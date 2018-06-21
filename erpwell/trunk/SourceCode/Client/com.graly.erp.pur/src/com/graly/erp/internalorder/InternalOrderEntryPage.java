package com.graly.erp.internalorder;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class InternalOrderEntryPage extends SectionEntryPage {
	public InternalOrderEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public InternalOrderEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new InternalOrderSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docType ='PPM'" );
	}
}
