package com.graly.erp.wip.workcenter.schedule.purchase3;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
*
*����--���̶�����
*��ԭ����ĩ�������ĩ����1709��ȡ���ϼ�
*�����Ǽƻ��ų���
 * */
public class PurchaseMaterialEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter.schedule.purchase3.PurchaseMaterialEditor";
	
	protected IFormPage page;
	

	@Override
	protected void addPages() {
		try {
			page = new PurchaseMaterialEntryPage(this, "", "");
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
