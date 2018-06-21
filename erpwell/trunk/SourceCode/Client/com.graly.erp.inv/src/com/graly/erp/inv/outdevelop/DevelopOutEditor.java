package com.graly.erp.inv.outdevelop;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.inv.outother.OtherOutEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class DevelopOutEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.outdevelop.DevelopOutEditor";
		
		protected IFormPage page;
		
		public DevelopOutEditor(){
		}
		
		@Override
		protected void addPages() {
			try {
				page = new DevelopOutEntryPage(this, "", "");
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
