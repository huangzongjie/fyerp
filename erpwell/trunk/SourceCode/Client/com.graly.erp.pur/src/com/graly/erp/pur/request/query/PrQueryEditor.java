package com.graly.erp.pur.request.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class PrQueryEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.pur.request.query.PrQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new PrQueryEntryPage(this, "", "");
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
