package com.graly.erp.ppm.mpsline;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MpsEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.ppm.mpsline.MpsEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MpsEntryPage(this, "", "");
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
