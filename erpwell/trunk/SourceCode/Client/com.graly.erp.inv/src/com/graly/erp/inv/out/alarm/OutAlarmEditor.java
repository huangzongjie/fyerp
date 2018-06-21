package com.graly.erp.inv.out.alarm;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/*
 * 销售出库提醒,
 * 销售出库功能的优化
 * 显示所有同意并且出库单为空的销售订单
 * */
public class OutAlarmEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.out.alarm.OutAlarmEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new OutAlarmEntryPage(this, "", "");
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
