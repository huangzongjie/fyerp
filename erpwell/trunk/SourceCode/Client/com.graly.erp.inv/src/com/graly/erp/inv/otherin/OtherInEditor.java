package com.graly.erp.inv.otherin;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OtherInEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.otherin.OtherInEditor";
	
	protected IFormPage page;
	
	public OtherInEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new OtherInEntryPage(this, "", "");
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
