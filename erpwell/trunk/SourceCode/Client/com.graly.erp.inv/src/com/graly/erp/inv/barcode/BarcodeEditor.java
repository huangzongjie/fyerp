package com.graly.erp.inv.barcode;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class BarcodeEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.inv.barcode.BarcodeEditor";
	
	protected IFormPage page;
	
	public BarcodeEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new BarcodeEntryPage(this, "", "");
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
