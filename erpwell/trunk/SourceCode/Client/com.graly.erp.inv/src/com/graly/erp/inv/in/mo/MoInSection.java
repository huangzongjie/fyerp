package com.graly.erp.inv.in.mo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import com.graly.erp.base.MaterialQueryDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.DelInvMovementAuthorityManager;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.StockIn;
import com.graly.erp.inv.model.MovementIn.InType;
import com.graly.erp.wip.model.ManufactureOrder;
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
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class MoInSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MoInSection.class);

	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected ToolItem itemRef; // 退库
	private ADTable adTable;
	private MovementIn selectedIn;
	protected ToolItem itemWms;
	protected ToolItem itemPreview;

	public MoInSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				if(selectedIn != null) {
					if(selectedIn.getIsRef()) {
						refundAdapter();
					} else{
						editAdapter();
					}
				}
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemWmsRun(tBar);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRef(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);//丁军提高打印效率
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_WIN);
			queryDialog.open();
		}
	}

	protected void createToolItemWmsRun(ToolBar tBar) {
		itemWms = new AuthorityToolItem(tBar, SWT.PUSH, "WIP.WMS");
		itemWms.setText("立体库创单");
		itemWms.setImage(SWTResourceCache.getImage("copy"));
		itemWms.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				wmsRunAdapter();
			}
		});
	}
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIN_CREATEFROM);
		itemNew.setText(Message.getString("pur.copyfrom"));
		itemNew.setImage(SWTResourceCache.getImage("copy"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIN_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}
	
	protected void createToolItemRef(ToolBar tBar) {
		itemRef = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIN_EDIT);
		itemRef.setText(Message.getString("wip.moin_ref"));
		itemRef.setImage(SWTResourceCache.getImage("editor"));
		itemRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refundAdapter();
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIN_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	protected void newAdapter() {
		try {
			WzMoInContext context = new WzMoInContext();
			context.setCategory(WzMoInContext.CATEGORY_NEW_MOIN);
			context.setAdTable_WIN(tableManager.getADTable());
			WzMoInWizard wizard = new WzMoInWizard(context);
			WzMoInDialog dialog = new WzMoInDialog(UI.getActiveShell(), wizard);
			context.setDialog(dialog);
			
			int code = dialog.open();
			if(code == Dialog.OK) {
				MovementIn win = new MovementIn();
				win.setDocStatus(MovementIn.STATUS_DRAFTED);
				win.setOrgRrn(Env.getOrgRrn());
				win.setMoId(wizard.getContext().getMo().getDocId());
				win.setMoRrn(wizard.getContext().getMo().getObjectRrn());
				//暂不带到生产入库单中
//				win.setDescription(wizard.getContext().getMo().getComments());
				
				MoInDetailDialog detailDialog = new MoInDetailDialog(UI.getActiveShell(),
						context.getMo(), win, tableManager.getADTable(), context.getInLineLots());
				if(detailDialog.open() == Dialog.CANCEL) {
					win = (MovementIn)detailDialog.getMovementIn();
					if (win != null && win.getObjectRrn() != null) {
						selectedIn = win;
						refreshSection();
						refreshAdd(selectedIn);
					}
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoInSection : newAdapter() ", e);
		}
	}

	protected void deleteAdapter() {
		if (selectedIn != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedIn.getObjectRrn() != null) {
						if(DelInvMovementAuthorityManager.hasDeleteAuthority(Env.getUserRrn(),
								selectedIn.getWarehouseRrn(), selectedIn.getWarehouseId())) {
							INVManager invManager = Framework.getService(INVManager.class);
							invManager.deleteMovementIn(selectedIn, MovementIn.InType.WIN, Env.getUserRrn());
							this.refreshDelete(selectedIn);
							this.selectedIn = null;
							refreshSection();							
						}
					}
				}
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				logger.error("Error at MoInSection : deleteAdapter() ", e);
			}
		}
	}

	protected void editAdapter() {
		if (selectedIn == null || selectedIn.getMoId() == null) {
			UI.showWarning(Message.getString("inv.entityisnull"));
			return;
		}
		try {
			ManufactureOrder mo = new ManufactureOrder();
			ADManager adManager = Framework.getService(ADManager.class);
			selectedIn = (MovementIn)adManager.getEntity(selectedIn);
			mo.setObjectRrn(selectedIn.getMoRrn());
			mo = (ManufactureOrder)adManager.getEntity(mo);
			
			MoInDetailDialog detailDialog = new MoInDetailDialog(UI.getActiveShell(),
					mo, selectedIn, tableManager.getADTable(), null);
			if(detailDialog.open() == Dialog.CANCEL) {
				refreshSection();
				this.refreshUpdate(selectedIn);
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoInSection : editAdapter() ", e);
		}
	}
	
	protected void refundAdapter() {
		try {
			if (selectedIn == null || selectedIn.getMoRrn() == null) {
				UI.showWarning(Message.getString("inv.entityisnull"));
				return;
			}
			// 如果不是退库单并且selectedIn已审核，则要为selectedIn新建一个退库单
			// 否则直接打开selectedIn
			MovementIn refIn = null, preIn = null;
			boolean isAdd = false;
			if(selectedIn.getIsRef()) {
				refIn = selectedIn;
			} else if(!selectedIn.getIsRef()
					&& MovementIn.STATUS_APPROVED.equals(selectedIn.getDocStatus())) {
				isAdd = true;
				refIn = new MovementIn();
				refIn.setDocStatus(MovementIn.STATUS_DRAFTED);
				refIn.setOrgRrn(Env.getOrgRrn());
				refIn.setMoId(selectedIn.getDocId());
				refIn.setMoRrn(selectedIn.getObjectRrn());
				refIn.setWarehouseId(selectedIn.getWarehouseId());
				refIn.setWarehouseRrn(selectedIn.getWarehouseRrn());
				refIn.setLocatorRrn(selectedIn.getLocatorRrn());
				refIn.setIsRef(true);
				preIn = selectedIn;
			} else {
				return;
			}
			
			ManufactureOrder mo = new ManufactureOrder();
			ADManager adManager = Framework.getService(ADManager.class);
			selectedIn = (MovementIn)adManager.getEntity(selectedIn);
			mo.setObjectRrn(selectedIn.getMoRrn());
			mo = (ManufactureOrder)adManager.getEntity(mo);
			
			MoRefDetailDialog refDialog = new MoRefDetailDialog(UI.getActiveShell(),
					mo, refIn, preIn, tableManager.getADTable(), null);
			if(refDialog.open() == Dialog.CANCEL) {
				refIn = (MovementIn)refDialog.getMovementIn();
				if (refIn != null && refIn.getObjectRrn() != null) {
					selectedIn = refIn;
					refreshSection();
					if(isAdd) {
						refreshAdd(selectedIn);
					} else {
						this.refreshUpdate(selectedIn);
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoInSection : refundAdapter() ", e);
		}
	}

	protected void refreshSection() {
//		refresh();
		try {
			if (selectedIn != null && selectedIn.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedIn = (MovementIn) adManager.getEntity(selectedIn);
				setStatusChanged(selectedIn.getDocStatus());
			} else {
				setStatusChanged("");
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			selectedIn = null;
		}
	}

	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("MoInSection : getADTableOfRequisition()", e);
		}
		return null;
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof MovementIn) {
			selectedIn = (MovementIn) obj;
			setStatusChanged(selectedIn.getDocStatus());
		} else {
			selectedIn = null;
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (MovementIn.STATUS_DRAFTED.equals(status)) {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if (MovementIn.STATUS_CLOSED.equals(status)) {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else if(MovementIn.STATUS_APPROVED.equals(status)) {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
		// 若入库单为1.已审核的并不是退货单 或者 2.为Draft的并是退货单时，itemRef才可用
		if(selectedIn != null && selectedIn.getIsRef()
				&& MovementIn.STATUS_DRAFTED.equals(selectedIn.getDocStatus())) {
			itemRef.setEnabled(true);
			itemEdit.setEnabled(false);
		} else if(selectedIn != null && !selectedIn.getIsRef()
				&& MovementIn.STATUS_APPROVED.equals(selectedIn.getDocStatus())) {
			itemRef.setEnabled(true);
		} else {
			itemRef.setEnabled(false);
		}
	}
	
	protected void wmsRunAdapter() {
		try {
			 INVManager invManager = Framework.getService(INVManager.class);
			 
			 String whereClause = "isActive = 'Y' and receiptType='WIN' AND wmsRead =2 and erpMovement is null and quality is not null";
			 List<String> mos=  invManager.getMovementInByWms(whereClause);
//			 List<StockIn> stockIns = adManager.getEntityList(Env.getOrgRrn(), StockIn.class,Integer.MAX_VALUE,"","");
			 for(String mo:mos){
				 ADManager adManager = Framework.getService(ADManager.class);
				 		 
				 String whereClause2 =  whereClause+" and receiptId ='"+mo+"'";
				 List<StockIn> stockIns = adManager.getEntityList(Env.getOrgRrn(), StockIn.class,Integer.MAX_VALUE,whereClause2,"");
				
				
				List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
				 BigDecimal total = BigDecimal.ZERO;
				 BigDecimal current = null;
				 int i =0;
				 MovementLine ml = new MovementLine();
					ml.setOrgRrn(Env.getOrgRrn());
				 for(StockIn si : stockIns){
					 Date now = Env.getSysDate();
					 MovementLineLot inLineLot = new MovementLineLot();
						inLineLot.setOrgRrn(Env.getOrgRrn());
						inLineLot.setIsActive(true);
						inLineLot.setCreated(now);
						inLineLot.setCreatedBy(Env.getUserRrn());
						inLineLot.setUpdated(now);
						inLineLot.setUpdatedBy(Env.getUserRrn());
						
						Lot lot = invManager.getLotByLotId(Env.getOrgRrn(), si.getBatch());
						ml.setMaterialRrn(lot.getMaterialRrn());
						inLineLot.setLotRrn(lot.getObjectRrn());
						inLineLot.setLotId(lot.getLotId());
						inLineLot.setMaterialRrn(lot.getMaterialRrn());
						inLineLot.setMaterialId(lot.getMaterialId());
						inLineLot.setMaterialName(lot.getMaterialName());
						inLineLot.setQtyMovement(si.getQuality());
						
						current = inLineLot.getQtyMovement() == null ? BigDecimal.ZERO : inLineLot.getQtyMovement();
						total = total.add(current);
						lineLots.add(inLineLot);
						++i;
				 }
				 List<MovementLine> lines = new ArrayList<MovementLine>();
				
				ml.setMovementLots(lineLots);
				ml.setQtyMovement(total);
				
				lines.add(ml);
				MovementIn win = generateWin(stockIns.get(0));
				win = invManager.saveMovementInByWms(win, lines, getMovementInType(), Env.getUserRrn(),stockIns);
				 
			 }
		 
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoInSection : newAdapter() ", e);
		}
	}
	
	protected InType getMovementInType() {
		return MovementIn.InType.WIN;
	}
	
	public MovementIn generateWin(StockIn si){
		MovementIn win = new MovementIn();
		win.setOrgRrn(Env.getOrgRrn());
		win.setDocStatus(MovementIn.STATUS_DRAFTED);
		win.setWarehouseRrn(151043L);
		win.setWmsWarehouse("自动化A库");
		win.setKind(si.getWorkcenterId());
		
		try {
			 INVManager invManager = Framework.getService(INVManager.class);
			 ADManager adManager = Framework.getService(ADManager.class);
			 WipManager wipManager = Framework.getService(WipManager.class);
			 ManufactureOrder mo = wipManager.getMoById(Env.getOrgRrn(), si.getReceiptId());
			 win.setMoRrn(mo.getObjectRrn());
			win.setMoId(mo.getDocId());
			 
//			 List<StockIn> stockIns = adManager.getEntityList(Env.getOrgRrn(), StockIn.class,Integer.MAX_VALUE,"","");

				 
		 
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoInSection : newAdapter() ", e);
		}
		return win;
		
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
			//保存打印次数
			MovementIn win = selectedIn;
			Long time = win.getPrintTime();
			if(time == null){
				win.setPrintTime(1L);
			}else{
				win.setPrintTime(time + 1L);
			}
			ADManager manager = Framework.getService(ADManager.class);
			win = (MovementIn) manager.saveEntity(win, Env.getUserRrn());
			
			String report = "win_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(!(Movement.STATUS_APPROVED.equals(win.getDocStatus()) || Movement.STATUS_COMPLETED.equals(win.getDocStatus()))){
				UI.showWarning(Message.getString("common.is_not_approved")+","+Message.getString("common.can_not_print"));
				return;
			}
			if(win == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = win.getObjectRrn();
			userParams.put("INV_OBJECT_RRN", String.valueOf(objectRrn));

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
//			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
}
