package com.graly.erp.inv.sum.xz.inout;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class XZInOutQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.sum.bj.inout.XZInOutQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new XZInOutQueryEntryPage(this, "", "");
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
