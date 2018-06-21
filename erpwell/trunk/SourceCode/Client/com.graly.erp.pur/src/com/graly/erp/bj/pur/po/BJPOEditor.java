package com.graly.erp.bj.pur.po;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * �����ɹ�����(��ֲ�ڿ��ܻ����ɹ���������)
 * ֻ����˹���������������
 * 
 * */
public class BJPOEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.bj.pur.po.BJPOEditor";
	
	protected IFormPage page;
	
	public BJPOEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new BJPOEntryPage(this, "", "");
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
