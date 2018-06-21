package com.graly.erp.inv.aging;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.material.EntityQueryDialog4WC;
import com.graly.erp.inv.material.online.OnlineErrorDialog;
import com.graly.erp.inv.material.online.QueryProgressMonitorDialog;
import com.graly.erp.inv.model.MaterialAging;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class AgingSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(AgingSection.class);
	protected ToolItem itemKNzzReport;
	protected ToolItem itemKNzcReport;
	protected ToolItem itemBTzzReport;
	protected ToolItem itemBTzcReport;
	protected EntityTableManager knTableManager;
	protected static String TABLE_NAME="INVKnAging";
	public AgingSection() {
		super();
	}

	public AgingSection(EntityTableManager tableListManager) {
		super(tableListManager);
		knTableManager = new EntityTableManager(getADTableOf());
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemExport(tBar);
//		createToolItemSearch(tBar);
		createToolItemKNzcReport(tBar);
		createToolItemKNzzReport(tBar);
		createToolItemBTzcReport(tBar);
		createToolItemBTzzReport(tBar);
		createToolItemBLklReport(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemKNzcReport(ToolBar tBar) {
		itemBTzcReport = new ToolItem(tBar, SWT.PUSH);
		itemBTzcReport.setText("开能总仓库龄查询");
		itemBTzcReport.setImage(SWTResourceCache.getImage("search"));
		itemBTzcReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				knzcReportAdapter();
			}
		});
	}
	
	protected void createToolItemKNzzReport(ToolBar tBar) {
		itemKNzzReport = new ToolItem(tBar, SWT.PUSH);
		itemKNzzReport.setText("开能制造库龄查询");
		itemKNzzReport.setImage(SWTResourceCache.getImage("search"));
		itemKNzzReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				knzzReportAdapter();
			}
		});
	}
	
	protected void createToolItemBTzcReport(ToolBar tBar) {
		itemKNzcReport = new ToolItem(tBar, SWT.PUSH);
		itemKNzcReport.setText("奔泰总仓库龄查询");
		itemKNzcReport.setImage(SWTResourceCache.getImage("search"));
		itemKNzcReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				btzcReportAdapter();
			}
		});
	}
	
	protected void createToolItemBTzzReport(ToolBar tBar) {
		itemBTzzReport = new ToolItem(tBar, SWT.PUSH);
		itemBTzzReport.setText("奔泰制造库龄查询");
		itemBTzzReport.setImage(SWTResourceCache.getImage("search"));
		itemBTzzReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				btzzReportAdapter();
			}
		});
	}
	
	protected void createToolItemBLklReport(ToolBar tBar) {
		itemBTzcReport = new ToolItem(tBar, SWT.PUSH);
		itemBTzcReport.setText("壁炉库龄查询");
		itemBTzcReport.setImage(SWTResourceCache.getImage("search"));
		itemBTzcReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				blReportAdapter();
			}
		});
	}
	

	
	protected void knzcReportAdapter() {
		AgingKNzcDialog queryDialog =  new AgingKNzcDialog(UI.getActiveShell(), knTableManager, this);
		queryDialog.open();
	}
	protected void knzzReportAdapter() {
		AgingKNzzDialog queryDialog =  new AgingKNzzDialog(UI.getActiveShell(), knTableManager, this);
		queryDialog.open();
	}
	protected void btzcReportAdapter() {
		AgingBTzcDialog queryDialog =  new AgingBTzcDialog(UI.getActiveShell(), tableManager, this);
		queryDialog.open();
	}
	protected void btzzReportAdapter() {
		AgingBTzzDialog queryDialog =  new AgingBTzzDialog(UI.getActiveShell(), tableManager, this);
		queryDialog.open();
	}
	protected void blReportAdapter() {
		AgingBLDialog queryDialog =  new AgingBLDialog(UI.getActiveShell(), knTableManager, this);
		queryDialog.open();
	}
	
	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog4WC(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	public void refresh(){
		viewer.setInput(null);	
		// 调用供应商评估方法
//		try{
//			if(queryDialog!=null){
//				Map<String, Object> keys = queryDialog.getQueryKeys();
//				for(String name : keys.keySet()){
//					if("approvedDate".equals(name)){
//						Map approvedDate = (Map) keys.get(name);
//						Date fromDate = null;
//						Date toDate = null;
//						if(approvedDate != null){
//							fromDate = (Date) approvedDate.get(FromToCalendarField.DATE_FROM);
//							toDate = (Date) approvedDate.get(FromToCalendarField.DATE_TO);
//							INVManager invManager;
////							try {
//							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//							String from = df.format(fromDate);
//							String to = df.format(toDate);
//								invManager = Framework.getService(INVManager.class);
//								List<MaterialAging> maas = invManager.getMaterialAgingBT(Env.getOrgRrn(), "商品", from, to);
//								this.viewer.setInput(maas);
////							} catch (Exception e) {
////								// TODO Auto-generated catch block
////								e.printStackTrace();
////							}
//							
//						}
//					}
//				}
//			}
			
//			List<MaterialSum> input = new ArrayList<MaterialSum>();
//			List<Material> errorMaterials = new ArrayList<Material>();
//			if(materialRrn != null) {
//				WipManager wipManager = Framework.getService(WipManager.class);
//				MaterialSum ms = wipManager.getMaterialSum (Env.getOrgRrn(), materialRrn, false, false);
////				MaterialSum ms = wipManager.getMaterialSum2(Env.getOrgRrn(), materialRrn, false, false,false);
//				if(ms == null) {
//					UI.showWarning(getMessageInfo(null));
//				} else {
//					input.add(ms);
//				}
//			}
//			else {
//				QueryProgressMonitorDialog progressDiglog = new QueryProgressMonitorDialog(
//						UI.getActiveShell(), "");
//				if(materials != null && materials.size() > 0) {
//					progressDiglog.run(true, true, progressDiglog.createProgress(materials));
//				}
//				else if(isQueryAll){
//					progressDiglog.run(true, true, progressDiglog.createProgress());
//				}
//				input = progressDiglog.getMaterialSums();
//				errorMaterials = progressDiglog.getErrorMaterials();
//				List<Material> unQuerys = progressDiglog.getUnQueryMaterials();
//				if(unQuerys != null && unQuerys.size() > 0) {
//					UI.showWarning(getMessageInfo(unQuerys));
//				}
//			}
//			viewer.setInput(input);
//			listTableManager.updateView(viewer);
//			createSectionDesc(section);
//			//添加弹出错误对话框显示错误信息
//			if(errorMaterials!=null && errorMaterials.size() >0 ){
//				OnlineErrorDialog errorDialog = new OnlineErrorDialog(UI.getActiveShell(), null, null, errorMaterials);
//				if(errorDialog.open() == Dialog.OK ){
//				}
//			}
//		} catch (Exception e){
//			logger.error("EntityBlock : refresh()  ", e);
//			ExceptionHandlerManager.asyncHandleException(e);
//		}
	}
	
	protected ADTable getADTableOf() {
		try {
			ADTable adTable =null;
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
			return adTable;
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
}
