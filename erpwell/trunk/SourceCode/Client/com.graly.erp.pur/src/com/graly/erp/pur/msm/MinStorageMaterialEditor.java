package com.graly.erp.pur.msm;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
 * 最低库存物料查询、合并成采购订单模块
 */
public class MinStorageMaterialEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.pur.msm.MinStorageMaterialEditor";
	
	protected IFormPage page;
	
	public MinStorageMaterialEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new MinStorageMaterialPage(this, "", "");
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
