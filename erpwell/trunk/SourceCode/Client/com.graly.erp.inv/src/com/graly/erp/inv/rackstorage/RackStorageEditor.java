package com.graly.erp.inv.rackstorage;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class RackStorageEditor extends EntityEditor {
	private static final Logger logger = Logger.getLogger(RackStorageEditor.class);

	public static final String EDITOR_ID = "com.graly.erp.inv.rackstorage.RackStorageEditor";
	
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new RackStorageEntryPage(this, "", "");
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
