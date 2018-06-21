package com.graly.erp.inv.racklot;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class RackLotEditor extends EntityEditor {
	private static final Logger logger = Logger.getLogger(RackLotEditor.class);

	public static final String EDITOR_ID = "com.graly.erp.inv.racklot.RackLotEditor";
	
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new RackLotEntryPage(this, "", "");
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
