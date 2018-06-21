package com.graly.erp.ppm.lading;

import org.eclipse.ui.forms.editor.FormEditor;
import com.graly.framework.activeentity.model.ADTable;

import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class LadingEntryPage extends EntityEntryPage {
	protected LadingEntityBlock planBlock = null;

	public LadingEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name);
	}

	public LadingEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		planBlock = new LadingEntityBlock(new EntityTableManager(adTable));
		block = planBlock;
	}

	public LadingEntityBlock getPlanBlock() {
		return planBlock;
	}

	public void setPlanBlock(LadingEntityBlock planBlock) {
		this.planBlock = planBlock;
	}
}
