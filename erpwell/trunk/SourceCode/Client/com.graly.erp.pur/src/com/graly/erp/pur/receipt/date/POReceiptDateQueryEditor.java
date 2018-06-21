package com.graly.erp.pur.receipt.date;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * ����������(��ʾû�ս����ջ��Ĳɹ�������)
 * 1.�ջ�ʱ�������ֵ
 * 2.û�������ջ���
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
