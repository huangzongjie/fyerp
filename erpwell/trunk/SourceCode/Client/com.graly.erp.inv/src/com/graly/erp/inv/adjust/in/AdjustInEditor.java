package com.graly.erp.inv.adjust.in;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class AdjustInEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.adjust.in.AdjustInEditor";
	
	protected IFormPage page;
	
	public AdjustInEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new AdjustInEntryPage(this, "", "");
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
