package com.graly.erp.ppm.mps.delivery;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * 主计划通知信息
 * */
public class MpsDeliveryQueryEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.ppm.mps.delivery.MpsDeliveryQueryEditor";
	
	protected IFormPage page;
	
	public MpsDeliveryQueryEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new MpsDeliveryQueryEntryPage(this, "", "");
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
