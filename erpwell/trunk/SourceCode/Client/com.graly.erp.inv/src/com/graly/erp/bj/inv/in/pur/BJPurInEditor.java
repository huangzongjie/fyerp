package com.graly.erp.bj.inv.in.pur;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * �����ɹ���⣺
 * �˽�Ҫ�������һ�����������ʱ�ļ۸񱣴�2λ����
 * com.graly.erp.bj.inv.in.BJInEditor������Էϳ�
 * */
public class BJPurInEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.bj.inv.in.pur.BJPurInEditor";
	
	protected IFormPage page;
	
	public BJPurInEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new BJPurInEntryPage(this, "", "");
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