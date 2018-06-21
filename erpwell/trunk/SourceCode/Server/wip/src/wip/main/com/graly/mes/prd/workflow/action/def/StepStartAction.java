package com.graly.mes.prd.workflow.action.def;

import com.graly.mes.prd.workflow.graph.def.ActionHandler;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.wip.model.Lot;

public class StepStartAction implements ActionHandler {
	
	public void execute(ExecutionContext executionContext) {
		Lot lot = executionContext.getLot();
		if (lot != null) {
			ProcessDefinition step = executionContext.getProcessDefinition();
			lot.setStepRrn(step.getObjectRrn());
			lot.setStepName(step.getName());
			lot.setStepVersion((long)step.getVersion());
			executionContext.getEntityManager().merge(lot);
		}
	}
}
