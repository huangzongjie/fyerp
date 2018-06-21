package com.graly.erp.pur.receipt.date;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * 乔艳明需求(显示没收进行收货的采购订单行)
 * 1.收货时间必须有值
 * 2.没有生成收货单
 * */
public class POReceiptDateQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.pur.receipt.date.POReceiptDateQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new POReceiptDateQueryEntryPage(this, "", "");
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
