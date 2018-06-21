package com.graly.erp.inv.out.adjust.sell;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.erp.inv.out.adjust.AdjustOutSection;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class SellAdjustPage extends SectionEntryPage {
	public SellAdjustPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public SellAdjustPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new SellAdjustSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('APPROVED', 'DRAFTED') ");
	}
}
