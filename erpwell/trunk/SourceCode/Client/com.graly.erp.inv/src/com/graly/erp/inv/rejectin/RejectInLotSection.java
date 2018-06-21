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
					// ����˻��������λMaterial���͵�����,Ĭ�����Ӧ�������Ѿ���Lot���д���
					//(��Ϊֻ���������ܳ��⣬Ȼ�����˻�),�������lotΪnull,��ʹΪMaterial����,
					//��ʾ�仹δ����δ��ˣ�Ҳ����ʾ�����β�����
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				else if(validLot(lot)) {
					// ���l��Ϊnull����ʾlot����Ӧ��������lines�л���inLine��Ӧ������һ��
					MovementLine l = this.isContainsLot(lot);
					if(l == null) {
						return;
					}
					// Batch��Material������Ҫ���õ�������
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
						// ��ʱLot.QtyCurrent����Ϊ��(��ʹ�û��ѳ���) ���Դ����ֵӦΪBigDecimal.ONE
						lineLot = pareseMovementLineLot(l, BigDecimal.ONE, lot);
					}
					addLineLotToTable(lineLot);
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ��addLot() ", e);
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
