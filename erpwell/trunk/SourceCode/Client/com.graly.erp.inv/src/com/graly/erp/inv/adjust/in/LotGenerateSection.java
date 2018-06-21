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
	
	// 验证lot对应的物料必须在lines中, 并且lot不能在IQC, GEN和WIP上并且是未使用的
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
					// 此处不能传入movementLineLot，因为后台方法会genLots生成对应的lineLot
					// (当且仅当movementInLine.getObjectRrn()为空时)
//					movementInLine.setMovementLots(list);
					movementInLine.setLots(genLots);
					List<MovementLine> listLine = new ArrayList<MovementLine>();
					listLine.add(movementInLine);
					in = invManager.saveMovementInLine(in, listLine, MovementIn.InType.OIN, Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					ADManager adManager = Framework.getService(ADManager.class);
					// 不重新获得movementInLine，因为此时movementInLine已经删除，新的movementLine的objectRrn不同
					// 由于保存后，不可以再保存和删除(save和delete按钮不可用)，不重新获得movementInLine不影响业务功能
//					movementInLine = (MovementLine)adManager.getEntity(movementInLine);
					in = (MovementIn)adManager.getEntity((MovementIn)in);
					
					// 因为此处保存saveMovementInLine时，是先删除movementInLine，所以保存movementInLine.objectRrn已经变了
					// 在initTableContent中会根据line.objectRrn来得到相应的movementLineLot，所以这里只能通过getLineNo来获得
					// 对应的新的movementInLine
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
	
	// 验证批次总数量是否等于入库单行的入库数量
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
