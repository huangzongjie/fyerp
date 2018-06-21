package com.graly.erp.inv.in.mo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.MovementIn.InType;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class MoRefDetailSection extends MoInDetailSection {
	private static final Logger logger = Logger.getLogger(MoRefDetailSection.class);
	protected static final String ID_DESCRIPTION = "description";
	private MovementIn preIn; // 原先生产入库的入库单
	private ADManager adManager;

	public MoRefDetailSection(ADTable lienLotTable, ADTable winTable,
			ManufactureOrder mo, MovementIn refIn, MovementIn preIn,
			List<MovementLineLot> lineLots, MoInDetailDialog dialog) {
		super(lienLotTable, winTable, mo, refIn, lineLots, dialog);
		this.preIn = preIn;
	}
	
	public void refresh() {
		super.refresh();
		// 将除备注控件外，其余置为不可用
		for(Form form : this.getDetailForms()) {
			for(IField ifield : form.getFields().values()) {
				if(!ID_DESCRIPTION.equals(ifield.getId())) {
					ifield.setEnabled(false);
				}
			}
		}
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
//						if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
//							// Material类型可入库数量=工作令已完成数量-工作令已入库数量
//							lot.setQtyTransaction(mo.getQtyReceive().subtract(mo.getQtyIn()));							
//						}
						Warehouse wh = getWarehouse();
						MoRefQtySetupDialog refQtyDialog = new MoRefQtySetupDialog(UI.getActiveShell(),
								null, lot, wh);
						int openId = refQtyDialog.open();
						if(openId == Dialog.OK) {
							lineLot = pareseMovementLineLot(inLine, refQtyDialog.getInputQty(), lot, false);
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
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoRefDetailSection ：addLot() ", e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	// 当退库时，退库的批次的数量是原生产入库仓库的库存。由于退库单保存后只能审核，所以不存在
	// preIn.getWarehouseRrn()为空情况
	protected Warehouse getWarehouse() throws Exception {
		if(adManager == null) {
			adManager = Framework.getService(ADManager.class);
		}
		Warehouse wh = null;
		if(this.preIn != null && preIn.getWarehouseRrn() != null) {
			wh = new Warehouse();
			wh.setObjectRrn(preIn.getWarehouseRrn());
			wh.setWarehouseId(preIn.getWarehouseId());
		}
		return wh;
	}

	// 将退货数量设置用户输入的相反数
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
		// 将用户输入的退库数量的相反数设置到qtyMovement中
		inLineLot.setQtyMovement(inQty.negate());
		return inLineLot;
	}
	
	@Override
	protected boolean validLot(Lot lot) {
		// 批次位置只能在仓库中，否则，未入库的也可以退库
//		!Lot.POSITION_WIP.equals(lot.getPosition()) && !Lot.POSITION_WIN.equals(lot.getPosition()) && 
		if(!Lot.POSITION_WIP.equals(lot.getPosition()) && !Lot.POSITION_WIN.equals(lot.getPosition())
				&& !Lot.POSITION_INSTOCK.equals(lot.getPosition()) && !Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
			UI.showError(String.format(Message.getString("wip.lot_not_in_stock"), lot.getLotId()));
			return false;
		}
		// 验证批次是否对应入库的工作令
		if(lot.getMoRrn() == null || !lot.getMoRrn().equals(mo.getObjectRrn())
				&& !Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
			UI.showError(String.format(Message.getString("wip.lot_is_not_belong_mo"), mo.getDocId()));
			return false;
		}
		// 验证退库的批次必须属于已审核的生产入库单preIn，由于退库单保存后只能审核，所以不存在preIn为空情况
//		if(preIn != null && !preIn.getObjectRrn().equals(lot.getInRrn())) {
//			UI.showError(String.format(Message.getString("inv.lot_is_not_belong_in"), preIn.getDocId()));
//			return false;
//		}
		// 若为Batch或Material类型，如果列表中存在，则不能重复入库
		if(isContainsInLineLots(lot)) {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		return true;
	}

	// 保存后只能审核
	@Override
	protected void setStatusChanged() {
		if (win != null) {
			String status = win.getDocStatus();
			if (MovementIn.STATUS_DRAFTED.equals(status)) {
				if(win.getObjectRrn() == null) {
					itemApprove.setEnabled(true);
					itemSeniorApprove.setEnabled(true);
					itemSave.setEnabled(true);
					itemDelete.setEnabled(true);
				} else {
					itemApprove.setEnabled(true);
					itemSeniorApprove.setEnabled(true);
					itemSave.setEnabled(false);
					itemDelete.setEnabled(false);
				}
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
}
