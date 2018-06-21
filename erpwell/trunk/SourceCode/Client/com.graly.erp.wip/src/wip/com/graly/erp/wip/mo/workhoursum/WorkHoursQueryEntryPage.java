package com.graly.erp.wip.mo.workhoursum;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class WorkHoursQueryEntryPage extends SectionEntryPage {

	public WorkHoursQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public WorkHoursQueryEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new WorkHoursQuerySection(new EntityTableManager(adTable));
	}
}
