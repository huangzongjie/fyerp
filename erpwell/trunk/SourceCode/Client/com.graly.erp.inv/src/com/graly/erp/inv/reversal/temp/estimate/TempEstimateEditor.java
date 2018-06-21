package com.graly.erp.inv.reversal.temp.estimate;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class TempEstimateEditor extends EntityEditor {

	public static final String EDITOR_ID = "com.graly.erp.inv.reversal.temp.estimate.TempEstimateEditor";
	public Logger logger = Logger.getLogger(TempEstimateEditor.class);
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new TempEstimateEntryPage(this, "", "");
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
