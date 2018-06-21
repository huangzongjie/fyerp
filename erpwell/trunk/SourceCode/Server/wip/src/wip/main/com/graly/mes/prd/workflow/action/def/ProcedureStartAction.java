package com.graly.mes.prd.workflow.action.def;

import com.graly.mes.prd.workflow.graph.def.ActionHandler;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.wip.model.Lot;

public class ProcedureStartAction implements ActionHandler {
	
	public void execute(ExecutionContext executionContext) {
		Lot lot = executionContext.getLot();
		if (lot != null) {
			ProcessDefinition procedure = executionContext.getProcessDefinition();
			lot.setProcedureRrn(procedure.getObjectRrn());
			lot.setProcedureName(procedure.getName());
			lot.setProcedureVersion((long)procedure.getVersion());
			executionContext.getEntityManager().merge(lot);
		}
	}
}
