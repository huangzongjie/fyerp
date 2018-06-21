package com.graly.erp.wip.prepare.tpsline;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class PrepareTpsLineEntityEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.prepare.tpsline.PrepareTpsLineEntityEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new PrepareTpsLineEntryPage(this, "", "");
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
