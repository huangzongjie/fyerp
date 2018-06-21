package com.graly.erp.wip.mo.molinedelay;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
/**
 * 由于现在“订单延误原因”由于各种原因无法及时汇总，我们现在想一个在ERP上做汇总的想法，你看一下是否行得通。
*1、在接收日期超过交货日期3天（不包含3天）的商品；
*2、在工作看板上点接收，跳出“接收数量设置”，在点确定时，假如接收日期超过交货期3天以上，则跳出另一个简单页面“选择订单延误原因”，不填则无法跳过“接收数量设置”页面，如填好，则跳至“接收已完成物料”页面；如在3天以内，则直接跳到“接收已完成物料”页面。
*3、在“生产管理”大项中做一个小项，汇总“订单延误汇总”，包括工作令、物料编号、物料名称、数量、计划交期、实际完成日期、延误天数、延误原因。
*4、延误原因：1）采购物料；2）业务更改；3）产能不足；4）自制物料
**/
public class MoLineDelayQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.wip.mo.molinedelay.MoLineDelayQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MoLineDelayQueryEntryPage(this, "", "");
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
