package com.graly.erp.inv.mo.consume;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MaterialConsumeEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.mo.consume.MaterialConsumeEditor";
	
	protected IFormPage page;
	
	public MaterialConsumeEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new MaterialCosumeEntryPage(this, "", "");
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
