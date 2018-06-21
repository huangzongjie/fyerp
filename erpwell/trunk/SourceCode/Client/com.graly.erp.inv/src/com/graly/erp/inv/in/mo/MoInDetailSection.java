package com.graly.erp.inv.in.mo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.WarehouseEntityForm;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.MovementIn.InType;
import com.graly.erp.inv.otherin.OtherInLotTableManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class MoInDetailSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(MoInDetailSection.class);	
	protected static final String ID_LOCATOR_RRN = "locatorRrn";
	protected static final String ID_KIND = "kind"; //相关单位
	protected int mStyle = SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK;
	
	protected MoInDetailDialog dialog;
	protected Section section;
	protected IFormPart spart;
	protected ADTable winTable;
	protected ManufactureOrder mo;
	protected MovementIn win;
	protected List<MovementLineLot> lineLots;
	
	protected ToolItem itemApprove;
	protected ToolItem itemSeniorApprove;
	protected ToolItem itemPreview;
	protected ToolItem itemMaterial;
	protected List<Form> detailForms = new ArrayList<Form>();
	protected CTabFolder tabs;
	protected IField iLocator;

	public MoInDetailSection(ADTable lienLotTable, ADTable winTable, ManufactureOrder mo,
			MovementIn win, List<MovementLineLot> lineLots, MoInDetailDialog dialog) {
		super(lienLotTable);
		this.winTable = winTable;
		this.mo = mo;
		this.win = win;
		this.dialog  = dialog;
		this.lineLots = lineLots;
		getInMovementLine();
	}

	protected void createParentContent(Composite client, final FormToolkit toolkit) {
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 5;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : winTable.getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			WarehouseEntityForm itemForm = new WarehouseEntityForm(getTabs(), SWT.NONE, win, tab, mmng);
			detailForms.add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		setLocatorContent();
		initEnabled();
	}
	
	protected void initEnabled() {
		if(win.getObjectRrn() == null || MovementIn.STATUS_APPROVED.equals(win.getDocStatus())) {
			itemApprove.setEnabled(false);
			itemSeniorApprove.setEnabled(false);
		}
	}
	
	private void setLocatorContent() {
		//设置LocatorRrn显示
		Form detailForm = this.getDetailForms().get(0);
		iLocator = (IField)detailForm.getFields().get(ID_LOCATOR_RRN);
//		if(!(win.getDocStatus().equals(MovementIn.STATUS_DRAFTED))) {
//			iLocator.setEnabled(false);
//		} else {
//			iLocator.setEnabled(true);
//		}
		iLocator.refresh();
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new OtherInLotTableManager(adTable);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		lotManager.updateView(viewer);
	}
	
	// 重载addLot(), 实现将lot转化为movementLineLot
	protected void addLot() {
		String lotId = txtLotId.getText();
		try {			
			if(lotId != null && !"".equals(lotId)) {				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				if(validLot(lot)) {
					MovementLineLot lineLot = null;
					MovementLine inLine = null;
					if(lines != null && lines.size() > 0) 
						inLine = lines.get(0);
					
					// Batch类型或Material类型需要设置入库数量
					if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
							|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
							// Material类型可入库数量=工作令已完成数量-工作令已入库数量
							lot.setQtyTransaction(mo.getQtyReceive().subtract(mo.getQtyIn()));							
						}
						Warehouse wh = invManager.getWriteOffWarehouse(Env.getOrgRrn());
						MoInQtySetupDialog inQtyDialog = new MoInQtySetupDialog(UI.getActiveShell(),
								null, lot, wh);
						int openId = inQtyDialog.open();
						if(openId == Dialog.OK) {
							lineLot = pareseMovementLineLot(inLine, inQtyDialog.getInputQty(), lot, false);
						} else if(openId == Dialog.CANCEL) {
							return;
						}
					} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
						lineLot = pareseMovementLineLot(inLine, lot.getQtyCurrent(), lot, false);
					}
					getLineLots().add(lineLot);
					refreshLineLotTable();
					setDoOprationsTrue();
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	protected boolean validLot(Lot lot) {
		// 如果批次不在生产线中且不是生产入库且不为Material类型，则不能入库
		if(!Lot.POSITION_WIP.equals(lot.getPosition()) && !Lot.POSITION_WIN.equals(lot.getPosition())
				&& !Lot.POSITION_INSTOCK.equals(lot.getPosition()) && !Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
			UI.showError(String.format(Message.getString("wip.lot_not_in_wip"), lot.getLotId()));
			return false;
		}
		// 验证批次是否对应入库的工作令
//		if(lot.getMoRrn() == null || !lot.getMoRrn().equals(mo.getObjectRrn())
//				&& !Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
//			UI.showError(String.format(Message.getString("wip.lot_is_not_belong_mo"), mo.getDocId()));
//			return false;
//		}
		// 若为Batch或Material类型，如果列表中存在，则不能重复入库
		if(isContainsInLineLots(lot)) {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		return true;
	}
	
	protected boolean isContainsInLineLots(Lot lot) {
		for(MovementLineLot lineLot : getLineLots()) {
			if(lineLot.getLotRrn().equals(lot.getObjectRrn()))
				return true;
		}
		return false;
	}
	
	protected MovementLineLot pareseMovementLineLot(MovementLine line,
			BigDecimal inQty, Lot lot, boolean isGenNewLotId) throws Exception {
		Date now = Env.getSysDate();
		MovementLineLot inLineLot = new MovementLineLot();
		inLineLot.setOrgRrn(Env.getOrgRrn());
		inLineLot.setIsActive(true);
		inLineLot.setCreated(now);
		inLineLot.setCreatedBy(Env.getUserRrn());
		inLineLot.setUpdated(now);
		inLineLot.setUpdatedBy(Env.getUserRrn());
		
		if(win != null) {
			inLineLot.setMovementRrn(win.getObjectRrn());
			inLineLot.setMovementId(win.getDocId());
		}
		if(line != null) {
			inLineLot.setMovementLineRrn(line.getObjectRrn());			
		}
		inLineLot.setLotRrn(lot.getObjectRrn());
		inLineLot.setLotId(lot.getLotId());
		
		inLineLot.setMaterialRrn(lot.getMaterialRrn());
		inLineLot.setMaterialId(lot.getMaterialId());
		inLineLot.setMaterialName(lot.getMaterialName());
		// 将用户输入的出库数量设置到outLineLot.qtyMovement中
		inLineLot.setQtyMovement(inQty);
		return inLineLot;
	}

	public void createToolBar(Section section) {
		final ToolBar toolBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemApprove(toolBar);
		createToolItemSeniorApprove(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemMaterial(toolBar);
		createToolItemSave(toolBar);
		createToolItemDelete(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemPreview(toolBar);
		section.setTextClient(toolBar);
		this.setStatusChanged();
	}


	protected void createToolItemMaterial(ToolBar tBar) {
		itemMaterial = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIN_MATERIAL);
		itemMaterial.setText(Message.getString("inv.win_material"));
		itemMaterial.setImage(SWTResourceCache.getImage("volume"));
		itemMaterial.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				materialAdapter();
			}
		});
	}
	
	protected void materialAdapter() {
		try {
			Material material = win.getMo().getMaterial();
			if(material != null && material.getObjectRrn() != null){
				EditVolumeDialog evd = new EditVolumeDialog(UI.getActiveShell(),form,material);
				if(evd.open() == Dialog.OK){
					ADManager adManager =  Framework.getService(ADManager.class);
					win = (MovementIn) adManager.getEntity(win);
					refresh();
				}
			}
		} catch (Exception e) {
			logger.error("MoInDetailSection : materialAdapter()", e);
		}
	}

	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIN_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}
	
	protected void createToolItemSeniorApprove(ToolBar tBar) {
		itemSeniorApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIN_SENIORAPPROVED);
		itemSeniorApprove.setText(Message.getString("common.seniorapprove"));
		itemSeniorApprove.setImage(SWTResourceCache.getImage("approve"));
		itemSeniorApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				seniorApproveAdapter();
			}
		});
	}
	
	protected void seniorApproveAdapter() {
		approveMethod(true);
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
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void approveAdapter() {
		approveMethod(false);
	}

	private void approveMethod(boolean seniorApprove) {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			ADManager adManager = Framework.getService(ADManager.class);
			win = (MovementIn)adManager.getEntity(win);
			INVManager invManager = Framework.getService(INVManager.class);
			// 不能将approveMovementIn()返回的最新的赋给onIn,因为这样在多个用户操作时可以多次审核
			if(seniorApprove){//高级审核不检查物料核销数量
				win = invManager.approveMovementIn(win, getMovementInType(), seniorApprove, Env.getUserRrn());
			}else{
				win = invManager.approveMovementIn(win, getMovementInType(), Env.getUserRrn());
			}
			
			UI.showInfo(Message.getString("common.approve_successed"));
			
			this.refresh();
			this.setStatusChanged();
			setIsSaved(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void saveAdapter() {
		try {
			boolean saveFlag = true;
			for (Form detailForm : getDetailForms()) {
				if (!detailForm.saveToObject()) {
					saveFlag = false;
					return;
				}
			}
			if (saveFlag) {
				if (getLineLots() == null || getLineLots().size() == 0) {
					UI.showError(Message.getString("inv.no_lot_to_in_warehouse"));
					return;
				}
				
				if(win.getObjectRrn() == null){
					win.setMoRrn(mo.getObjectRrn());
					win.setMoId(mo.getDocId());
					lines = this.generateMovementLine();
				} else {
					lines = getInMovementLine();
				}
				IField kind = (IField)this.getDetailForms().get(0).getFields().get(ID_KIND);
				if(kind != null && kind.getValue() instanceof String) {
					win.setKind((String)kind.getValue());
				}
				
				BigDecimal total = BigDecimal.ZERO;
				BigDecimal current = null;
				for (MovementLineLot lineLot : getLineLots()) {
					current = lineLot.getQtyMovement() == null ? BigDecimal.ZERO : lineLot.getQtyMovement();
					total = total.add(current);
				}
				lines.get(0).setQtyMovement(total);
				lines.get(0).setMovementLots(getLineLots());
				
				INVManager invManager = Framework.getService(INVManager.class);
				win = invManager.saveMovementInLine(win, lines, getMovementInType(), Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				this.refresh();
				this.setStatusChanged();
				this.setIsSaved(true);
				
//				form.getMessageManager().setAutoUpdate(true);
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at winOfLotSection : buttonPressed()", e);
			return;
		}
	}

	/**
	 * @return MovementIn.InType
	 */
	protected InType getMovementInType() {
		return MovementIn.InType.WIN;
	}
	
	public MovementIn getMovementIn() {
		return win;
	}
	
	protected List<MovementLine> getInMovementLine() {
		try {
			if(win != null && win.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				win = (MovementIn)adManager.getEntity(win);
				lines = win.getMovementLines();				
			}
		} catch(Exception e) {
		}
		return lines;
	}
	
	protected Long getLocationRrn() {
//		if(iLocator != null && iLocator.getValue() != null && iLocator.getValue() != "") {
//			Long locatorRrn = Long.parseLong(iLocator.getValue().toString());
//			return locatorRrn;
//		}
		return null;
	}

	/* 删除Lot列表中选中的Lot*/
	protected void deleteAdapter() {
		try {
			TableItem[] items = viewer.getTable().getSelection();
        	if (items != null && items.length > 0){
        		TableItem item = items[0];
        		Object obj = item.getData();
        		if(obj instanceof MovementLineLot) {
        			boolean confirmDelete = UI.showConfirm(Message
        					.getString("common.confirm_delete"));
        			if (confirmDelete) {
        				delete((MovementLineLot)obj);
        			}
        		}
        	}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	protected void delete(MovementLineLot lineLot) {
		getLineLots().remove(lineLot);
		// 
		refreshLineLotTable();
		setDoOprationsTrue();
	}

	protected List<MovementLineLot> getMovementLineLot() throws Exception {
		List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
		if(win == null || win.getObjectRrn() == null)
			return lineLots;
		String whereClause = " movementRrn = '" + win.getObjectRrn() + "' ";
		ADManager adManager = Framework.getService(ADManager.class);
		lineLots = adManager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,
				Integer.MAX_VALUE, whereClause, null);
		return lineLots;
	}
	
	public void refresh() {
		for(Form detailForm : getDetailForms()) {
			detailForm.setObject(win);
			detailForm.loadFromObject();
		}
		super.refresh();
		setLocatorValue();//设置库位值
		form.getMessageManager().removeAllMessages();
	}
	
	private void setLocatorValue(){//设置库位值
		List<MovementLine> l = this.getInMovementLine();
		if(l != null && l.size() != 0 && iLocator != null){
			iLocator.setValue(l.get(0).getLocatorId());
			iLocator.refresh();
		}
	}
	
	public void refreshLineLotTable() {
		super.refresh();
	}
	
	protected List<?> getInput() {
		return getLineLots();
	}
	
	protected List<MovementLine> generateMovementLine() {
		List<MovementLine> list = new ArrayList<MovementLine>();
		MovementLine inLine = new MovementLine();
		inLine.setOrgRrn(Env.getOrgRrn());
		inLine.setMaterialRrn(mo.getMaterialRrn());
		inLine.setUomId(mo.getMaterial().getInventoryUom());
		inLine.setQtyMovement(BigDecimal.ZERO);
		list.add(inLine);
		return list;
	}

	protected void initTableContent() {
		try {
			// 如果在构造DetailSection时没有传入lineLots，则重DB中获得
			if(getLineLots() == null || getLineLots().size() == 0) {
				if(win != null && win.getObjectRrn() != null) {
					lineLots = getMovementLineLot();
				}
				Collections.sort(lineLots, new LotIdComparator());
			}
			refresh();			
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error : ", e);
		}
	}

	protected void setStatusChanged() {
		if (win != null) {
			String status = win.getDocStatus();
			if (MovementIn.STATUS_DRAFTED.equals(status)) {
				itemApprove.setEnabled(true);
				itemSeniorApprove.setEnabled(true);
				itemSave.setEnabled(true);
				itemDelete.setEnabled(true);
			} else {
				itemApprove.setEnabled(false);
				itemSeniorApprove.setEnabled(false);
				itemSave.setEnabled(false);
				itemDelete.setEnabled(false);
			}
		} else {
			itemApprove.setEnabled(false);
			itemSeniorApprove.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}

	public CTabFolder getTabs() {
		return tabs;
	}

	public void setTabs(CTabFolder tabs) {
		this.tabs = tabs;
	}

	public List<Form> getDetailForms() {
		return detailForms;
	}

	public void setDetailForms(List<Form> detailForms) {
		this.detailForms = detailForms;
	}
	
	class LotIdComparator implements Comparator<MovementLineLot> {
		public int compare(MovementLineLot obj1, MovementLineLot obj2) {
			if(obj1 != null && obj2 != null) {
				if(obj1.getLotId() != null && obj2.getLotId() != null);
				return obj1.getLotId().compareTo(obj2.getLotId());
			}
			return 0;
		}
	}

	public List<MovementLineLot> getLineLots() {
		if(lineLots == null)
			lineLots = new ArrayList<MovementLineLot>();
		return lineLots;
	}

	public void setLineLots(List<MovementLineLot> lineLots) {
		this.lineLots = lineLots;
	}
}
