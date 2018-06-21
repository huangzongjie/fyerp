package com.graly.erp.vdm.materialassessment;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MaterialAssEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.vdm.materialassessment.MaterialAssEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MaterialAssEntryPage(this, "", "");
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
