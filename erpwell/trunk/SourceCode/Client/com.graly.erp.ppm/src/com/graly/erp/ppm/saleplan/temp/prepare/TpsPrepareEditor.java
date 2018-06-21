package com.graly.erp.ppm.saleplan.temp.prepare;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.ppm.saleplan.SalePlanEditor;

public class TpsPrepareEditor extends SalePlanEditor {

	public static final String EDITOR_ID = "com.graly.erp.ppm.saleplan.temp.prepare.TpsPrepareEditor";
	protected IFormPage page;

	@Override
	protected void addPages() {
		try {
			page = new TpsPrepareEntryPage(this, "", "");
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
