package com.graly.erp.wip.mo;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MOEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.mo.MOEditor";
	
	protected IFormPage page;
	
	public MOEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new MOEntryPage(this, "", "");
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
