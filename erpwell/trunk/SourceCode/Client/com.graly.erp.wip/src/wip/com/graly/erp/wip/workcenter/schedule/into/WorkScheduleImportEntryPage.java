package com.graly.erp.wip.workcenter.schedule.into;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class WorkScheduleImportEntryPage extends SectionEntryPage {

	public WorkScheduleImportEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public WorkScheduleImportEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new WorkScheduleImportSection(new EntityTableManager(adTable));
		masterSection.setWhereClause("1<>1");
	}
	
	public WorkScheduleImportSection getImportSection(){
		return (WorkScheduleImportSection) masterSection;
	}
	
	
}
