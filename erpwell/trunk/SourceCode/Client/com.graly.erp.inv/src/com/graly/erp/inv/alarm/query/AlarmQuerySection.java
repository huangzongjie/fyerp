package com.graly.erp.inv.alarm.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.in.createfrom.iqc.CreateContext;
import com.graly.erp.inv.in.createfrom.iqc.CreateDialog;
import com.graly.erp.inv.iqc.IqcLineDialog;
import com.graly.erp.inv.iqc.createfrom.CreateIqcContext;
import com.graly.erp.inv.iqc.createfrom.CreateIqcDialog;
import com.graly.erp.inv.model.AlarmData;
import com.graly.erp.inv.model.AlarmTarget;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.VAlarmData;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
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

public class AlarmQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(AlarmQuerySection.class);

	protected ToolItem itemNote;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;

	protected AlarmData alarmData;
	protected TableListManager listTableManager;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public AlarmQuerySection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionAlarmData(ss.getFirstElement());
				if(alarmData!=null && AlarmTarget.TARGET_TYPE_IQC.equals(alarmData.getAlarmType())){
					openIQCDialog();
				}else if(alarmData!=null && AlarmTarget.TARGET_TYPE_WAREHOUSE.equals(alarmData.getAlarmType())){
					openWAREHOUSEDialog();
				}
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionAlarmData(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemModify(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemClose(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemModify(ToolBar tBar) {
		String authorityToolItem = "Alarm.Iqc.Note";
		itemNote = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemNote.setText("备注");
		itemNote.setImage(SWTResourceCache.getImage("save"));
		itemNote.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				modifyAdapter();
			}
		});
	}
	
	protected void createToolItemClose(ToolBar tBar) {
		String authorityToolItem = "Alarm.Iqc.Close";
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemDelete.setText("关闭警报");
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closeAdapter();
			}
		});
	}

	protected void modifyAdapter() {
		if (alarmData != null) {
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable adTable = adManager.getADTable(0L, "AlarmDataClose");
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
				AlarmModifyDialog alarmModifyDialog = new AlarmModifyDialog(UI.getActiveShell(),alarmData,adTable);
				if(alarmModifyDialog.open() == Dialog.OK){
					AlarmData alarmData = alarmModifyDialog.getAlarmData();
					if(alarmData.getField21()!=null){
						String field21 = alarmData.getField21();//提醒的备注信息
						alarmData = (AlarmData) adManager.getEntity(alarmData);
						alarmData.setField21(field21);
						adManager.saveEntity(alarmData, Env.getUserRrn());
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}

	protected void closeAdapter() {
		if (alarmData != null) {
			try {
				boolean confirmDelete = UI.showConfirm("是否关闭警报");
				if (confirmDelete) {
					if (alarmData.getObjectRrn() != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						alarmData = (AlarmData) adManager.getEntity(alarmData);
						alarmData.setUpdated(Env.getSysDate());
						alarmData.setUpdatedBy(Env.getOrgRrn());
						alarmData.setStatus(AlarmData.STATUS_CLOSE);
						alarmData.setCloser(Env.getUserRrn());
						adManager.saveEntity(alarmData,Env.getUserRrn() );
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}

	
	
	@Override
	public void refresh() {
//		List<AlarmData> showAlarmDatas = new ArrayList<AlarmData>();//界面上显示的AlarmData数据
		List<VAlarmData> alarmDatas = null;
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			alarmDatas = adManager.getEntityList(Env.getOrgRrn(), VAlarmData.class,Integer.MAX_VALUE,
					getWhereClause()+" and status ='OPEN' and alarmType='IQC' ","field1 desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		for(int a=0;a<alarmDatas.size();a++){
//			AlarmData tempAlarmData = alarmDatas.get(a);
//			String[] field5 = null;
//			String[] field6 = null;
//			String[] field8 = null;
//			String[] field25 =null;
//			
//			if(tempAlarmData.getField5()!=null){
//				field5 = tempAlarmData.getField5().split(";");	
//			}
//			if(tempAlarmData.getField6()!=null){
//				field6 = tempAlarmData.getField6().split(";");
//			}
//			if(tempAlarmData.getField8()!=null){
//				field8 = tempAlarmData.getField8().split(";");
//			}
//			if(tempAlarmData.getField25()!=null){
//				field25 = tempAlarmData.getField25().split(";");
//			}
//			if(field5 !=null && field5.length>1){
//				for(int j=0;j<field5.length;j++){
//					AlarmData alarmData = new AlarmData();
//					alarmData.setObjectRrn(tempAlarmData.getObjectRrn());
//					alarmData.setOrgRrn(tempAlarmData.getOrgRrn());
//					alarmData.setOwner(tempAlarmData.getOwner());
//					alarmData.setAlarmType(tempAlarmData.getAlarmType());
//					alarmData.setField1(tempAlarmData.getField1());
//					alarmData.setField2(tempAlarmData.getField2());
//					alarmData.setField3(tempAlarmData.getField3());
//					alarmData.setField4(tempAlarmData.getField4());
//					if(field5!=null && field5[j]!=null){
//						alarmData.setField5(field5[j]);
//					}else{
//						alarmData.setField5(null);
//					}
//					if(field6!=null && field6[j]!=null){
//						alarmData.setField6(field6[j]);
//					}else{
//						alarmData.setField6(null);
//					}
//					if(field8!=null && field8[j]!=null){
//						alarmData.setField8(field8[j]);
//					}else{
//						alarmData.setField8(null);
//					}
//					if(field25!=null && field25[j]!=null){
//						alarmData.setField25(field25[j]);
//					}else{
//						alarmData.setField25(null);
//					}
//					alarmData.setField7(tempAlarmData.getField7());
//					alarmData.setField9(tempAlarmData.getField9());
//					alarmData.setField10(tempAlarmData.getField10());
//					alarmData.setField11(tempAlarmData.getField11());
//					alarmData.setField12(tempAlarmData.getField12());
//					alarmData.setField13(tempAlarmData.getField13());
//					alarmData.setField14(tempAlarmData.getField14());
//					alarmData.setField15(tempAlarmData.getField15());
//					alarmData.setField16(tempAlarmData.getField16());
//					alarmData.setField17(tempAlarmData.getField17());
//					alarmData.setField18(tempAlarmData.getField18());
//					alarmData.setField19(tempAlarmData.getField19());
//					alarmData.setField20(tempAlarmData.getField20());
//					alarmData.setField21(tempAlarmData.getField21());//质检备注
//					alarmData.setField22(tempAlarmData.getField22());//供应商编号
//					alarmData.setField26(tempAlarmData.getField26());//供应商名称
//					alarmData.setField27(tempAlarmData.getField27());
//					alarmData.setField28(tempAlarmData.getField28());
//					alarmData.setField29(tempAlarmData.getField29());
//					alarmData.setField30(tempAlarmData.getField30());
//					showAlarmDatas.add(alarmData);
//				}
//			}else{
//				showAlarmDatas.add(tempAlarmData);
//			}
//			
//		}
		viewer.setInput(alarmDatas);
		tableManager.updateView(viewer);
		this.createSectionDesc(alarmDatas);
		refreshSection();
	}

	protected void refreshSection() {
		TableViewer tViewer = (TableViewer) viewer;
		Table table = tViewer.getTable();
		table.addListener(SWT.MeasureItem, new Listener() {//行高加2倍，杨汉东纸质书写
			public void handleEvent(Event event) {
				event.height = event.gc.getFontMetrics().getHeight() * 2;
			}
		});
		
		for(int i =0;i<table.getItems().length;i++){
			TableItem item = table.getItems()[i];
			VAlarmData alarmData = (VAlarmData) item.getData();
			if(alarmData !=null && AlarmTarget.TARGET_TYPE_IQC.equals(alarmData.getAlarmType()) ){
				//超2天提醒为绿色，超3天为黄色，4天及4天以上为红色
				if(alarmData.getField13()!=null){
					long day1 = 86400000;//1天
					long day2 = 86400000*2;
					long day3 = 86400000*3;
					Date d1 = alarmData.getField13();
					Date d2 = Env.getSysDate();
					long diff = d2.getTime() - d1.getTime();
					
					if(diff >day1 && diff <=day2){
						Color greenColor = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
						item.setBackground(greenColor);
					}else if(diff >day2 && diff <=day3 ){
						Color yellowColor = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
						item.setBackground(yellowColor);
					}else if(diff >day3){
						Color redColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
						item.setBackground(redColor);
					}
				}
			}
		}
	}

	private void setSelectionAlarmData(Object obj) {
		if (obj instanceof VAlarmData) {
			VAlarmData vad = (VAlarmData) obj;
			AlarmData ad = new AlarmData();
			ad.setObjectRrn(vad.getObjectRrn());
			ad.setOrgRrn(vad.getOrgRrn());
			ADManager adManager;
			try {
				adManager = Framework.getService(ADManager.class);
				alarmData = (AlarmData) adManager.getEntity(ad);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ExceptionHandlerManager.asyncHandleException(e);
			}
			
//			alarmData = (AlarmData) obj;
		} else {
			alarmData = null;
		}
	}


	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	public void openIQCDialog(){
		//检验警报
		String PAGE_CATEGORY_IQC = "newIqcAlarm";
		String TABLE_NAME_IqcLine = "INVIqcLine";
		ADTable adTable;
		CreateIqcContext context = new CreateIqcContext();
		context.setCategory(CreateIqcContext.CATEGORY_NEW_IQC);
		context.setAlarmWhereClause(" and docId = '"+alarmData.getField1()+"'");
		ADTable table = context.getTable(CreateIqcContext.TableName_Receipt);
		TableListManager listTableManager = new TableListManager(table);

		com.graly.erp.inv.iqc.createfrom.IqcCreateWizard wizard = new com.graly.erp.inv.iqc.createfrom.IqcCreateWizard(context, PAGE_CATEGORY_IQC);
		CreateIqcDialog dialog = new CreateIqcDialog(UI.getActiveShell(), wizard, listTableManager);
		context.setDialog(dialog);
		int code = dialog.open();
		if (code == Dialog.OK) {
			Iqc iqc = context.getIqc();
			String where = " iqcId= '" + iqc.getDocId() + "'";
			adTable = getADTableOfRequisition(TABLE_NAME_IqcLine);
			IqcLineDialog iqcLineDialog = new IqcLineDialog(UI.getActiveShell(), adTable, where, iqc);
			if(iqcLineDialog.open() == Dialog.OK){
				
			}
			refresh();
		}
	}
	
	public void openWAREHOUSEDialog(){
		String PAGE_CATEGORY_WAREHOUSE = "createIqcAlarm";
		CreateContext context = new CreateContext();
		context.setCategory(CreateContext.CATEGORY_NEW_IQC);
		context.setAlarmWhereClause(" and receiptId = '"+alarmData.getField1()+"'");
		ADTable table = context.getTable(CreateContext.TableName_Iqc);
		TableListManager listTableManager = new TableListManager(table);

		com.graly.erp.inv.in.createfrom.iqc.IqcCreateWizard wizard = new com.graly.erp.inv.in.createfrom.iqc.IqcCreateWizard(context, PAGE_CATEGORY_WAREHOUSE);
		CreateDialog dialog = new CreateDialog(UI.getActiveShell(), wizard, listTableManager);
		context.setDialog(dialog);
		int code = dialog.open();
		if (code == Dialog.OK) {
			
//			MovementIn mi = context.getIn();
//			if(mi != null && mi.getObjectRrn() != null) {
//			}
		}
		refresh();
	}
	
	
	
	
	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
		}
		return null;
	}
	
	protected void createSectionDesc(List<VAlarmData> alarmDatas){
		try{ 
			String text = Message.getString("common.totalshow");
			long count = alarmDatas.size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("MasterSection : createSectionDesc ", e);
		}
	}
	
	protected void createSectionDesc(Section section){
	}
	
}
