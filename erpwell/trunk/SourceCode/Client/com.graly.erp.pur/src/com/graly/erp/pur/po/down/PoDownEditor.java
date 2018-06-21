package com.graly.erp.pur.po.down;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class PoDownEditor extends EntityEditor {
	public static final String ID= "com.graly.erp.pur.po.down.PoDownEditor";
	protected IFormPage page;
	@Override
	protected void addPages() {
		try {
			page = new PoDownEntryPage(this, "", "");
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
