package com.graly.erp.inv.wirteoffselect;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MovementInListEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.wirteoffselect.MovementInListEditor";
	
	protected IFormPage page;
	
	public MovementInListEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new MovementInListEntryPage(this, "", "");
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
