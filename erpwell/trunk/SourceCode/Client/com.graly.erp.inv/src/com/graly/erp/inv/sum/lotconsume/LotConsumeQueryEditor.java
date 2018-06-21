package com.graly.erp.inv.sum.lotconsume;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class LotConsumeQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.sum.lotconsume.LotConsumeQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new LotConsumeQueryEntryPage(this, "", "");
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
