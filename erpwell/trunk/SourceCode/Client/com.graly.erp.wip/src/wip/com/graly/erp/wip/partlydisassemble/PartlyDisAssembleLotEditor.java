package com.graly.erp.wip.partlydisassemble;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class PartlyDisAssembleLotEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.partlydisassemble.PartlyDisAssembleLotEditor";
	
	protected IFormPage page;
	
	public PartlyDisAssembleLotEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new PartlyDisAssembleLotEntryPage(this, "", "");
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
