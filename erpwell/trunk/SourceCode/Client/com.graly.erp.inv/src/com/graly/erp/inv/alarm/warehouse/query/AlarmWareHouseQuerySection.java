package com.graly.erp.inv.alarm.warehouse.query;

import java.util.ArrayList;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.in.InLineDialog;
import com.graly.erp.inv.in.createfrom.iqc.CreateContext;
import com.graly.erp.inv.in.createfrom.iqc.CreateDialog;
import com.graly.erp.inv.model.AlarmData;
import com.graly.erp.inv.model.AlarmTarget;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.VAlarmData;
import com.graly.erp.inv.model.VAlarmDataHouse;
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

public class AlarmWareHouseQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(AlarmWareHouseQuerySection.class);

	protected ToolItem itemAgree;
	protected ToolItem itemNoAgree;
	protected ToolItem itemDelete;

	protected AlarmData alarmData;
	protected TableListManager listTableManager;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	
	public AlarmWareHouseQuerySection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionAlarmData(ss.getFirstElement());
				if(alarmData!=null && AlarmTarget.TARGET_TYPE_WAREHOUSE.equals(alarmData.getAlarmType())){
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
		createToolItemAgree(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNoAgree(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemClose(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemAgree(ToolBar tBar) {
		String authorityToolItem = "Alarm.Warehouse.Agree";
		itemAgree = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemAgree.setText("同意入库");
		itemAgree.setImage(SWTResourceCache.getImage("save"));
		itemAgree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				agreeAdapter();
			}
		});
	}
	protected void createToolItemNoAgree(ToolBar tBar) {
		String authorityToolItem = "Alarm.Warehouse.NoAgree";
		itemNoAgree = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemNoAgree.setText("不同意入库");
		itemNoAgree.setImage(SWTResourceCache.getImage("save"));
		itemNoAgree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				noAgreeAdapter();
			}
		});
	}

	protected void createToolItemClose(ToolBar tBar) {
		String authorityToolItem = "Alarm.Warehouse.Close";
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

	protected void closeAdapter() {
		if (alarmData != null) {
			try {
				boolean confirmDelete = UI.showConfirm("是否关闭警报?");
				if (confirmDelete) {
					if (alarmData.getObjectRrn() != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						alarmData = (AlarmData) adManager.getEntity(alarmData);
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
	
	protected void agreeAdapter() {
		if (alarmData != null) {
			try {
				boolean confirmDelete = UI.showConfirm("是否同意入库?");
				if (confirmDelete) {
					if (alarmData.getObjectRrn() != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						alarmData = (AlarmData) adManager.getEntity(alarmData);
						alarmData.setField7("Y");
						alarmData.setField17(Env.getUserRrn());
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
	
	protected void noAgreeAdapter() {
		if (alarmData != null) {
			try {
				boolean confirmDelete = UI.showConfirm("是否不同意入库?");
				if (confirmDelete) {
					if (alarmData.getObjectRrn() != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						alarmData = (AlarmData) adManager.getEntity(alarmData);
						alarmData.setField7("N");
						alarmData.setField17(Env.getUserRrn());//记录不同意人
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

	private void setSelectionAlarmData(Object obj) {
		if (obj instanceof VAlarmDataHouse) {
			VAlarmDataHouse vad = (VAlarmDataHouse) obj;
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
			
		}
		MovementIn movementIn = wizard.getContext().getIn();
//		//弹出审核对话框
		try{
			if(movementIn != null && movementIn.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				movementIn = (MovementIn)adManager.getEntity(movementIn);
				ADTable adTable = getADTableOfMovement("INVMovementLine");
				String whereClause = " movementId='" + movementIn.getDocId().toString() + "'";
				InLineDialog inLineDialog = new InLineDialog(UI.getActiveShell(), getADTableOfInLineDialog("INVMovementIn"), whereClause, movementIn,
						adTable, false);
				inLineDialog.open();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		refresh();
	}
	
	protected ADTable getADTableOfInLineDialog(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
		}
		return null;
	}
	
	protected ADTable getADTableOfMovement(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("InSection : getADTableOfRequisition()", e);
		}
		return null;
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
	
	//总共多少条记录
	protected void createSectionDesc(List<VAlarmDataHouse> alarmDatas){
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
	
	
	
	@Override
	public void refresh() {
//		List<AlarmData> showAlarmDatas = new ArrayList<AlarmData>();//界面上显示的AlarmData数据
		List<VAlarmDataHouse> alarmDatas = null;
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			alarmDatas = adManager.getEntityList(Env.getOrgRrn(), VAlarmDataHouse.class,Integer.MAX_VALUE,
					getWhereClause()+" and status ='OPEN' and alarmType='WAREHOUSE' ","field1 desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		for(int a=0;a<alarmDatas.size();a++){
//			AlarmData tempAlarmData = alarmDatas.get(a);
//			String[] field5 = null;
//			String[] field6 = null;
//			String[] field8 = null;
//			String[] field23 = null;//检验数
//			String[] field24 = null;//合格数
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
//			if(tempAlarmData.getField23()!=null){
//				field23 = tempAlarmData.getField23().split(";");
//			}
//			if(tempAlarmData.getField24()!=null){
//				field24 = tempAlarmData.getField24().split(";");
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
//					alarmData.setStatus(tempAlarmData.getStatus());
//					alarmData.setField1(tempAlarmData.getField1());
//					alarmData.setField2(tempAlarmData.getField2());
//					alarmData.setField3(tempAlarmData.getField3());
//					alarmData.setField4(tempAlarmData.getField4());
//					if(field5!=null && field5[j]!=null){
//						alarmData.setField5(field5[j]);
//					}
//					if(field6!=null && field6[j]!=null){
//						alarmData.setField6(field6[j]);
//					}
//					if(field8!=null && field8[j]!=null){
//						alarmData.setField8(field8[j]);
//					}
//					if(field23!=null && field23[j]!=null){
//						alarmData.setField23(field23[j]);
//					}
//					if(field24!=null && field24[j]!=null){
//						alarmData.setField24(field24[j]);
//					}
//					if(field25!=null && field25[j]!=null){
//						alarmData.setField25(field25[j]);
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
//					alarmData.setField21(tempAlarmData.getField21());
//					alarmData.setField22(tempAlarmData.getField22());
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
	}
	
	
}

