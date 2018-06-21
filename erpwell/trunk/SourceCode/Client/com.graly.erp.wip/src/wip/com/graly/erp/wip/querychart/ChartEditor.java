package com.graly.erp.wip.querychart;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.wip.mo.MOEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class ChartEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.querychart.ChartEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new ChartEntryPage(this, "", "");
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
