package com.graly.erp.inv.adjust.in;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotGenerateSection extends AdjustInLotSection {
	protected List<Lot> genLots;

	public LotGenerateSection(ADTable adTable, MovementIn in,
			MovementLine movementInLine, List<Lot> genLots) {
		super(adTable, in, movementInLine);
		this.genLots = genLots;
	}

	@Override
	protected void initTableContent() {
		try {
        	if(genLots != null) {
        		List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
        		for(Lot lot : genLots) {
        			lot.setPosition(Lot.POSITION_GEN);
        			lineLots.add(this.pareseMovementLineLot(this.movementInLine,
        					lot.getQtyCurrent(), lot));
        		}
        		this.setLineLots(lineLots);
        		refresh();
        	}
        	if(getLineLots().size() > 0) {
        		setDoOprationsTrue();
    			((AdjustInLotTableManager)lotManager).setCanEdit(true);
    		}
        	setPrintEnable(false);
        } catch (Exception e) {
        }
	}
	
	// ��֤lot��Ӧ�����ϱ�����lines��, ����lot������IQC, GEN��WIP�ϲ�����δʹ�õ�
	protected boolean validLot(Lot lot) {
		if(isContainsInLineLots(lot)) {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		if (movementInLine != null || !lot.getMaterialRrn().equals(movementInLine.getMaterialRrn())) {
			UI.showError(Message.getString("inv.in_material_not_lot_material"));
			return false;
		}
		if (!(Lot.POSITION_IQC.equals(lot.getPosition()) ||
				Lot.POSITION_GEN.equals(lot.getPosition()) || Lot.POSITION_WIP.equals(lot.getPosition()))) {
			UI.showError(String.format(Message.getString("inv.lot_already_in"), lot.getLotId()));
			return false;
		} else {
			if (lot.getIsUsed()) {
				UI.showError(String.format(Message.getString("wip.lot_is_used"), lot.getLotId()));
				return false;
			}
		}
		return true;
	}

	@Override
	protected void saveAdapter() {
		try {
			if(movementInLine != null) {
				if(validate()) {
					INVManager invManager = Framework.getService(INVManager.class);
					List<MovementLineLot> list = this.getLineLots();
					if (list.size() == 0)
						return;
					// �˴����ܴ���movementLineLot����Ϊ��̨������genLots���ɶ�Ӧ��lineLot
					// (���ҽ���movementInLine.getObjectRrn()Ϊ��ʱ)
//					movementInLine.setMovementLots(list);
					movementInLine.setLots(genLots);
					List<MovementLine> listLine = new ArrayList<MovementLine>();
					listLine.add(movementInLine);
					in = invManager.saveMovementInLine(in, listLine, MovementIn.InType.OIN, Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					ADManager adManager = Framework.getService(ADManager.class);
					// �����»��movementInLine����Ϊ��ʱmovementInLine�Ѿ�ɾ�����µ�movementLine��objectRrn��ͬ
					// ���ڱ���󣬲������ٱ����ɾ��(save��delete��ť������)�������»��movementInLine��Ӱ��ҵ����
//					movementInLine = (MovementLine)adManager.getEntity(movementInLine);
					in = (MovementIn)adManager.getEntity((MovementIn)in);
					
					// ��Ϊ�˴�����saveMovementInLineʱ������ɾ��movementInLine�����Ա���movementInLine.objectRrn�Ѿ�����
					// ��initTableContent�л����line.objectRrn���õ���Ӧ��movementLineLot����������ֻ��ͨ��getLineNo�����
					// ��Ӧ���µ�movementInLine
//					int index = movementInLine.getLineNo().intValue() / 10;
//					if(in.getMovementLines() != null && in.getMovementLines().size() >= index) {
//						movementInLine = in.getMovementLines().get(index - 1);						
//					}
					((AdjustInLotTableManager)lotManager).setCanEdit(false);
					setIsSaved(true);
					setEnable(false);
					setPrintEnable(true);
//					super.initTableContent();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	// ��֤�����������Ƿ������ⵥ�е��������
	protected boolean validate() {
		BigDecimal total = BigDecimal.ZERO;
		for(MovementLineLot lineLot : getLineLots()) {
			if(lineLot.getLotId() == null || "".equals(lineLot.getLotId().trim())) {
				UI.showError(Message.getString("inv.invalid_lotId"));
				return false;
			}
			if(lineLot.getMaterialRrn().equals(movementInLine.getMaterialRrn()))
				total = total.add(lineLot.getQtyMovement());
		}
		if(total.compareTo(movementInLine.getQtyMovement()) == 0) {
			return true;
		} else {
			UI.showError(String.format(Message.getString("inv.lot_qtyTotal_isnot_equals_inQty"),
					total.toString(), movementInLine.getQtyMovement().toString(), movementInLine.getMaterialId()));
		}
		return false;
	}
	
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
		if(movementInLine != null && movementInLine.getObjectRrn() != null) {
			whereClause.append(" movementLineRrn = '");
			whereClause.append(movementInLine.getObjectRrn());
			whereClause.append("' ");
			return whereClause.toString();
		}
		return " 1 <> 1 ";
	}
	
}
