package com.graly.erp.ppm.statistic;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MpsStatisticEntityEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.ppm.statistic.MpsStatisticEntityEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MpsStatisticEntryPage(this, "", "");
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
