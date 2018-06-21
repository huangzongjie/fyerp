package com.graly.erp.bj.inv.spares;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * ������ά�޵��豸����;ͳ��ԭ�������������
 * */
public class BJSparesEditor extends EntityEditor {

	public static final String EDITOR_ID = "com.graly.erp.bj.inv.spares.BJSparesEditor";
	public Logger logger = Logger.getLogger(BJSparesEditor.class);
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new BJSparesEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			logger.error(e);
		}
	}
	
	@Override
	public void setFocus() {
		page.setFocus();
	}

}
