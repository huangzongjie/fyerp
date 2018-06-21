package com.graly.erp.inv.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.DelInvMovementAuthorityManager;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.MaterialWCAndInvoiceQueryDialog;
import com.graly.erp.inv.model.InvErrorLog;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.imp.ErrorLogDisplayDialog;
import com.graly.erp.inv.out.imp.OutLineImport;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class OutSection extends MasterSection {
	
	private static final Logger logger = Logger.getLogger(OutSection.class);
	protected ToolItem itemNew;
	protected ToolItem itemEdit;
	protected ToolItem itemDelete;
	private static final String TABLE_NAME = "INVMovementOutLine";
	private ADTable adTable;
	protected MovementOut selectedOut;
	protected IManagedForm form;
	protected ADManager adManager;
	protected List<Map<String, List>> listMap=new ArrayList<Map<String,List>>();
	protected ToolItem itemPreview;
	
	public OutSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		super.createContents(form, parent, sectionStyle);
		this.form = form;
		loseLotAlarmBT();
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionMovementOut(ss.getFirstElement());
	    		editAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionMovementOut(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItenImport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		
		// Add by BruceYou 2012-03-14
		//createToolItemExport(tBar);
//		createToolItemPreview(tBar);//丁军提高打印效率
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItenImport(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SOU_IMPORT);
		//itemNew.setText(Message.getString("common.new"));
		itemNew.setText("批量出库");
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				importAdapter();
			}
		});
	}
	
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SOU_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}
	
	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SOU_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}
	
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SOU_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	protected void newAdapter() {
		SaleOrderQueryDialog orderDialog = new SaleOrderQueryDialog(form, UI.getActiveShell());
		if(orderDialog.open() == Dialog.OK) {
			MovementOut out = orderDialog.getOut();
			if(out != null) {
				String whereClause = " movementRrn = " + out.getObjectRrn() + " ";
				out.setOrgRrn(Env.getOrgRrn());
				OutLineBlockDialog olbd = new OutLineBlockDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, out, getADTableOfPOLine());
				olbd.setSouFlag(true);
				if(olbd.open() == Dialog.CANCEL) {
					out = (MovementOut)olbd.getParentObject();
					if (out != null && out.getObjectRrn() != null) {
						selectedOut = out;
						refreshSection();
						if(Env.getOrgRrn() == 12644730L){//注意，前程序员BUG，由于影响需时间查看，有待修复，
						}else{
							refreshAdd(selectedOut);
						}
					}
				}
			}
		}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected void importAdapter() {
			FileDialog fileDialog = new FileDialog(UI.getActiveShell(), SWT.OPEN);
			// 设置初始路径
			fileDialog.setFilterPath("C:/");
			// 设置扩展名过滤
			String[] filterExt = { "*.txt"};
			fileDialog.setFilterExtensions(filterExt);
			// 打开文件对话框，返回选择的文件
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				if (!selectedFile.contains(".txt")) {
					UI.showWarning(Message.getString("ppm.upload_file_type_not_support"));
					return;
				}
				else{
					
					OutLineImport outLineImport=new OutLineImport(selectedFile);
					if(outLineImport.getErrorLogs().size()<=0){
						UI.showConfirm(Message.getString("inv.import_successed"));
					}else{
						if(UI.showConfirm(Message.getString("inv.errorlog_see"))){
							List<InvErrorLog> list=outLineImport.getErrorLogs();
							ErrorLogDisplayDialog errorLogDisplayDialog=new ErrorLogDisplayDialog(UI.getActiveShell(),list);
								errorLogDisplayDialog.open();
								list.clear();
						}
					}
//							String whereClause = " movementRrn = " + movementOut.getObjectRrn() + " ";
//							movementOut.setOrgRrn(Env.getOrgRrn());
//							OutLineBlockDialog olbd = new OutLineBlockDialog(UI.getActiveShell(),
//									this.getTableManager().getADTable(), whereClause, movementOut, getADTableOfPOLine());
//							if(olbd.open() == Dialog.CANCEL) {
//								movementOut = (MovementOut)olbd.getParentObject();
//								if (movementOut != null && movementOut.getObjectRrn() != null) {
//									selectedOut = movementOut;
//									refreshSection();
//									refreshAdd(selectedOut);
//								}
//							}
//						}
//					}
					
				}
			}
		}
	
	protected void deleteAdapter() {
		if(selectedOut != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedOut.getObjectRrn() != null) {
						if(DelInvMovementAuthorityManager.hasDeleteAuthority(Env.getUserRrn(),
								selectedOut.getWarehouseRrn(), selectedOut.getWarehouseId())) {
							INVManager invManager = Framework.getService(INVManager.class);
							invManager.deleteMovementOut(selectedOut, Env.getUserRrn());
							this.refreshDelete(selectedOut);
							this.selectedOut = null;
							refreshSection();							
						}
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
	protected void editAdapter() {
		try {
			if(selectedOut != null && selectedOut.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedOut = (MovementOut)adManager.getEntity(selectedOut);
				String whereClause = ( " movementRrn = '" + selectedOut.getObjectRrn().toString() + "' ");
				OutLineBlockDialog cd = new OutLineBlockDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedOut, getADTableOfPOLine());
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedOut);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at OutSection : editAdapter() " + e);
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialWCAndInvoiceQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_SOU);
			queryDialog.open();
		}
	}
	
	protected void refreshSection() {
		try {
//			refresh();
			if(selectedOut != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedOut = (MovementOut)adManager.getEntity(selectedOut);
				this.setStatusChanged(selectedOut.getDocStatus());
			}
			if(Env.getOrgRrn() == 12644730L){
				refresh();
			}
		} catch(Exception e) {
			logger.error("Error at OutSection : refreshSection() " + e);
		}
	}

	private void setSelectionMovementOut(Object obj) {
		if(obj instanceof MovementOut) {
			selectedOut = (MovementOut)obj;
			setStatusChanged(selectedOut.getDocStatus());
		} else {
			selectedOut = null;
			setStatusChanged("");
		}
	}
	
	protected void setStatusChanged(String status) {
		if(MovementOut.STATUS_DRAFTED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if(MovementOut.STATUS_CLOSED.equals(status)) {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
	
	/**
	 * 需求人:罗小华
	 * 功能:遗挂批次提醒.Material类型无需挂批次,Batch类型需要挂批次
	 * 奔泰未挂批次的销售出库单显示红色,对于批次要么全部挂上，要么都不挂上
	 * */
	protected void loseLotAlarmBT() {
		if(Env.getOrgRrn() != 12644730L){
			return;
		}
		try {
		if (viewer instanceof TableViewer){
			Table table = ((TableViewer) viewer).getTable();
			for (TableItem item : table.getItems()) {
				TableItem tableItem = item;
				Object obj =  tableItem.getData();
				MovementOut movementOut = null;
				if(obj instanceof MovementOut){
					movementOut = (MovementOut) obj;
					if(movementOut.getBtLotAlarm()){
						item.setBackground(SWTResourceCache.getColor("Red"));
					}
					
				}
//				ADManager adManager =Framework.getService(ADManager.class);
//				List<MovementLine> movementLines = adManager.getEntityList(Env.getOrgRrn(), 
//						MovementLine.class, Integer.MAX_VALUE, 
//						"movementRrn = " + movementOut.getObjectRrn(), null);
//				
//				for(MovementLine movementLine : movementLines){
//					if(Lot.LOTTYPE_MATERIAL.equals(movementLine.getLotType())){
//						continue;
//					}else{
//						List<MovementLineLot> lineLots =null;
//							lineLots = adManager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,
//									Integer.MAX_VALUE,"movementRrn= "+movementOut.getObjectRrn(),"");
//						if(lineLots==null || lineLots.size() ==0){
//							item.setBackground(SWTResourceCache.getColor("Red"));
//						}
//						break;
//					}
//				}
			}
			table.redraw();
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void refresh() {
		super.refresh();
		loseLotAlarmBT();
	}
	
	protected void createToolItemPreview(ToolBar tBar) {
		itemPreview = new ToolItem(tBar, SWT.PUSH);
		itemPreview.setText(Message.getString("common.print"));
		itemPreview.setImage(SWTResourceCache.getImage("print"));
		itemPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewAdapter();
			}
		});
	}
	protected void previewAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			
			//保存打印次数
			MovementOut mo = selectedOut;
			Long time = mo.getPrintTime();
			if(time == null){
				mo.setPrintTime(1L);
			}else{
				mo.setPrintTime(time + 1L);
			}
			ADManager manager = Framework.getService(ADManager.class);
			mo = (MovementOut) manager.saveEntity(mo, Env.getUserRrn());			
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(!Movement.STATUS_APPROVED.equals(mo.getDocStatus())){
//				UI.showWarning(Message.getString("common.is_not_approved")+","+Message.getString("common.can_not_print"));
//				return;
			}
			
			if(mo == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = mo.getObjectRrn();
			userParams.put("OBJECT_RRN", String.valueOf(objectRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), "sout_report.rptdesign", params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
}
