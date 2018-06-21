package com.graly.mes.prd.workflow.graph.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.graly.framework.core.exception.ClientException;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.action.def.FutureAction;
import com.graly.mes.prd.workflow.action.def.FutureHold;
import com.graly.mes.prd.workflow.action.exe.FutureHoldInstance;
import com.graly.mes.prd.workflow.action.exe.InstanceAction;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;

public class InstanceActionResolver implements Serializable {
	
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(InstanceActionResolver.class);

	public List<InstanceAction> loadInstanceActions(ExecutionContext executionContext, StepState stepState, String actionType) throws JbpmException {
		List<InstanceAction> instanceActions = new ArrayList<InstanceAction>();
		try {
			List<FutureAction> futureActions = executionContext.getLotManager().getFutureAction(executionContext.getToken().getProcessInstance().getOrgRrn(), 
					stepState.getObjectRrn(), executionContext.getToken().getProcessInstance().getInstanceKey(), actionType);
			int i = 0;
			for (FutureAction futureAction : futureActions) {
				if (futureAction instanceof FutureHold) {
					FutureHoldInstance action = new FutureHoldInstance();
					action.setOrgRrn(executionContext.getToken().getProcessInstance().getOrgRrn());
					action.setIsActive(true);
					action.setInstanceKey(executionContext.getToken().getProcessInstance().getInstanceKey());
					action.setFutureHold((FutureHold)futureAction);
					action.setSeqNo(i);
					instanceActions.add(action);
				}
				i++;
			}
		} catch (ClientException e) {
			throw new JbpmException(e.getMessage());
		}
		return instanceActions;
	}
	
	public List<InstanceAction> getInstanceActions(ExecutionContext executionContext) throws JbpmException {
		List<InstanceAction> instanceActions = null;
		try {
			Long instanceKey = executionContext.getToken().getProcessInstance().getInstanceKey();
			if (instanceKey != null) {
				instanceActions = executionContext.getLotManager().getInstanceAction(executionContext.getToken().getProcessInstance().getOrgRrn(), 
						executionContext.getToken().getProcessInstance().getInstanceKey());
				
			}
		} catch (ClientException e) {
			throw new JbpmException(e.getMessage());
		}
		return instanceActions;
	}
	
	public void removeInstanceAction(ExecutionContext executionContext) throws JbpmException {
		try {
			executionContext.getLotManager().removeInstanceAction(executionContext.getToken().getProcessInstance().getOrgRrn(), 
					executionContext.getToken().getProcessInstance().getInstanceKey());
		} catch (ClientException e) {
			throw new JbpmException(e.getMessage());
		}
	}
}
