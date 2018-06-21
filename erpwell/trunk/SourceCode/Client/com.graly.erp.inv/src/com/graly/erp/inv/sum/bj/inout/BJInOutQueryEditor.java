package com.graly.erp.inv.sum.bj.inout;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class BJInOutQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.sum.bj.inout.BJInOutQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new BJInOutQueryEntryPage(this, "", "");
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
