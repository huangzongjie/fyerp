package com.graly.erp.inv.iqc.statistics;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class IqcLineEntryPage extends SectionEntryPage {

	public IqcLineEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public IqcLineEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new IqcLineSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" lineStatus in ('APPROVED') ");
	}
}
