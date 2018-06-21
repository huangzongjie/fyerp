package com.graly.erp.wip.virtualhouse;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class VirtualHouseEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.wip.virtualhouse.VirtualHouseEditor";
	
	protected IFormPage page;
	
	public VirtualHouseEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new VirtualHouseEntryPage(this, "", "");
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
