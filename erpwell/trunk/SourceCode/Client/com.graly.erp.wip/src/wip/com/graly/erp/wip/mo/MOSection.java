package com.graly.erp.wip.mo;

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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.ppm.model.TpsLine;
import com.graly.erp.wip.mo.create.MOGenerateContext;
import com.graly.erp.wip.mo.create.MOGenerateDialog;
import com.graly.erp.wip.mo.create.MOGenerateWizard;
import com.graly.erp.wip.mo.edit.MOEditContext;
import com.graly.erp.wip.mo.fromtps.TpsLinePrepareDialog;
import com.graly.erp.wip.mo.fromtps.TpsLineSelectionDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class MOSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MOSection.class);
	protected ToolItem itemFormTps;
	protected ToolItem itemPrepareNew;
	protected ToolItem itemNew;
	protected ToolItem itemPrepareEdit;
	protected ToolItem itemEdit;
	protected ToolItem itemDelete;
	protected ToolItem itemApprove;	
	protected ToolItem itemClose;
	protected ToolItem itemPreviewBom;
	protected ToolItem itemPreviewTask;

	protected ToolItem toolItemConfirm;
	protected ToolItem itemComments;
	protected ToolItem itemChildren;
	protected ToolItem itemDelByMps;
	
//	protected ToolItem itemFromTps2;//创建从临时计划II(不包含有预处理物料的临时计划)
	protected ToolItem itemFromPrepareTps;//创建从有预处理物料的临时计划
	protected ToolItem itemQueryPrepare;//查询待处理
	protected ToolItem itemWmsIn;
	protected ToolItem itemSubTime;
	protected ManufactureOrder selectedMO;
	private static final String TABLE_NAME = "WIPManufactureOrderLine";
	private ADTable adTable;
	private IManagedForm form;
	ADManager adManager;
	
	public MOSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	public void createContents(IManagedForm form, Composite parent){
		this.form = form;
		createContents(form, parent, Section.DESCRIPTION|Section.TITLE_BAR);
		createSectionDesc(section);
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionPO(ss.getFirstElement());
	    		if(selectedMO.getIsPrepareMo()){
	    			editDoubleClickAdapter();
	    		}else{
	    			editAdapter();
	    		}
	    		
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionPO(ss.getFirstElement());
				} catch (Exception e){
					logger.error("Error MOSection : createViewAction() " + e);
				}
			}
	    });
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSubTime(tBar);
		createToolItemFromTps(tBar);//正常计划
		createToolItemFromPrepareTps(tBar);//预处理计划
		createToolItemPrepareBom(tBar);//预处理计划物料
//		createToolItemPrepareNew(tBar);//取消新建工作令设置预处理，经过探讨
		createToolItemPrepareEdit(tBar);
		createToolItemNew(tBar);
		createToolItemEdit(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		createToolItemClose(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreviewBom(tBar);
		createToolItemPreviewTask(tBar);
		createToolItemConfirm(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemComments(tBar);
		createToolItemChildrenMoLine(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDeleteByMps(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		createToolItemWmsIn(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemConfirm(ToolBar tBar){
		toolItemConfirm = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_REFERENCEDOC);
		toolItemConfirm.setText(Message.getString("wip.confirm"));
		toolItemConfirm.setImage(SWTResourceCache.getImage("preview"));
		toolItemConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				confirmAdapter();
			}
		});
	}

	protected void confirmAdapter() {
		try {
			String report = "mo_produce_confirm.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Long moLineRrn = 0L;
			if(selectedMO != null){
				moLineRrn = selectedMO.getObjectRrn();
			}else{
				UI.showError(Message.getString("mo.no_mo_selected"));
				return;
			}
			userParams.put("MO_RRN", String.valueOf(moLineRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}	
	}
	
	private void createToolItemPreviewTask(ToolBar bar) {
		itemPreviewTask = new AuthorityToolItem(bar, SWT.PUSH, Constants.KEY_MO_MOTASK);
		itemPreviewTask.setText(Message.getString("mo.preview_mo_task"));
		itemPreviewTask.setImage(SWTResourceCache.getImage("preview"));
		itemPreviewTask.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewTaskAdapter();
			}
		});
	}
	
	private void createToolItemChildrenMoLine(ToolBar bar) {
		itemChildren = new AuthorityToolItem(bar, SWT.PUSH, Constants.KEY_MO_CHILDREN);
		itemChildren.setText(Message.getString("wip.mo_line"));
		itemChildren.setImage(SWTResourceCache.getImage("lines"));
		itemChildren.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				getMoLineAdapter();
			}
		});
	}
	
	private void createToolItemPreviewBom(ToolBar bar) {
		itemPreviewBom = new AuthorityToolItem(bar, SWT.PUSH, Constants.KEY_MO_BOM);
		itemPreviewBom.setText(Message.getString("common.view_bom"));
		itemPreviewBom.setImage(SWTResourceCache.getImage("preview"));
		itemPreviewBom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewBomAdapter();
			}
		});
	}
	
	protected void createToolItemFromTps(ToolBar tBar) {
		itemFormTps = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_CREATEFROM);
		itemFormTps.setText(Message.getString("pur.copyfrom"));
		itemFormTps.setImage(SWTResourceCache.getImage("copy"));
		itemFormTps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				fromTpsAdapter();
			}
		});
	}
	
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}
	protected void createToolItemPrepareEdit(ToolBar tBar) {
		if(Env.getOrgRrn() ==139420L || Env.getOrgRrn() == 2501932L ){
			String authorityToolItem = "WIP.MO.EDIT_PREPARE";
			itemPrepareEdit = new AuthorityToolItem(tBar, SWT.PUSH, authorityToolItem);
			itemPrepareEdit.setText("编辑预处理");
			itemPrepareEdit.setImage(SWTResourceCache.getImage("edit"));
			itemPrepareEdit.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					editPrepareAdapter();
				}
			});
		} 
	}
	
	protected void createToolItemEdit(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_EDIT);
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
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_DELETE);
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
	
	protected void createToolItemDeleteByMps(ToolBar tBar) {
		itemDelByMps = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_DELETE);
		itemDelByMps.setText(Message.getString("wip.del_by_mps"));
		itemDelByMps.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelByMps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				delByMpsAdapter();
			}
		});
	}
	
	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}
	
	protected void createToolItemClose(ToolBar tBar) {
		itemClose = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_REVOKE);
		itemClose.setText(Message.getString("common.close"));
		itemClose.setImage(SWTResourceCache.getImage("close"));
		itemClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closeAdapter();
			}
		});
	}
	
	protected void createToolItemComments(ToolBar tBar) {
		itemComments = new AuthorityToolItem(tBar, SWT.PUSH, "");
		itemComments.setText(Message.getString("pdm.material_comments"));
		itemComments.setImage(SWTResourceCache.getImage("report"));
		itemComments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				commentsAdapter();
			}
		});
	}
	
	protected void commentsAdapter() {
		try {
			if(selectedMO == null) return;
			MoCommentsDialog md = new MoCommentsDialog(this.getTableManager().getADTable(), selectedMO);
			int keyCode = md.open();
			if(keyCode == Dialog.OK) {
				selectedMO = md.getUpdateCommentsMo();
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
//				adManager.saveEntity(selectedMO, Env.getUserRrn());
				WipManager wipManager = Framework.getService(WipManager.class);
				wipManager.updateMoComments(selectedMO ,Env.getUserRrn());
				selectedMO = (ManufactureOrder)adManager.getEntity(selectedMO);
				this.refreshUpdate(selectedMO);
			}			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void fromTpsAdapter() {
		TpsLineSelectionDialog tls = new TpsLineSelectionDialog(UI.getActiveShell());
		if(tls.open() == Dialog.OK) {
			Long tps = tls.getManufactureOrder().getTpsRrn();//添加创建从计划判断，预处理计划不允许该功能创建
			if(tps!=null){
				try {
					TpsLine tpsline= new TpsLine();
					tpsline.setObjectRrn(tps);
					ADManager adManager = Framework.getService(ADManager.class);
					tpsline = (TpsLine) adManager.getEntity(tpsline);
					if(tpsline!=null && tpsline.getExcelValidate()){
						UI.showInfo("该计划为预处理计划，请从创建从2里面创建");
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			MOGenerateContext context = new MOGenerateContext();
			context.setCategory(MOGenerateContext.CATEGORY_NEW);
			context.setAdTable_MO(getTableManager().getADTable());
			context.setManufactureOrder(tls.getManufactureOrder());
			
			MOGenerateWizard wizard = new MOGenerateWizard(context);
			MOGenerateDialog mgd = new MOGenerateDialog(UI.getActiveShell(), wizard);
			context.setDialog(mgd);
			int code = mgd.open();
			if (code == Dialog.OK) {
				this.refresh();
			}			
		}
	}
	
	protected void newAdapter() {
		MOGenerateContext context = new MOGenerateContext();
		context.setCategory(MOGenerateContext.CATEGORY_NEW);
		context.setAdTable_MO(getTableManager().getADTable());
		MOGenerateWizard wizard = new MOGenerateWizard(context);
		MOGenerateDialog mgd = new MOGenerateDialog(UI.getActiveShell(), wizard);
		context.setDialog(mgd);
		int code = mgd.open();
		if (code == Dialog.OK) {
			this.refresh();
		}
	}
	
	protected void editPrepareAdapter() {
		try {
			if(selectedMO != null && selectedMO.getObjectRrn() != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedMO = (ManufactureOrder)adManager.getEntity(selectedMO);
				if(!selectedMO.getIsPrepareMo()){
					UI.showError("该工作令不是预处理工作令");
					return;
				}
				MOGenerateContext context = new MOGenerateContext();
				context.setCategory(MOEditContext.CAGEGORY_PREPARE_EDIT);
				context.setManufactureOrder(selectedMO);
				
				MOGenerateWizard wizard = new MOGenerateWizard(context);
				if(!ManufactureOrder.STATUS_DRAFTED.equals(selectedMO.getDocStatus()) 
						&& !ManufactureOrder.STATUS_PREPARE.equals(selectedMO.getDocStatus())) {
					wizard.setCanEdit(false);
				}
				MOGenerateDialog mgd = new MOGenerateDialog(UI.getActiveShell(), wizard);
				context.setDialog(mgd);
				if(mgd.open() == Dialog.OK) {
					this.refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MOSection : editAdapter() " + e);
		}
	}
	protected void editDoubleClickAdapter() {
		try {
			if(selectedMO != null && selectedMO.getObjectRrn() != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedMO = (ManufactureOrder)adManager.getEntity(selectedMO);
				MOGenerateContext context = new MOGenerateContext();
//				context.setCategory(MOEditContext.CAGEGORY_EDIT);
				context.setCategory("prepareEditMo");
				context.setManufactureOrder(selectedMO);
				
				MOGenerateWizard wizard = new MOGenerateWizard(context);
				if(!ManufactureOrder.STATUS_DRAFTED.equals(selectedMO.getDocStatus()) 
						&& !ManufactureOrder.STATUS_PREPARE.equals(selectedMO.getDocStatus())) {
					wizard.setCanEdit(false);
				}
				MOGenerateDialog mgd = new MOGenerateDialog(UI.getActiveShell(), wizard);
				context.setDialog(mgd);
				if(mgd.open() == Dialog.OK) {
					this.refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MOSection : editAdapter() " + e);
		}
	}
	
	protected void editAdapter() {
		try {
			if(selectedMO != null && selectedMO.getObjectRrn() != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedMO = (ManufactureOrder)adManager.getEntity(selectedMO);
				MOGenerateContext context = new MOGenerateContext();
				context.setCategory(MOEditContext.CAGEGORY_EDIT);
				context.setManufactureOrder(selectedMO);
				
				MOGenerateWizard wizard = new MOGenerateWizard(context);
				if(!ManufactureOrder.STATUS_DRAFTED.equals(selectedMO.getDocStatus())) {
					wizard.setCanEdit(false);
				}
				MOGenerateDialog mgd = new MOGenerateDialog(UI.getActiveShell(), wizard);
				context.setDialog(mgd);
				if(mgd.open() == Dialog.OK) {
					this.refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MOSection : editAdapter() " + e);
		}
	}
	
	protected void deleteAdapter() {
		if(selectedMO != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedMO.getObjectRrn() != null) {
						WipManager wipManager = Framework.getService(WipManager.class);
						wipManager.deleteMo(selectedMO, Env.getUserRrn());
						this.selectedMO = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				logger.error("Error at MOSection : deleteAdapter() " + e1.getStackTrace());
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
	protected void delByMpsAdapter() {
		if(selectedMO != null && selectedMO.getMpsRrn() != null) {
			try {
				
				boolean confirmDelete = UI.showConfirm(String.format(Message
						.getString("wip.confige_delete_by_mps"), selectedMO.getMpsId()));
				if (confirmDelete) {
					if (selectedMO.getObjectRrn() != null) {
						WipManager wipManager = Framework.getService(WipManager.class);
						wipManager.deleteMoByMps(selectedMO, Env.getUserRrn());
						this.selectedMO = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				logger.error("Error at MOSection : deleteAdapter() " + e1.getStackTrace());
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			if(selectedMO != null && selectedMO.getObjectRrn() != null) {
				WipManager wipManager = Framework.getService(WipManager.class);
				wipManager.approveMo(selectedMO, Env.getUserRrn());
				UI.showInfo(Message.getString("common.approve_successed"));
				refreshSection();
			}
		} catch (Exception e) {
			logger.error("Error at MOSection : approveAdapter() " + e.getStackTrace());
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void closeAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if(selectedMO != null && selectedMO.getObjectRrn() != null) {
				boolean confirmClose = UI.showConfirm(Message.getString("wip.confirm_close_mo"));
				if(confirmClose){
//					ADManager adManager = Framework.getService(ADManager.class);
//					selectedMO = (ManufactureOrder)adManager.getEntity(selectedMO);
//					
//					if(selectedMO.getQtyReceive() != null
//							&& selectedMO.getQtyReceive().compareTo(selectedMO.getQtyIn()) > 0) {
//						//未接收完不能入库
//						UI.showError(Message.getString("wip.is_not_received_finished"));
//						return;
//					}
					if(selectedMO.getQtyIn().compareTo(selectedMO.getQtyReceive()) < 0) {
						boolean continuedo = UI.showConfirm(Message.getString("wip.qtyIn_smaller_qtyReceive_continue"));
						if(!continuedo) {
							return;
						}
					}
					WipManager wipManager = Framework.getService(WipManager.class);
					wipManager.closeMo(selectedMO, Env.getUserRrn());
					UI.showInfo(Message.getString("common.close_successed"));
					refreshSection();							
				}
			}
		} catch (Exception e) {
			logger.error("Error at MOSection : closeAdapter() " + e.getStackTrace());
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void getMoLineAdapter() {
		try {
			if(selectedMO != null && selectedMO.getObjectRrn() != null) {
				MOChildrenDialog dialog = new MOChildrenDialog(UI.getActiveShell(), selectedMO);
				dialog.open();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void previewTaskAdapter() {
		try {
			String report = "motask_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Long moRrn = 0L;
			if(selectedMO != null){
				moRrn = selectedMO.getObjectRrn();
			}else{
				UI.showError(Message.getString("mo.no_mo_selected"));
				return;
			}
			userParams.put("MO_RRN", String.valueOf(moRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}	
	}
	
	protected void previewBomAdapter() {
		try {
			String report = "bom2_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Long moRrn = 0L;
			if(selectedMO != null){
				moRrn = selectedMO.getObjectRrn();
			}else{
				UI.showError(Message.getString("mo.no_mo_selected"));
				return;
			}
			userParams.put("MO_RRN", String.valueOf(moRrn));
			userParams.put("ORG_RRN", String.valueOf(Env.getOrgRrn()));//奔泰领料单下方的 财务主管签字改成 车间主管
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void refreshSection() {
		try {
			refresh();
			if(selectedMO != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedMO = (ManufactureOrder)adManager.getEntity(selectedMO);
				this.setStatusChanged(selectedMO.getDocStatus());
				if(selectedMO.getMpsRrn() == null) {
					this.itemDelByMps.setEnabled(false);
				} else {
					this.itemDelByMps.setEnabled(true);
				}
			}
		} catch(Exception e) {
			logger.error("Error at MOSection : refreshSection() " + e);
		}
	}
	
	/**
	 * 预处理MO如果有待生成的工作令，背景颜色显示红色
	 * */
	@Override
	public void refresh(){
		viewer.setInput(new EntityItemInput(getTableManager().getADTable(), getWhereClause(), ""));		
		tableManager.updateView(viewer);
		TableViewer tViewer = (TableViewer) viewer;
		Table table = tViewer.getTable();
		for(int i =0;i<table.getItems().length;i++){
			TableItem item = table.getItems()[i];
			ManufactureOrder mo = (ManufactureOrder) item.getData();
			if(mo.getHasPrepareMoLine()){
				Color redColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
				item.setBackground(redColor);
			}else if(mo.getIsPrepareMo() && !mo.getHasPrepareMoLine()){
				Color greenColor = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
				item.setBackground(greenColor);
			}
		}
		createSectionDesc(section);
	}
	

	protected ADTable getADTableOfMOLine() {
		try {
			if(adTable == null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				adTable = adManager.getADTable(0L, TABLE_NAME);
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("MOSection : getADTableOfMOLine()", e);
		}
		return null;
	}
	
	private void setSelectionPO(Object obj) {
		if(obj instanceof ManufactureOrder) {
			selectedMO = (ManufactureOrder)obj;
			setStatusChanged(selectedMO.getDocStatus());
			if(selectedMO.getMpsRrn() == null) {
				this.itemDelByMps.setEnabled(false);
			} else {
				this.itemDelByMps.setEnabled(true);
			}
		} else {
			selectedMO = null;
			setStatusChanged("");
			itemDelByMps.setEnabled(false);
		}
	}
	
	protected void setStatusChanged(String status) {
		if(ManufactureOrder.STATUS_DRAFTED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
			itemApprove.setEnabled(true);
			itemClose.setEnabled(false);
			if(Env.getOrgRrn() ==139420L){
				itemPrepareEdit.setEnabled(false);
			}
		} else if(ManufactureOrder.STATUS_APPROVED.equals(status)) {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
			itemApprove.setEnabled(false);
			itemClose.setEnabled(true);
			if(Env.getOrgRrn() ==139420L){
				itemPrepareEdit.setEnabled(false);
			}
			
		}else if(ManufactureOrder.STATUS_CLOSED.equals(status)) {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
			itemApprove.setEnabled(false);
			itemClose.setEnabled(false);
			if(Env.getOrgRrn() ==139420L){
				itemPrepareEdit.setEnabled(false);
			}
		}else if(ManufactureOrder.STATUS_PREPARE.equals(status)){
			if(Env.getOrgRrn() ==139420L){
				itemPrepareEdit.setEnabled(true);
			}
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(true);
			itemApprove.setEnabled(false);
			itemClose.setEnabled(false);
		}else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
			itemApprove.setEnabled(false);
			itemClose.setEnabled(false);
			if(Env.getOrgRrn() ==139420L){
				itemPrepareEdit.setEnabled(false);
			}
		}
	}
	
//以下为预处理的按钮-------------------------------------------------------------------------------
	//创建从2
	protected void createToolItemFromPrepareTps(ToolBar tBar){
		//有权限才显示该按钮否则不予显示
//		if (Env.getAuthority() != null) {
//			if (Env.getAuthority().contains(Constants.KEY_MO_PREPARE_TPS_LINE)) {
				new ToolItem(tBar, SWT.SEPARATOR);
				itemFromPrepareTps = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_PREPARE_TPS_LINE);
				itemFromPrepareTps.setText(Message.getString("wip.prepare.tps"));
				itemFromPrepareTps.setImage(SWTResourceCache.getImage("copy"));
				itemFromPrepareTps.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						queryFromPrepareTpsAdapter();
					}
				});
//			}
//		}

	}
	
	protected void createToolItemPrepareBom(ToolBar tBar){
		if (Env.getAuthority() != null) {
			if (Env.getAuthority().contains(Constants.KEY_MO_COUNT_PREPARE_BOM)) {
				new ToolItem(tBar, SWT.SEPARATOR);
				itemQueryPrepare = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MO_COUNT_PREPARE_BOM);
				itemQueryPrepare.setText(Message.getString("wip.count.bom"));
				itemQueryPrepare.setImage(SWTResourceCache.getImage("preview"));
				itemQueryPrepare.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						queryPrepareBomAdapter();
					}
				});
			}
		}

	}
	
	protected void queryFromPrepareTpsAdapter(){
		try{
			TpsLinePrepareDialog prepareDialog = new TpsLinePrepareDialog(UI.getActiveShell());
			if(prepareDialog.open() == Dialog.OK) {
				MOGenerateContext context = new MOGenerateContext();
				context.setCategory(MOGenerateContext.CAGEGORY_PREPARE_NEW);
				context.setAdTable_MO(getTableManager().getADTable());
				context.setManufactureOrder(prepareDialog.getManufactureOrder());
				
				MOGenerateWizard wizard = new MOGenerateWizard(context);
				MOGenerateDialog mgd = new MOGenerateDialog(UI.getActiveShell(), wizard);
				context.setDialog(mgd);
				int code = mgd.open();
				if (code == Dialog.OK) {
					this.refresh();
				}			
			}
 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void queryPrepareBomAdapter(){
		try{
			ADManager adManager = Framework.getService(ADManager.class);
			List<ManufactureOrderBom> boms = adManager.getEntityList(Env.getOrgRrn(),
					ManufactureOrderBom.class,Integer.MAX_VALUE, "isPrepareMoLine = 'Y' ", null);
			PrepareBomDialog dialog = new PrepareBomDialog(UI.getActiveShell(), null);
			dialog.open();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	protected void createToolItemWmsIn(ToolBar tBar) {
		itemWmsIn = new ToolItem(tBar, SWT.PUSH);
		itemWmsIn.setText("WMS生产入库");
		itemWmsIn.setImage(SWTResourceCache.getImage("preview"));
		itemWmsIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				wmsInAdapter();
			}
		});
	}

	protected void wmsInAdapter() {

		String tableName = "WIPWms";
		ADTable invAdTable = null;
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			invAdTable = adManager.getADTable(0L, tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TableListManager listTableManager = new TableListManager(invAdTable);
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		ManufactureOrder mo = null;
		mo = this.selectedMO;
		WmsInDialog invDialog = new WmsInDialog(listTableManager, null, null,
				style, mo);
		if (invDialog.open() == IDialogConstants.OK_ID) {

		}
	}
	
	protected void createToolItemSubTime(ToolBar tBar) {
		itemSubTime = new ToolItem(tBar, SWT.PUSH);
		itemSubTime.setText("交货周期");
		itemSubTime.setImage(SWTResourceCache.getImage("barcode"));
		itemSubTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				subTimeAdapter();
			}
		});
	}
	protected void subTimeAdapter() {
		
		String tableName = "WIPManufactureOrderFan";
		ADTable invAdTable = null;
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			invAdTable = adManager.getADTable(0L, tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TableListManager listTableManager = new TableListManager(invAdTable);
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
		| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		SubTimeQueryDialog invDialog = new SubTimeQueryDialog(listTableManager, null, null, style);
		if(invDialog.open() == IDialogConstants.OK_ID){
			
		}
	}
}
