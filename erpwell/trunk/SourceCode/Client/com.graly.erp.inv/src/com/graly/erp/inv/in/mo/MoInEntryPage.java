package com.graly.erp.inv.in.mo;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class MoInEntryPage extends SectionEntryPage {
	
	public MoInEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public MoInEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MoInSection(new EntityTableManager(adTable));
//		masterSection.setWhereClause(" docStatus in ('DRAFTED') ");		
	}
}
