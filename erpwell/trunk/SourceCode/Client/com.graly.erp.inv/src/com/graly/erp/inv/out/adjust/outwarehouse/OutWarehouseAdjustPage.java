package com.graly.erp.inv.out.adjust.outwarehouse;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.erp.inv.out.adjust.AdjustOutSection;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class OutWarehouseAdjustPage extends SectionEntryPage {
	public OutWarehouseAdjustPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public OutWarehouseAdjustPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new OutWarehouseAdjustSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('APPROVED', 'DRAFTED') ");
	}
}
