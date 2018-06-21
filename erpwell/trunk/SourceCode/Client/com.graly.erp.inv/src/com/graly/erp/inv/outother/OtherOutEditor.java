package com.graly.erp.inv.outother;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OtherOutEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.inv.outother.OtherOutEditor";
	
	protected IFormPage page;
	
	public OtherOutEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new OtherOutEntryPage(this, "", "");
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
