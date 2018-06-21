package com.graly.mes.prd.workflow.action.def;

import java.util.Date;

import com.graly.mes.prd.workflow.graph.def.ActionHandler;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

public class ProcessEndAction implements ActionHandler {
	
	public void execute(ExecutionContext executionContext) {
		Lot lot = executionContext.getLot();
		if (lot != null) {
			try {
				lot.stateTrans(LotStateMachine.TRANS_FINISH);
			} catch (Exception e) {
			}
			lot.setEndMainQty(lot.getMainQty());
			lot.setEndSubQty(lot.getSubQty());
			lot.setEndTime(new Date());
			executionContext.getEntityManager().merge(lot);
		}
		
	}
}
