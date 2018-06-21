package com.graly.erp.xz.inv.in;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
 * 行政采购入库
 * */
public class XZInEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.xz.inv.in.XZInEditor";
	
	protected IFormPage page;
	
	public XZInEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new XZInEntryPage(this, "", "");
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