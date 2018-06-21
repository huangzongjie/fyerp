package com.graly.erp.inv.selectbylot;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class SearchByLotEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.selectbylot.SearchByLotEditor";
	
	protected IFormPage page;
	
	public SearchByLotEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new SearchByLotEntryPage(this, "", "");
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
