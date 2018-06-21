package com.graly.erp.inv.rejectin;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.otherin.OtherInLotSection;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class RejectInLotSection extends OtherInLotSection {
	private static final Logger logger = Logger.getLogger(RejectInLotSection.class);

	public RejectInLotSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}

	public RejectInLotSection(ADTable adTable, MovementIn in,
			MovementLine movementInLine, List<MovementLine> lines,
			boolean isView) {
		super(adTable, in, movementInLine, lines, isView);
	}

	public RejectInLotSection(ADTable adTable, MovementIn in,
			MovementLine movementInLine) {
		super(adTable, in, movementInLine);
	}

	@Override
	protected void addLot() {
		String lotId = txtLotId.getText();
		try {			
			if(lotId != null && !"".equals(lotId)) {				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					// 如果退货入库中有位Material类型的物料,默认其对应的批次已经在Lot表中存在
					//(因为只有先入库才能出库，然后再退货),所以如果lot为null,即使为Material类型,
					//表示其还未入库或还未审核，也会提示该批次不存在
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				else if(validLot(lot)) {
					// 如果l不为null，表示lot所对应的物料在lines中或与inLine对应的物料一致
					MovementLine l = this.isContainsLot(lot);
					if(l == null) {
						return;
					}
					// Batch或Material类型需要设置调拨数量
					MovementLineLot lineLot = null;
					if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
							|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						RejectInQtySetupDialog resQtyDialog = new RejectInQtySetupDialog(UI.getActiveShell(),
								null, lot, null);
						int openId = resQtyDialog.open();
						if(openId == Dialog.OK) {
							lineLot = pareseMovementLineLot(l, resQtyDialog.getInputQty(), lot);
						} else if(openId == Dialog.CANCEL) {
							return;
						}
					} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
						// 此时Lot.QtyCurrent可能为零(已使用或已出库) 所以传入的值应为BigDecimal.ONE
						lineLot = pareseMovementLineLot(l, BigDecimal.ONE, lot);
					}
					addLineLotToTable(lineLot);
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

	@Override
	protected boolean validLot(Lot lot) {
		if(isContainsInLineLots(lot)) {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		boolean flag = false;
		if(lines != null && lines.size() > 0) {
			for(MovementLine line : lines) {
				if(line.getMaterialRrn().equals(lot.getMaterialRrn())) {
					flag = true;
					break;
				}
			}
		}
		if(!flag) {
			UI.showError(String.format(Message.getString("inv.material_by_lot_isnot_exist_inlines"),
					lot.getLotId(), lot.getMaterialId()));
			return false;
		}
		if (Lot.LOTTYPE_SERIAL.equals(lot.getLotType()) && !Lot.POSITION_OUT.equals(lot.getPosition())) {
			UI.showError(String.format(Message.getString("inv.lot_not_out"), lot.getLotId()));
			return false;
		} else {
			if (lot.getIsUsed()) {
				UI.showError(String.format(Message.getString("wip.lot_is_used"), lot.getLotId()));
				return false;
			}
		}
		return true;
	}
}
