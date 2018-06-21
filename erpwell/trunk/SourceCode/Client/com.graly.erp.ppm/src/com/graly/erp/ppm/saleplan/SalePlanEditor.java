package com.graly.erp.ppm.saleplan;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class SalePlanEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.ppm.saleplan.SalePlanEditor";
	protected IFormPage page;

	@Override
	protected void addPages() {
		try {
			page = new SalePlanEntryPage(this, "", "");
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
