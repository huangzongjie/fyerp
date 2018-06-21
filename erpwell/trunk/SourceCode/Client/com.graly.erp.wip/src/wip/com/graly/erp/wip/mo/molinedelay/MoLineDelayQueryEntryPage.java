package com.graly.erp.wip.mo.molinedelay;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class MoLineDelayQueryEntryPage extends SectionEntryPage {
	public MoLineDelayQueryEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public MoLineDelayQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MoLineDelayQuerySection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" 1<>1 ");
	}
}
