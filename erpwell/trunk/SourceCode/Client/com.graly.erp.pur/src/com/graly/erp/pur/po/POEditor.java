package com.graly.erp.pur.po;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class POEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.pur.po.POEditor";
	
	protected IFormPage page;
	
	public POEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new POEntryPage(this, "", "");
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
