package com.graly.erp.pdm.batchbom;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class BatchBomEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.pdm.batchbom.BatchBomEditor";
	
	@Override
	protected void addPages(){
		try {
			IFormPage page = new BatchBomEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
