package com.graly.erp.pur.vendorgoal;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.uiX.viewmanager.XTableViewerAdapter;
import com.graly.framework.base.uiX.viewmanager.XTableViewerManager;
import com.graly.framework.base.uiX.viewmanager.forms.XSectionEntryPage;

public class VendorGoalEntryPage extends XSectionEntryPage {
	
	public VendorGoalEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public VendorGoalEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new VendorGoalSection(new XTableViewerManager(new XTableViewerAdapter(adTable)));
	}
}
