package com.graly.erp.inv.reversal.temp.estimate;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class TempEstimateEntryPage extends SectionEntryPage {


	public TempEstimateEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name, table);
	}

	public TempEstimateEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new TempEstimateSection(new EntityTableManager(adTable));
	}

}
