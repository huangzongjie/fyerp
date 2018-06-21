package com.graly.erp.wip.workcenter.schedule.purchase;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Storage;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.RepScheResult;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADUser;
import com.graly.mes.wip.client.WipManager;



public class PurchaseMaterialSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(PurchaseMaterialSection.class);
	public static final String TABLE_NAME_PR_LINE = "WorkCenterRequisitionLine";
	public static final String TABLE_NAME_PO_LINE = "WorkCenterPurchaseOrderLine";
	public static final String TABLE_NAME_MO_LINE = "WIPManufactureOrderLine";	
	protected ToolItem itemBarcode;
	protected RepScheResult selectedLine;
	protected ToolItem itemRunTotal;//领用单
	protected ToolItem itemPreview;
	protected ToolItem itemNote;
	protected Label labe;
	public PurchaseMaterialSection(EntityTableManager tableManager) {
		super(tableManager);
//		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemModify(tBar);
		createToolItemPreview(tBar);
		createToolRunTotal(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemModify(ToolBar tBar) {
//		String authorityToolItem = "Alarm.Iqc.Note";
//		itemNote = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemNote  = new ToolItem(tBar, SWT.PUSH);
		itemNote.setText("备注");
		itemNote.setImage(SWTResourceCache.getImage("save"));
		itemNote.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				modifyAdapter();
			}
		});
	}
	
	protected void createToolRunTotal(ToolBar tBar) {
		itemRunTotal = new ToolItem(tBar, SWT.PUSH);
		itemRunTotal.setText("重新运算库存");
		itemRunTotal.setImage(SWTResourceCache.getImage("barcode"));
		itemRunTotal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				runTotalAdapter();
			}
		});
	}
	
	protected void createToolItemPreview(ToolBar tBar) {
		itemPreview = new ToolItem(tBar, SWT.PUSH);
		itemPreview.setText("详细信息");
		itemPreview.setImage(SWTResourceCache.getImage("print"));
		itemPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewAdapter();
			}
		});
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionLine(ss.getFirstElement());
	    		try {
					if(selectedLine != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						ADTable prTable = adManager.getADTable(0L, TABLE_NAME_PR_LINE);
						prTable = adManager.getADTableDeep(prTable.getObjectRrn());
						ADTable poTable = adManager.getADTable(0L, TABLE_NAME_PO_LINE);
						poTable = adManager.getADTableDeep(poTable.getObjectRrn());
						
						Long materialRrn = selectedLine.getMaterialRrn();
						String prWhereClause = "qty-(case when qtyOrdered is null then 0 else qtyOrdered end) >0";
						String poWhereClause = "qty-(case when qtyIn is null then 0 else qtyIn end) >0";

						PurAssociatedDialog cd = new PurAssociatedDialog(UI.getActiveShell(),
								prTable, poTable, materialRrn,prWhereClause,poWhereClause);
						if(cd.open() == Dialog.CANCEL) {
						}
					}
				} catch(Exception e) {
					ExceptionHandlerManager.asyncHandleException(e);
				}
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionLine(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	private void setSelectionLine(Object obj) {
		if(obj instanceof RepScheResult) {
			selectedLine = (RepScheResult)obj;
		} else {
			selectedLine = null;
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	protected void refreshSection() {
		refresh();
	}
	@Override
	protected void createSectionTitle(Composite client) {
		 labe =new Label(client, 0);
		 labe.setText("最近统计时间："+"					");
		 setStartTime();
	}
	
	public void setStartTime(){
		 ADManager adManager;
			try {
				adManager = Framework.getService(ADManager.class);
				 List<PasErrorLog> logs = adManager.getEntityList(Env.getOrgRrn(), PasErrorLog.class,Integer.MAX_VALUE,"pasType='SCHEDULE'","errDate DESC");
				 if(logs!=null && logs.size()>0){
					 Date date =logs.get(0).getErrDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String scheduleDate = sdf.format(date);
					 labe.setText("最近统计时间："+scheduleDate);
					 labe.pack();
				 }else{
					 labe.setText("最近统计时间："+"					");
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	protected void runTotalAdapter() {
		try{
			//审核并且统计过采购情况并且没有生成出库单
			WipManager wipManager = Framework.getService(WipManager.class);
			wipManager.runSchedulePurchase(Env.getOrgRrn(),Env.getUserRrn());
			UI.showInfo("操作成功");
			setStartTime();
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	protected void previewAdapter() {
		try {
			String report = "sche_report.rptdesign";
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(selectedLine == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = selectedLine.getObjectRrn();
			userParams.put("SCHE_OBJECT_RRN", String.valueOf(objectRrn));

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void modifyAdapter() {
		if (selectedLine != null) {
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable adTable = adManager.getADTable(0L, "PurchaseMaterialNote");
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
				PurchaseMaterialModifyDialog alarmModifyDialog = new PurchaseMaterialModifyDialog(UI.getActiveShell(),selectedLine,adTable);
				if(alarmModifyDialog.open() == Dialog.OK){
					RepScheResult repScheResult = alarmModifyDialog.getRepScheResult();
					if(repScheResult!=null){
						adManager.saveEntity(repScheResult, Env.getUserRrn());
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
}
