package com.graly.erp.inv.material.nomove;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
 * 未出入库的物料
 */
public class NoMoveEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.material.nomove.NoMoveEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new NoMoveEntryPage(this, "", "");
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
