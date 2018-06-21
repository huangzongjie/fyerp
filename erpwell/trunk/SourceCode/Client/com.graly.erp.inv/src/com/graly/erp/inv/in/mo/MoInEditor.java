package com.graly.erp.inv.in.mo;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MoInEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.in.mo.MoInEditor";
	
	protected IFormPage page;
	
	public MoInEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new MoInEntryPage(this, "", "");
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
