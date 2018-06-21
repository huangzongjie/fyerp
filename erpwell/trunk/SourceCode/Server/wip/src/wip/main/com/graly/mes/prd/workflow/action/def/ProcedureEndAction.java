package com.graly.mes.prd.workflow.action.def;

import com.graly.mes.prd.workflow.graph.def.ActionHandler;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.wip.model.Lot;

public class ProcedureEndAction implements ActionHandler {
	
	public void execute(ExecutionContext executionContext) {
		Lot lot = executionContext.getLot();
		if (lot != null) {
			//lot.setProcedureId(null);
			//lot.setProcedureName(null);
			//lot.setProcedureVersion(null);
		}
	}
}
