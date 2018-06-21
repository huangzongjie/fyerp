package com.graly.erp.xz.inv.outother;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * �������ù���
 * �ù����뿪�ܵ��������⹦��һ�¡�������һЩ���ƻ��Ķ���
 * */
public class XZOtherOutEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.xz.inv.outother.XZOtherOutEditor";
	
	protected IFormPage page;
	
	public XZOtherOutEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new XZOtherOutEntryPage(this, "", "");
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
