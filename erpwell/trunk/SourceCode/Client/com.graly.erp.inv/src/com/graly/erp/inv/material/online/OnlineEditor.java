package com.graly.erp.inv.material.online;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
 * @author Administrator
 *  物料统计查询功能模块
 */
public class OnlineEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.material.online.OnlineEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new OnlineEntryPage(this, "", "");
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
