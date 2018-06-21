package com.graly.erp.inv.outserialquery;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OutserialQueryEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.outserialquery.OutserialQueryEditor";
	
	protected IFormPage page;
	
	public OutserialQueryEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new OutserialQueryEntryPage(this, "", "");
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
