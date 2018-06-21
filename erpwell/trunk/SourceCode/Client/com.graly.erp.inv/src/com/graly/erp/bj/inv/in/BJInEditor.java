package com.graly.erp.bj.inv.in;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
 * �����ɹ����
 * */
public class BJInEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.bj.inv.in.BJInEditor";
	
	protected IFormPage page;
	
	public BJInEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new BJInEntryPage(this, "", "");
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