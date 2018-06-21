package com.graly.erp.inv.aging;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * ��������ѯ
 * ʵ�ַ�ʽ���洢����SP_AGING,SP_AGING_COUNT
 * */
public class AgingEntityEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.aging.AgingEntityEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new AgingEntryPage(this, "", "");
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
