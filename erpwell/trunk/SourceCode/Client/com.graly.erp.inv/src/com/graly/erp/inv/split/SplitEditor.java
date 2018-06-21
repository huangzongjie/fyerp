package com.graly.erp.inv.split;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class SplitEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.split.SplitEditor";
	
	protected IFormPage page;
	
	public SplitEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new SplitEntryPage(this, "", "");
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
