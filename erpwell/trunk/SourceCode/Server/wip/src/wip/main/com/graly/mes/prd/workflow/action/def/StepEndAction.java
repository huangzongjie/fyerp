package com.graly.mes.prd.workflow.action.def;

import java.util.Date;

import com.graly.mes.prd.workflow.graph.def.ActionHandler;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.wip.model.Lot;

public class StepEndAction implements ActionHandler {
	
	public void execute(ExecutionContext executionContext) {
		Lot lot = executionContext.getLot();
		if (lot != null) {
			lot.setEquipmentId("");
			lot.setEquipmentRrn(null);
			lot.setTrackOutTime(new Date());
//			lot.setStepId(null);
//			lot.setStepName(null);
//			lot.setStepVersion(null);
			executionContext.getEntityManager().merge(lot);
		}
	}
}
