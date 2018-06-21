package com.graly.erp.xz.inv.in;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class XZInEntryPage extends SectionEntryPage {
	public XZInEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public XZInEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new XZInSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('DRAFTED','APPROVED') and updated > add_Months(sysdate,-1)");
	}
}