package com.graly.erp.pur.vendorgoal;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.uiX.viewmanager.forms.XSectionEditor;

public class VendorGoalSectionEditor extends XSectionEditor {

	public static final String EDITOR_ID = "com.graly.erp.pur.vendorgoal.VendorGoalSectionEditor";
	protected IFormPage page;
	
	public VendorGoalSectionEditor() {
	}
	
	@Override
	protected void addPages() {
		try {
			page = new VendorGoalEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setFocus() {
		page.setFocus();
	}
}
