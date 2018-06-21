package com.graly.erp.wip.lothis;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WipHisQueryEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.lothis.WipHisQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new WipHisQueryEntryPage(this, "", "");
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
