package com.graly.erp.inv.out.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OutQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.out.query.OutQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new OutQueryEntryPage(this, "", "");
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
