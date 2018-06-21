package com.graly.erp.inv.out;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.model.Storage;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.WarehouseEntityForm;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.erp.inv.out.serialnum.OutSerialNumberDialog;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class OutLineEntryBlock extends ParentChildEntityBlock {
	private static final Logger logger = Logger.getLogger(OutLineEntryBlock.class);
	protected ToolItem itemLot;
	protected ToolItem itemRackLot;
	protected ToolItem itemSelectLot;
	protected ToolItem itemApprove;
	protected ToolItem itemOutSerial;
	protected ToolItem itemPreview;
	protected ToolItem itemAutoSelectLot;
	private static final String INV_OUT_AUTO_SELECT_LOT = "Inv.Out.AutoSelectLot";

	protected  boolean hasShow = false;
	protected  boolean souFlag = false;
	protected ToolItem itemWms;
	protected MovementLine selectedOutLine;
	protected boolean flag = false;
	protected final String REPORT_FILE_NAME = "sout_report.rptdesign";
	
	public OutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable){
		super(parentTable, parentObject, whereClause, childTable);
	}
	
	public OutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable, boolean flag){
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
	}
	
	public OutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable, boolean flag,boolean souFlag){
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
		this.souFlag = souFlag;
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);	
		refresh();
		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
		setParenObjectStatusChanged();
	}
	
	protected void createParentContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : parentTable.getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			OutEntityForm itemForm = new OutEntityForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		if(Env.getOrgRrn()==139420L){//--wms
			this.section.addFocusListener(new FocusAdapter(){
				@Override
				public void focusGained(FocusEvent e) {
					if(!hasShow && souFlag){
						hasShow= true;
						MovementOut out = (MovementOut)parentObject;
						if(out.getWmsWarehouse()!=null){
							return;
						}
						List<Material> wmsMaterials = getWmsMaterial();
						if(wmsMaterials!=null && wmsMaterials.size() >0){
							WmsStorageDialog wmsDialog = new WmsStorageDialog(Display.getCurrent().getActiveShell(),null,out, wmsMaterials);
							wmsDialog.open();
						}
					}
				}
			});
			this.section.setFocus();
		}
	}
	
	protected void createViewAction(StructuredViewer viewer){
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					Object obj = ((StructuredSelection) event.getSelection()).getFirstElement();
					if(obj instanceof MovementLine) {
						selectedOutLine = (MovementLine)obj;
					} else {
						selectedOutLine = null;
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		if(Env.getOrgRrn() ==139420L){
			createToolItemOpenWms(tBar);
		}
		createToolItemAutoSelectLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRackLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSelectLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		//此处做出修改，如果是奔泰环境，且相关单位包含“售后”等字眼时，审核人只有罗小华
		
		createToolItemApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemOutSerial(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		section.setTextClient(tBar);
	}
	private void createToolItemOpenWms(ToolBar tBar) {
		itemWms = new ToolItem(tBar, SWT. PUSH);
		itemWms.setText("立体库 ");
		itemWms.setImage(SWTResourceCache.getImage("barcode"));
		itemWms.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				openWmsAdapter();
			}
		});
	}

//	开能添加自动选批，奔泰如果需要改功能请先分析是否符合逻辑
	private void createToolItemAutoSelectLot(ToolBar tBar) {
		itemAutoSelectLot = new AuthorityToolItem(tBar, SWT.PUSH, INV_OUT_AUTO_SELECT_LOT);;
		itemAutoSelectLot.setText("自动选批");
		itemAutoSelectLot.setImage(SWTResourceCache.getImage("barcode"));
		itemAutoSelectLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				autoSelectLotAdapter();
			}
		});
	}
	
	protected void autoSelectLotAdapter() {
		try {
			if(selectedOutLine != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				MovementOut out = (MovementOut)parentObject;
				out = (MovementOut)adManager.getEntity((MovementOut)out);
				parentObject = out;
				selectedOutLine = (MovementLine)adManager.getEntity(selectedOutLine);
				INVManager invManager = Framework.getService(INVManager.class);
//				List<Lot> lots = invManager.getOptionalOutLot(selectedOutLine);
				List<Lot> lots =null;
				if(Env.getOrgRrn()== 139420L){
					if(out.getWmsWarehouse()!=null && out.getWmsWarehouse().length()>0){
						lots = invManager.getOptionalOutLotInWms(selectedOutLine);
					}else{
						lots = invManager.getOptionalOutLotNoWms(selectedOutLine);
					}
				}else{
					 lots = invManager.getOptionalOutLot(selectedOutLine);
				}
				if(lots == null || lots.size() == 0) {
					String name = selectedOutLine.getMaterialName();
					UI.showInfo(String.format(Message.getString("inv.material_no_lot"), name));
					return;
				}
				// 打开批次界面，可以进行删除、保存等操作
				LotSelectFromDbDialogOld dialog = new LotSelectFromDbDialogOld(UI.getActiveShell(),
						parentObject, selectedOutLine, lots);
				if (dialog.open() == Dialog.CANCEL) {
					selectedOutLine = null;
					this.viewer.setSelection(null);
					parentObject = adManager.getEntity((MovementOut)parentObject);
					refresh();
				}
			} else {
				UI.showWarning(Message.getString("inv.entityisnull"));
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	   protected void createToolItemRackLot(ToolBar tBar) {
		   itemRackLot = new ToolItem(tBar, SWT. PUSH);
		   itemRackLot .setText("货架管理" );
		   itemRackLot .setImage(SWTResourceCache.getImage( "barcode" ));
		   itemRackLot .addSelectionListener( new SelectionAdapter() {
	               @Override
	               public void widgetSelected(SelectionEvent event) {
	                    rackLotAdapter();
	              }
	        });
	  }
	
		protected void rackLotAdapter() {
			try {
				MovementOut out = (MovementOut)parentObject;
				if (out != null && out.getObjectRrn() != null) {
					ADManager adManager = Framework.getService(ADManager.class);
					out = (MovementOut)adManager.getEntity((MovementOut)out);
					parentObject = out;
					List<MovementLine> lines = new ArrayList<MovementLine>();
					if (selectedOutLine != null) {
						selectedOutLine = (MovementLine)adManager.getEntity(selectedOutLine);					
					}
					List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), getTableManager().getADTable().getObjectRrn(), 
							Env.getMaxResult(), getWhereClause(), null);
					for(ADBase adBase : list) {
						if(adBase instanceof MovementLine)
							lines.add((MovementLine)adBase);
					}
					if((lines == null || lines.size() == 0) && selectedOutLine == null) 
						return;
					RackLotDialog od = createRackLotDialog(lines);
					if (od.open() == Dialog.CANCEL) {
						selectedOutLine = null;
						this.viewer.setSelection(null);
						out = (MovementOut)adManager.getEntity((MovementOut)out);
						parentObject = out;
						refresh();
					}
				}
			} catch(Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				logger.error("Error at lotAdapter()", e);
			}
		}
		
		protected RackLotDialog createRackLotDialog(List<MovementLine> lines) {
			return new RackLotDialog(UI.getActiveShell(),
					parentObject, selectedOutLine, lines, false);
		}
	   
	protected void createToolItemSelectLot(ToolBar tBar) {
		itemSelectLot = new ToolItem(tBar, SWT.PUSH);
		itemSelectLot.setText(Message.getString("inv.optional_lot"));
		itemSelectLot.setImage(SWTResourceCache.getImage("barcode"));
		itemSelectLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectLotAdapter();
			}
		});
	}

	protected void selectLotAdapter() {
		try {
			if(selectedOutLine != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				MovementOut out = (MovementOut)parentObject;
				out = (MovementOut)adManager.getEntity((MovementOut)out);
				parentObject = out;
				selectedOutLine = (MovementLine)adManager.getEntity(selectedOutLine);
//				INVManager invManager = Framework.getService(INVManager.class);
//				List<Lot> lots = invManager.getOptionalOutLot(selectedOutLine);
//				if(lots == null || lots.size() == 0) {
//					String name = selectedOutLine.getMaterialName();
//					UI.showInfo(String.format(Message.getString("inv.material_no_lot"), name));
//					return;
//				}
				// 打开批次界面，可以进行删除、保存等操作
//				LotSelectFromDbDialog dialog = new LotSelectFromDbDialog(UI.getActiveShell(),
//						parentObject, selectedOutLine, lots);
				// 2012-03-20 Simon 进入选择批次画面时不查询批次，待进去后通过回车事件再查询
				LotSelectFromDbDialog dialog = new LotSelectFromDbDialog(UI.getActiveShell(),
						parentObject, selectedOutLine, null);//可选批次给赋空值
				if (dialog.open() == Dialog.CANCEL) {
					selectedOutLine = null;
					this.viewer.setSelection(null);
					parentObject = adManager.getEntity((MovementOut)parentObject);
					refresh();
				}
			} else {
				UI.showWarning(Message.getString("inv.entityisnull"));
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void createToolItemOutSerial(ToolBar tBar) {
		itemOutSerial = new ToolItem(tBar, SWT.PUSH);
		itemOutSerial.setText(Message.getString("inv.out_serial_num"));
		itemOutSerial.setImage(SWTResourceCache.getImage("barcode"));
		itemOutSerial.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				genOutSerialAdapter();
			}
		});
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
	
	protected void genOutSerialAdapter() {
		if(selectedOutLine == null) {
			UI.showWarning(Message.getString("inv.entityisnull"));
			return;
		}
		if(Lot.LOTTYPE_SERIAL.equals(selectedOutLine.getLotType())) {
			UI.showError(String.format(Message.getString("inv.should_not_gen_outserial"),
					selectedOutLine.getMaterialId()));
			return;
		}
		OutSerialNumberDialog dialog = new OutSerialNumberDialog(UI.getActiveShell(),
				(MovementOut)parentObject, selectedOutLine);
		if(dialog.open() == Dialog.CANCEL) {
		}
	}

	protected void previewAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			
			//保存打印次数
			MovementOut mo = (MovementOut)getParentObject();
			Long time = mo.getPrintTime();
			if(time == null){
				mo.setPrintTime(1L);
			}else{
				mo.setPrintTime(time + 1L);
			}
			ADManager manager = Framework.getService(ADManager.class);
			parentObject = manager.saveEntity(mo, Env.getUserRrn());			
			
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
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), getReportFileName(), params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	public void setParenObjectStatusChanged() {
		MovementOut mo = (MovementOut)parentObject;
		String status = "";
		if(mo != null && mo.getObjectRrn() != null) {
			status = mo.getDocStatus();			
		}
		if(MovementOut.STATUS_APPROVED.equals(status)) {
			itemSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
			itemOutSerial.setEnabled(true);
			itemPreview.setEnabled(true);
			itemAutoSelectLot.setEnabled(false);
		} else if(MovementOut.STATUS_DRAFTED.equals(status)) {
			itemSelectLot.setEnabled(true);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(true);
			itemOutSerial.setEnabled(false);
			itemPreview.setEnabled(true);
			itemAutoSelectLot.setEnabled(true);
		} else if(MovementOut.STATUS_CLOSED.equals(status)) {
			itemSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
			itemOutSerial.setEnabled(true);
			itemPreview.setEnabled(false);
			itemAutoSelectLot.setEnabled(false);
		} else {
			itemSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
			itemOutSerial.setEnabled(false);
			itemPreview.setEnabled(false);
			itemAutoSelectLot.setEnabled(false);
		}
		if(flag){
			itemLot.setEnabled(true);
			itemSelectLot.setEnabled(false);
			itemApprove.setEnabled(false);
			itemOutSerial.setEnabled(true);
			itemPreview.setEnabled(false);
			itemAutoSelectLot.setEnabled(false);
		}
		if(Env.getOrgRrn() ==12644730L){//---奔泰区域选中批次按钮不能用
			itemSelectLot.setEnabled(false);
		}
	}
	
	protected void setChildObjectStatusChanged() {
		OutLineProperties page = (OutLineProperties)this.detailsPart.getCurrentPage();
		page.setStatusChanged(((MovementOut)parentObject).getDocStatus());
	}
	
	protected void createToolItemLot(ToolBar tBar) {
		itemLot = new ToolItem(tBar, SWT.PUSH);
		itemLot.setText(Message.getString("inv.barcode"));
		itemLot.setImage(SWTResourceCache.getImage("barcode"));
		itemLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				lotAdapter();
			}
		});
	}
	
	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SOU_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}
	
	protected void lotAdapter() {
		try {
			MovementOut out = (MovementOut)parentObject;
			if (out != null && out.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				out = (MovementOut)adManager.getEntity((MovementOut)out);
				parentObject = out;
				List<MovementLine> lines = new ArrayList<MovementLine>();
				if (selectedOutLine != null) {
					selectedOutLine = (MovementLine)adManager.getEntity(selectedOutLine);					
				}
				List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), getTableManager().getADTable().getObjectRrn(), 
						Env.getMaxResult(), getWhereClause(), null);
				for(ADBase adBase : list) {
					if(adBase instanceof MovementLine)
						lines.add((MovementLine)adBase);
				}
				if((lines == null || lines.size() == 0) && selectedOutLine == null) 
					return;
				OutLineLotDialog od = createOutLotDialog(lines);
				if (od.open() == Dialog.CANCEL) {
					selectedOutLine = null;
					this.viewer.setSelection(null);
					out = (MovementOut)adManager.getEntity((MovementOut)out);
					parentObject = out;
					refresh();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at lotAdapter()", e);
		}
	}
	
	protected OutLineLotDialog createOutLotDialog(List<MovementLine> lines) {
		return new OutLineLotDialog(UI.getActiveShell(),
				parentObject, selectedOutLine, lines, false);
	}
	
	//销售出库审核方法
	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			MovementOut mo = (MovementOut)parentObject;
			if (mo != null && mo.getObjectRrn() != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				List<RacKMovementLot> rLots = invManager.getRackLots(mo);
	              if(rLots != null && rLots.size() > 0){
	            	  for(RacKMovementLot rLot:rLots){
	                      if(!validate(rLot.getLotRrn(),rLot.getRackRrn(),rLot.getQty())){
	                          return;
	                      }
	                  }
	            	  invManager.batchApproveRackMovementLot(Env. getOrgRrn(), rLots, Env.getUserRrn());
	              }

				
				//2012-03-26判断出库类型
				if(MovementOut.OutType.SOU.equals(getOutType())){//销售出库
					if(Env.getOrgRrn() == 12644730L){//奔泰出库审核：material类型无需挂批次，Batch类型需挂批次因此（原操作方式出库审核会校验批次信息）
						boolean materialFlag = true;//是否全部是material类型的出库行
						for(MovementLine movementLine : mo.getMovementLines()){
							if(!Lot.LOTTYPE_MATERIAL.equals(movementLine.getLotType())){
								materialFlag = false;
								break;
							}
						}
						if(materialFlag){
							parentObject = invManager.approveSalesMovementOut(mo, Env.getUserRrn(),true);
						}else{
							parentObject = invManager.approveSalesMovementOutBT(mo, Env.getUserRrn(),true);
						}
					}else{
						parentObject = invManager.approveSalesMovementOut(mo, Env.getUserRrn(),true);
					}
				} 
				else if(MovementOut.OutType.DOU.equals(getOutType())){//研发用料
					parentObject = invManager.approveDevelopMovementOut(mo,Env.getUserRrn(),true,true);
				}
				else{
					parentObject = invManager.approveMovementOut(mo, 
							getOutType(), Env.getUserRrn());
				}				
				// 需要用adManager再获得parentObject，打印时往数据库中记入了打印次数,如不重新获取会报该记录已被更新或删除的错误
				ADManager adManager = Framework.getService(ADManager.class);
				parentObject = adManager.getEntity((ADBase) parentObject);
				UI.showInfo(Message.getString("common.approve_successed"));
				setParenObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		}catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
		}
	
	
    protected boolean validate( Long lotRrn , Long rackRrn , BigDecimal qty) {
        try{
       INVManager invManager = Framework.getService(INVManager.class);
       BigDecimal qtyonhand = invManager.getWarehouseRackQtyonhand(lotRrn, rackRrn);
        if(qty.compareTo(qtyonhand)==1){
             UI. showInfo("货架批次库存数量不足" );
              return false ;
       }
        return true ;
 } catch(Exception e) {
        logger.error("Error at OutLineLotSection : saveAdapter() " + e);
       ExceptionHandlerManager. asyncHandleException(e);
        return false ;
 }
 }


	/* 在其对应的properties中调用此方法, 会根据parentObject的状态来初始化properties按钮是否可用*/
	public boolean isEnableByParentObject() {
		MovementOut out = (MovementOut)this.getParentObject();
		if(out == null) {
			return false;
		}
		String status = out.getDocStatus();
		if(Requisition.STATUS_CLOSED.equals(status)
				|| Requisition.STATUS_APPROVED.equals(status)
				|| Requisition.STATUS_COMPLETED.equals(status)
				|| Requisition.STATUS_INVALID.equals(status)
				|| flag) {
			return false;
		}
		return true;
	}

	public boolean isViewOnly() {
		return flag;
	}
	
	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.SOU;
	}

	public String getReportFileName() {
		return REPORT_FILE_NAME;
	}
	
	protected void openWmsAdapter() {
		try {
			List<Material> wmsMaterials = getWmsMaterial();
			MovementOut out = (MovementOut)parentObject;
			if(wmsMaterials!=null && wmsMaterials.size() >0){
				WmsStorageDialog wmsDialog = new WmsStorageDialog(Display.getCurrent().getActiveShell(),null,out, wmsMaterials);
				wmsDialog.open();
				viewer.refresh();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	public boolean isSouFlag() {
		return souFlag;
	}

	public void setSouFlag(boolean souFlag) {
		this.souFlag = souFlag;
	}
	
	private List<Material> getWmsMaterial(){
		MovementOut out = (MovementOut)parentObject;
		List<MovementLine> moveLines = out.getMovementLines();
		List<Material> wmsMaterials = new ArrayList<Material>();
		try {
		INVManager invManager = Framework.getService(INVManager.class);
		ADManager adManager =Framework.getService(ADManager.class);
		
		for(MovementLine moveLine : moveLines){
			BigDecimal wmsQtyOnhand =   invManager.getQtyInWmsStorage(moveLine.getMaterialId(),"环保良品");//立体库添加仓库
			
			if(wmsQtyOnhand!=null && wmsQtyOnhand.compareTo(BigDecimal.ZERO)>0){
				List<Storage> storages = adManager.getEntityList(Env.getOrgRrn(), Storage.class, Integer.MAX_VALUE," warehouseRrn = 151043 and materialRrn='"+moveLine.getMaterialRrn()+"'",null);
				Material wmsMaterial = new Material();
				wmsMaterial.setOrgRrn(Env.getOrgRrn());
				wmsMaterial.setObjectRrn(moveLine.getMaterialRrn());
				wmsMaterial.setMaterialId(moveLine.getMaterialId());
				if(storages!=null && storages.size() > 0){
					wmsMaterial.setQtyInitial(storages.get(0).getQtyOnhand());
				}else{
					wmsMaterial.setQtyInitial(BigDecimal.ZERO);//环保库存
				}
				wmsMaterial.setQtyOnHand(wmsQtyOnhand);//WMS库存
				wmsMaterial.setQtyTransit(moveLine.getQtyMovement());//总仓出库数量
				wmsMaterial.setQtyOut(BigDecimal.ZERO);//立体库出库数量
				wmsMaterial.setQtyIn(moveLine.getQtyMovement());//
				wmsMaterials.add(wmsMaterial);
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wmsMaterials;
	}
}
