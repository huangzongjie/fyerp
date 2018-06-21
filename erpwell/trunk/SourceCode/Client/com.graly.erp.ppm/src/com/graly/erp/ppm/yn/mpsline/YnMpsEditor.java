package com.graly.erp.ppm.yn.mpsline;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class YnMpsEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.ppm.yn.mpsline.YnMpsEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new YnMpsEntryPage(this, "", "");
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
