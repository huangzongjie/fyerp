package com.graly.erp.inv.out.adjust.inwaehouser;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.erp.inv.out.adjust.AdjustOutSection;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class InWarehouseAdjustPage extends SectionEntryPage {
	public InWarehouseAdjustPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public InWarehouseAdjustPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new InWarehouseAdjustSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('APPROVED', 'DRAFTED') ");
	}
}
