package com.graly.erp.inv.iqc.statistics;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class IqcLineEditor2 extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.iqc.statistics.IqcLineEditor2";
	protected IFormPage page;
	
	public IqcLineEditor2() {
		super();
	}

	@Override
	protected void addPages() {
		try {
			page = new IqcLineEntryPage2(this, "", "");
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
