package com.graly.erp.inv.material.qtysquery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.BrowserDialog;
import com.graly.erp.base.QueryTimeDialog;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.pur.dialog.PurAssociatedDialog;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialQtyQuerySection extends QuerySection {
	private Logger logger = Logger.getLogger(MaterialQtyQuerySection.class);
	
	public static final String TABLE_NAME_PR_LINE = "WorkCenterRequisitionLine";
	public static final String TABLE_NAME_PO_LINE = "WorkCenterPurchaseOrderLine";
	public static final String TABLE_NAME_MO_LINE = "WIPManufactureOrderLine";			//子工作令,工作中心看板
	private ToolItem itemMinQuery;//库存数小于安全库存数
	private ToolItem itemGuanLian;//关联按钮
	private ToolItem itemStorageAlarm;
	private ToolItem itemWorkNeed;
	private Material selectedMaterial;			//选择的物料
	private Map<String,Object>	queryKeys;

	protected EntityQueryDialog minQueryDialog;
	private ToolItem itemComments;
	
	public MaterialQtyQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1 ");
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemComments(tBar);
		createToolItemMinQuery(tBar);
		createToolItemGuanLian(tBar);
		createToolItemStorageAlarm(tBar);
		createToolItemWorkNeed(tBar);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
		
	}

	private void createToolItemMinQuery(ToolBar tBar) {
		itemMinQuery = new ToolItem(tBar, SWT.PUSH);
		itemMinQuery.setText("小于安全库存");
		itemMinQuery.setImage(SWTResourceCache.getImage("search"));
		itemMinQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				minQueryAdapter();
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

	protected void minQueryAdapter() {
		try {
			if (minQueryDialog != null) {
				minQueryDialog.setVisible(true);
			} else {
				minQueryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this, this.getClass().getDeclaredMethod("refresh2"));
				minQueryDialog.open();
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void refresh2(){
		try {
			Map<String, Object>  queryKeys = null;
			
			if(minQueryDialog != null){
				queryKeys = minQueryDialog.getQueryKeys();
			}
			List ls = new ArrayList();
			
			INVManager invManager = Framework.getService(INVManager.class);
			if(queryKeys != null && !queryKeys.isEmpty()){
				long materialRrn = Long.valueOf((String) queryKeys.get("objectRrn"));
				String whereClause = "PM.material_rrn = "+materialRrn;
				ls = invManager.queryMaterialQtysAlarmYn(Env.getOrgRrn(), whereClause,null,null);
			}else{
				ls = invManager.queryMaterialQtysAlarmYn(Env.getOrgRrn(), null,null,null);
			}
			viewer.setInput(ls);		
			tableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void createToolItemGuanLian(ToolBar tBar) {
		itemGuanLian = new ToolItem(tBar, SWT.PUSH);
		itemGuanLian.setText("工作令");
		itemGuanLian.setImage(SWTResourceCache.getImage("search"));
		itemGuanLian.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				guanlianAdapter();
			}
		});
	}
	
	protected void guanlianAdapter(){
		try {
			if(selectedMaterial != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable moTable = adManager.getADTable(0L, TABLE_NAME_MO_LINE);
				moTable = adManager.getADTableDeep(moTable.getObjectRrn());
				
				Long materialRrn = selectedMaterial.getObjectRrn();
				GuanLianDialog cd = new GuanLianDialog(UI.getActiveShell(),
						moTable, materialRrn);
				if(cd.open() == Dialog.CANCEL) {
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	@Override
	protected void createNewViewer(Composite client, IManagedForm form) {
		super.createNewViewer(client, form);
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionMaterial(ss.getFirstElement());
				try {
					if(selectedMaterial != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						ADTable prTable = adManager.getADTable(0L, TABLE_NAME_PR_LINE);
						prTable = adManager.getADTableDeep(prTable.getObjectRrn());
						ADTable poTable = adManager.getADTable(0L, TABLE_NAME_PO_LINE);
						poTable = adManager.getADTableDeep(poTable.getObjectRrn());
						
						Long materialRrn = selectedMaterial.getObjectRrn();
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
					setSelectionMaterial(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	protected void setSelectionMaterial(Object obj) {
		if(obj instanceof Material) {
			selectedMaterial = (Material)obj;
		} else {
			selectedMaterial = null;
		}
	}
	
	@Override
	public void refresh() {
		try {
			if(queryDialog != null){
				queryKeys = queryDialog.getQueryKeys();
			}
			List ls = new ArrayList();
			
			INVManager invManager = Framework.getService(INVManager.class);
			if(queryKeys != null && !queryKeys.isEmpty()){
				long materialRrn = Long.valueOf((String) queryKeys.get("objectRrn"));
				String whereClause = "PM.material_rrn = "+materialRrn;
				ls = invManager.queryMaterialQtysAlarmYn(Env.getOrgRrn(), whereClause,null,null);
			}else{
				ls = invManager.queryMaterialQtysAlarmYn(Env.getOrgRrn(), null,null,null);
			}
			viewer.setInput(ls);		
			tableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createSectionDesc(Section section) {
		try{ 
			String text = Message.getString("common.totalshow");
			long count = ((List)viewer.getInput()).size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}
	
	protected void createToolItemStorageAlarm(ToolBar tBar) {
		itemStorageAlarm = new ToolItem(tBar, SWT.PUSH);
		itemStorageAlarm.setText("警报");
		itemStorageAlarm.setImage(SWTResourceCache.getImage("search"));
		itemStorageAlarm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				storageAlarmAdapter();
			}
		});
	}
	
//	protected void storageAlarmAdapter() {
//		    String material_id = selectedMaterial.getMaterialId();
//			String urlfmt = Message.getString("url.storagealarm");
//			String url = String.format(urlfmt,start_time,end_time,material_id);
//			System.out.print(url);
//			BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
//			bd.open();
//		}
	
	protected void storageAlarmAdapter() {
			QueryTimeDialog qtd=new QueryTimeDialog(UI.getActiveShell());
			if(qtd.open()== Dialog.OK){
				SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
				LinkedHashMap<String,IField> fields = qtd.baseForm.getFields();
				IField startTimeField =fields.get("sDate");
				Date startTime =  (Date)startTimeField.getValue();
				String s1 =  sdf.format(startTime);
				IField endTimeField =fields.get("eDate");
				Date endTime =  (Date)endTimeField.getValue();
				String s2 =  sdf.format(endTime);
				String material_id = selectedMaterial.getMaterialId();
				String urlfmt = Message.getString("url.storagealarm");
				String url = String.format(urlfmt, s1,s2,material_id);
				BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
				bd.open();
			}
		}
	
	protected void createToolItemWorkNeed(ToolBar tBar) {
		itemWorkNeed = new ToolItem(tBar, SWT.PUSH);
		itemWorkNeed.setText("工作令已分配数");
		itemWorkNeed.setImage(SWTResourceCache.getImage("search"));
		itemWorkNeed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				workNeedAdapter();
			}
		});
}

    protected void workNeedAdapter() {
	    long material_rrn = selectedMaterial.getObjectRrn();
		String urlfmt = Message.getString("url.workneed");
		String url = String.format(urlfmt,material_rrn);
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
		bd.open();
	}	
    
	public void saveCommentsAdapter() {
		try {
			if(selectedMaterial != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable adTable = adManager.getADTable(0L, "MaterialQtyQueryComment");
				
				MaterialQtyQueryCommentsDialog  commentsDialog = new MaterialQtyQueryCommentsDialog(UI.getActiveShell(),selectedMaterial,adTable);
				if(commentsDialog.open() == Dialog.OK) {
					Material commentMaterial = commentsDialog.getSelectMaterial();
					if(commentMaterial.getReferenceDoc5()!=null){
						Material saveMaterial = (Material) adManager.getEntity(selectedMaterial);
						saveMaterial.setReferenceDoc5(commentMaterial.getReferenceDoc5());
						saveMaterial = (Material) adManager.saveEntity(saveMaterial, Env.getUserRrn());
						selectedMaterial.setReferenceDoc5(saveMaterial.getReferenceDoc5());
						viewer.refresh();
//						refresh();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
}

