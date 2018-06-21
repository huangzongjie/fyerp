package com.graly.erp.wip.workcenter.movement;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WCTMovementEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter.movement.WCTMovementEditor";
	
	protected IFormPage page;
	
	public WCTMovementEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new WCTMovementEntryPage(this, "", "");
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
