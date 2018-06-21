package com.graly.erp.xz.pur.request;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * �������üƻ�--�뿪�ܲɹ�����һ��
 * */
public class XZRequisitionEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.xz.pur.request.XZRequisitionEditor";
	
	protected IFormPage page;
	
	public XZRequisitionEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new XZRequisitionEntryPage(this, "", "");
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
