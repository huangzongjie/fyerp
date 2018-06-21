package com.graly.erp.inv.materialtrace;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MaterialTraceEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.materialtrace.MaterialTraceEditor";
	public Logger logger = Logger.getLogger(MaterialTraceEditor.class);
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MaterialTraceEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			logger.error(e);
		}
	}
	
	@Override
	public void setFocus() {
		page.setFocus();
	}
}
