package com.graly.erp.inv.iqc;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class IqcEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.iqc.IqcEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new IqcEntryPage(this, "", "");
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
