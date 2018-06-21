package com.graly.erp.pur.lot.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.pur.po.POEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MerchandiseBatchEditor extends EntityEditor {
	public static final String editor_ID = "com.graly.erp.pur.lot.query.MerchandiseBatchEditor";
	protected IFormPage page;
	@Override
	protected void addPages() {
		page = new MerchandiseBatchPage(this, "", "");
		try {
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void setFocus() {
		super.setFocus();
	}

}
