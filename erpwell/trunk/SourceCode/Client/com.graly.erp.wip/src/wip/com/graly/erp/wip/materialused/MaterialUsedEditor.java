package com.graly.erp.wip.materialused;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MaterialUsedEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.wip.materialused.MaterialUsedEditor";
	
	protected IFormPage page;
	
	public MaterialUsedEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new MaterialUsedEntryPage(this, "", "");
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
