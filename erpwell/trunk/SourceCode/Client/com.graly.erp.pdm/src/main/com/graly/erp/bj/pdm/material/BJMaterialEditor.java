package com.graly.erp.bj.pdm.material;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * ������������
 * */
public class BJMaterialEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.bj.pdm.material.BJMaterialEditor";
	
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new BJMaterialEntryPage(this, "", "");
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