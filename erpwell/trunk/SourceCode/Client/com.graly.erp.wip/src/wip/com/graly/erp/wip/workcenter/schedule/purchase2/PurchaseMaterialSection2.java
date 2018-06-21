package com.graly.erp.wip.workcenter.schedule.purchase2;

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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.wip.model.MaterialComment;
import com.graly.erp.wip.model.PmcZzjResult;
import com.graly.erp.wip.model.VRepScheResult2;
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



public class PurchaseMaterialSection2 extends MasterSection {
	private static final Logger logger = Logger.getLogger(PurchaseMaterialSection2.class);
	public static final String TABLE_NAME_PR_LINE = "WorkCenterRequisitionLine";
	public static final String TABLE_NAME_PO_LINE = "WorkCenterPurchaseOrderLine";
	public static final String TABLE_NAME_MO_LINE = "WIPManufactureOrderLine";	
	protected ToolItem itemNote;
	protected ToolItem itemCompare;
	protected ToolItem itemProblem;
	protected Label labe;
	private ToolItem itemComments;
	private VRepScheResult2 selectedRepScheResult;
	protected EntityQueryDialog queryDialog;
	public PurchaseMaterialSection2(EntityTableManager tableManager,PurchaseMaterialMainSection2 mainSection) {
		super(tableManager);
//		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemProblem(tBar);
		createToolItemModify(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemProblem(ToolBar tBar) {
		itemCompare  = new AuthorityToolItem(tBar, SWT.PUSH,"INV.Purchase2.Setup");
		itemCompare.setText("设置提醒");
		itemCompare.setImage(SWTResourceCache.getImage("save"));
		itemCompare.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveProblemAdapter();
			}
		});
	}
//	protected void createToolItemCompare(ToolBar tBar) {
////		String authorityToolItem = "Alarm.Iqc.Note";
////		itemNote = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
//		itemCompare  = new ToolItem(tBar, SWT.PUSH);
//		itemCompare.setText("对比");
//		itemCompare.setImage(SWTResourceCache.getImage("save"));
//		itemCompare.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				compareAdapter();
//			}
//		});
//	}
	
	protected void createToolItemModify(ToolBar tBar) {
//		String authorityToolItem = "Alarm.Iqc.Note";
//		itemNote = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemNote  = new AuthorityToolItem(tBar, SWT.PUSH,"INV.Purchase2.Note");
		itemNote.setText("备注");
		itemNote.setImage(SWTResourceCache.getImage("save"));
		itemNote.addSelectionListener(new SelectionAdapter() {
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
	
	protected void saveProblemAdapter() {
		if (selectedRepScheResult != null) {
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable adTable = adManager.getADTable(0L, "PmcPurResult");
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
				List<VRepScheResult2> scheResults= adManager.getEntityList(Env.getOrgRrn(),VRepScheResult2.class,Integer.MAX_VALUE,"materialId ='"+selectedRepScheResult.getMaterialId()+"'",null);
				VRepScheResult2 mc=null;
				if(scheResults!=null && scheResults.size()>0){
					mc = scheResults.get(0);
				}else{
					mc = new VRepScheResult2();
					mc.setMaterialId(selectedRepScheResult.getMaterialId());
					
				}
				PurchaseMaterialModifyDialog2 alarmModifyDialog = new PurchaseMaterialModifyDialog2(UI.getActiveShell(),mc,adTable);
				if(alarmModifyDialog.open() == Dialog.OK){
					mc = alarmModifyDialog.getMc();
					PmcZzjResult pr = new PmcZzjResult();
					pr.setObjectRrn(mc.getObjectRrn());
					pr= (PmcZzjResult) adManager.getEntity(pr);
					pr.setHasProblem(mc.getHasProblem());
					adManager.saveEntity(pr, Env.getUserRrn());
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

//			for (TableItem item : table.getItems()) {
//				Object obj = item.getData();
//				if (obj instanceof VRepScheResult) {
//					VRepScheResult vc = (VRepScheResult) obj;
//					BigDecimal oneQty = vc.getOneQty()!=null?vc.getOneQty():BigDecimal.ZERO;
//					BigDecimal twoQty = vc.getTwoQty()!=null?vc.getTwoQty():BigDecimal.ZERO;
//					BigDecimal subQty1 = vc.getSubQty1()!=null?vc.getSubQty1():BigDecimal.ZERO;
//					BigDecimal subQty2= vc.getSubQty2()!=null?vc.getSubQty2():BigDecimal.ZERO;
//					//如果定义了参考价格并且实际采购价高于参考价用红色反显
//					if (oneQty.compareTo(subQty1)!=0) {
//						item.setBackground(new Color(null, 255, 0, 0));
//					}
//					if (twoQty.compareTo(subQty2)!=0) {
//						item.setBackground(new Color(null, 255, 0, 0));
//					}
//				}
//			}
		}
	}
	
	
	 
	protected void setSelectionRepScheResultl(Object obj) {
		if(obj instanceof VRepScheResult2) {
			selectedRepScheResult = (VRepScheResult2)obj;
		} else {
			selectedRepScheResult = null;
		}
	}
}
