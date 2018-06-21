package com.graly.erp.inv.material.movesum;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
 * 出入库物料汇总
 */
public class MoveSumEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.material.movesum.MoveSumEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MoveSumEntryPage(this, "", "");
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
