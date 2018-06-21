package com.graly.erp.internalorder.po;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class InternalOrderPoEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.internalorder.po.InternalOrderPoEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new InternalOrderPoEntryPage(this, "", "");
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
