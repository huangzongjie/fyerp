package com.graly.erp.vdm.vendorassess;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.views.TableListManager;

public class VendorAssessPage extends SectionEntryPage {

	public VendorAssessPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public VendorAssessPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new VendorAssessSection(new TableListManager(adTable));
		masterSection.setWhereClause(" 1 <> 1");
	}
}
