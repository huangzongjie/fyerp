package com.graly.erp.wip.output;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OutputEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.output.OutputEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new OutputEntryPage(this, "", "");
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
