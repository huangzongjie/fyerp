package com.graly.erp.inv.out;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotSelectFromDbSectionOld extends OutLineLotSection {
	protected List<Lot> selectedLots;

	public LotSelectFromDbSectionOld(ADBase parent, ADBase child, ADTable adTable,
			List<Lot> selectedLots, OutLineLotDialog olld) {
		super(parent, child, adTable, olld, false);
		this.selectedLots = selectedLots;
	}

	@Override
	protected void initTableContent() {
		// 将Lot转为MovementLineLot
		List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
		if(selectedLots != null) {
			for(Lot lot : selectedLots) {
				MovementLineLot lineLot = pareseMovementLineLot(outLine, lot.getQtyTransaction(), lot);
				lineLots.add(lineLot);
			}
			this.setLineLots(lineLots);
			refresh();
			setDoOprationsTrue();			
		}
	}
	
	protected MovementLine isContainsLot(Lot lot) {
		MovementLine l = null;
		if(outLine != null) {
			if (lot.getMaterialRrn().equals(outLine.getMaterialRrn())) {
				l = outLine;
				return l;
			} else {
				UI.showError(String.format(Message.getString("wip.material_does't_exisit_moboms"),
						lot.getLotId(), lot.getMaterialId(), outLine.getMaterialId()));
				return l;				
			}
		}
		return l;
	}

	@Override
	protected void saveAdapter() {
		try {
			if(outLine != null && (getLineLots() != null && getLineLots().size() > 0)) {
				if(validate()) {
					outLine.setMovementLots(getLineLots());
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.saveMovementOutLine(out, outLine, getOutType(), Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					this.setIsSaved(true);
					((OutLineLotDialog)parentDialog).buttonPressed(IDialogConstants.CANCEL_ID);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	/* 验证批次总数是否等于出库行出库数量 */
	protected boolean validate() {
		BigDecimal total = BigDecimal.ZERO;
		for(MovementLineLot lineLot : getLineLots()) {
			if(lineLot.getMaterialRrn().equals(outLine.getMaterialRrn()))
				total = total.add(lineLot.getQtyMovement());
		}
		if(total.doubleValue() == outLine.getQtyMovement().doubleValue()) {
			return true;
		} else {
			UI.showError(String.format(Message.getString("wip.out_qty_isnot_equal"),
					outLine.getQtyMovement().toString(), String.valueOf(total), outLine.getMaterialName()));
		}
		return false;
	}
}