package com.graly.erp.inv.adjust.out;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class AdjustOutEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.inv.adjust.out.AdjustOutEditor";
	
	protected IFormPage page;
	
	public AdjustOutEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new AdjustOutEntryPage(this, "", "");
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