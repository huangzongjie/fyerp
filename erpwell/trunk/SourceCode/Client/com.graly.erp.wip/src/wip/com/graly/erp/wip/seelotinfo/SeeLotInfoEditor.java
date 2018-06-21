package com.graly.erp.wip.seelotinfo;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class SeeLotInfoEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.seelotinfo.SeeLotInfoEditor";
	
	protected IFormPage page;
	
	public SeeLotInfoEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new SeeLotInfoEntryPage(this, "", "");
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
