package com.graly.erp.inv.sum.xz.inout;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class XZInOutQueryEntryPage extends SectionEntryPage {

	public XZInOutQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public XZInOutQueryEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new XZInOutQuerySection(new EntityTableManager(adTable));
	}
}
