package com.graly.erp.inv.iqc.statistics;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class IqcLineEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.iqc.statistics.IqcLineEditor";
	protected IFormPage page;
	
	public IqcLineEditor() {
		super();
	}

	@Override
	protected void addPages() {
		try {
			page = new IqcLineEntryPage(this, "", "");
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
