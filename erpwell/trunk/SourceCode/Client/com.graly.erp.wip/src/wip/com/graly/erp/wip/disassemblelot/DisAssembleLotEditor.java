package com.graly.erp.wip.disassemblelot;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class DisAssembleLotEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.disassemblelot.DisAssembleLotEditor";
	
	protected IFormPage page;
	
	public DisAssembleLotEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new DisAssembleLotEntryPage(this, "", "");
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
