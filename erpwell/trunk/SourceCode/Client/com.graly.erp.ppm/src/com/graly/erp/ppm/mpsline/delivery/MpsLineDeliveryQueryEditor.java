package com.graly.erp.ppm.mpsline.delivery;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
*主计划通知详细信息
*
**/
public class MpsLineDeliveryQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.ppm.mpsline.delivery.MpsLineDeliveryQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MpsLineDeliveryQueryEntryPage(this, "", "");
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
