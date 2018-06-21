package com.graly.erp.wip.mo;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class MOEntryPage extends SectionEntryPage {
	
	public MOEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public MOEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MOSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('DRAFTED','APPROVED','PREPARE') and updated > add_Months(sysdate,-1)");
	}
}