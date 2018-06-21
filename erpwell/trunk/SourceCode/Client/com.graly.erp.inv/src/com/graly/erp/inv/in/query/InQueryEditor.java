package com.graly.erp.inv.in.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class InQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.in.query.InQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new InQueryEntryPage(this, "", "");
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
