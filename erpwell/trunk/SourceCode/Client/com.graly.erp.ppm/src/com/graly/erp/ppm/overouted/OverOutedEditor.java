package com.graly.erp.ppm.overouted;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OverOutedEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.ppm.overouted.OverOutedEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new OverOutedEntryPage(this, "", "");
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
