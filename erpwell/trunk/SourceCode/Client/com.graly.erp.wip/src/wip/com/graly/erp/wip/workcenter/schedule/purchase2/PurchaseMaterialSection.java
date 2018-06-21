package com.graly.erp.wip.workcenter.schedule.purchase2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.wip.model.MaterialComment;
import com.graly.erp.wip.model.RepScheResult2;
import com.graly.erp.wip.model.VRepScheResult;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;



public class PurchaseMaterialSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(PurchaseMaterialSection.class);
	public static final String TABLE_NAME_PR_LINE = "WorkCenterRequisitionLine";
	public static final String TABLE_NAME_PO_LINE = "WorkCenterPurchaseOrderLine";
	public static final String TABLE_NAME_MO_LINE = "WIPManufactureOrderLine";	
	protected ToolItem itemBarcode;
//	protected RepScheResult2 selectedLine;
	protected ToolItem itemRunTotal;//领用单
	protected ToolItem itemPreview;
	protected ToolItem itemNote;
	protected ToolItem itemCompare;
	protected Label labe;
	private ToolItem itemComments;
	private VRepScheResult selectedRepScheResult;
	protected EntityQueryDialog queryDialog;
	public PurchaseMaterialSection(EntityTableManager tableManager,PurchaseMaterialMainSection mainSection) {
		super(tableManager);
//		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemCompare(tBar);
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
	
	protected void createToolItemCompare(ToolBar tBar) {
//		String authorityToolItem = "Alarm.Iqc.Note";
//		itemNote = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemCompare  = new ToolItem(tBar, SWT.PUSH);
		itemCompare.setText("对比");
		itemCompare.setImage(SWTResourceCache.getImage("save"));
		itemCompare.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				compareAdapter();
			}
		});
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
				saveCommentsAdapter();
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
	
	private void createToolItemComments(ToolBar tBar) {
		itemComments = new ToolItem(tBar, SWT.PUSH);
		itemComments.setText("备注");
		itemComments.setImage(SWTResourceCache.getImage("save"));
		itemComments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveCommentsAdapter();
			}
		});
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionRepScheResultl(ss.getFirstElement());
	    		try {
					if(selectedRepScheResult != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						ADTable prTable = adManager.getADTable(0L, TABLE_NAME_PR_LINE);
						prTable = adManager.getADTableDeep(prTable.getObjectRrn());
						ADTable poTable = adManager.getADTable(0L, TABLE_NAME_PO_LINE);
						poTable = adManager.getADTableDeep(poTable.getObjectRrn());
						
						Long materialRrn = selectedRepScheResult.getMaterialRrn();
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
					setSelectionRepScheResultl(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
//	private void setSelectionLine(Object obj) {
//		if(obj instanceof RepScheResult2) {
//			selectedLine = (RepScheResult2)obj;
//		} else {
//			selectedLine = null;
//		}
//	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new PurQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	protected void refreshSection() {
		refresh();
		compareWithRelValue();
	}
	@Override
	protected void createSectionTitle(Composite client) {
//		 labe =new Label(client, 0);
//		 labe.setText("最近统计时间："+"					");
//		 setStartTime();
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
			wipManager.runSchedulePurchase2(Env.getOrgRrn(),Env.getUserRrn());
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

			if(selectedRepScheResult == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = selectedRepScheResult.getObjectRrn();
			userParams.put("SCHE_OBJECT_RRN", String.valueOf(objectRrn));

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void saveCommentsAdapter() {
		if (selectedRepScheResult != null) {
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable adTable = adManager.getADTable(0L, "MaterialComment");
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
				List<MaterialComment> scheResults= adManager.getEntityList(Env.getOrgRrn(),MaterialComment.class,Integer.MAX_VALUE,"materialId ='"+selectedRepScheResult.getMaterialId()+"'",null);
				MaterialComment mc=null;
				if(scheResults!=null && scheResults.size()>0){
					mc = scheResults.get(0);
				}else{
					mc = new MaterialComment();
					mc.setMaterialId(selectedRepScheResult.getMaterialId());
					
				}
				PurchaseMaterialModifyDialog alarmModifyDialog = new PurchaseMaterialModifyDialog(UI.getActiveShell(),mc,adTable);
				if(alarmModifyDialog.open() == Dialog.OK){
					mc = alarmModifyDialog.getMc();
					adManager.saveEntity(mc, Env.getUserRrn());
					refresh();
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
	protected void compareAdapter() {
		
		String tableName = "RepScheResultCompare";
		String tableName2 = "RepScheResultHis";
		ADTable invAdTable = null;
		ADTable invAdTable2 = null;
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			invAdTable = adManager.getADTable(0L, tableName);
			invAdTable2 = adManager.getADTable(0L, tableName2);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		InvDialog invDialog = new InvDialog(UI.getActiveShell());
//		if(invDialog.open() == IDialogConstants.OK_ID){
//			
//		}
		
		TableListManager listTableManager = new TableListManager(invAdTable);
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
		| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		
		TableListManager listTableManager2 = new TableListManager(invAdTable2);

		
		PurchaseCompareQueryDialog invDialog = new PurchaseCompareQueryDialog(listTableManager, null, null, style,listTableManager2);
		if(invDialog.open() == IDialogConstants.OK_ID){
			
		}
	}
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		super.refresh();
		compareWithRelValue();
	}
	public void compareWithRelValue() {
		if (viewer instanceof TableViewer) {
			TableViewer tViewer = (TableViewer) viewer;
			Table table = tViewer.getTable();

			for (TableItem item : table.getItems()) {
				Object obj = item.getData();
				if (obj instanceof VRepScheResult) {
					VRepScheResult vc = (VRepScheResult) obj;
					BigDecimal oneQty = vc.getOneQty()!=null?vc.getOneQty():BigDecimal.ZERO;
					BigDecimal twoQty = vc.getTwoQty()!=null?vc.getTwoQty():BigDecimal.ZERO;
					BigDecimal subQty1 = vc.getSubQty1()!=null?vc.getSubQty1():BigDecimal.ZERO;
					BigDecimal subQty2= vc.getSubQty2()!=null?vc.getSubQty2():BigDecimal.ZERO;
					//如果定义了参考价格并且实际采购价高于参考价用红色反显
					if (oneQty.compareTo(subQty1)!=0) {
						item.setBackground(new Color(null, 255, 0, 0));
					}
					if (twoQty.compareTo(subQty2)!=0) {
						item.setBackground(new Color(null, 255, 0, 0));
					}
				}
			}
		}
	}
	
	
	 
	protected void setSelectionRepScheResultl(Object obj) {
		if(obj instanceof VRepScheResult) {
			selectedRepScheResult = (VRepScheResult)obj;
		} else {
			selectedRepScheResult = null;
		}
	}
}
