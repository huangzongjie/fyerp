package com.graly.mes.prd.adapter;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.workflow.graph.def.Procedure;
import com.graly.mes.prd.workflow.graph.def.Step;
import com.graly.mes.prd.workflow.graph.node.ProcedureState;
import com.graly.mes.prd.workflow.graph.node.StepState;

import com.graly.framework.base.application.Activator;
import com.graly.framework.runtime.Framework;


public class StepStateItemAdapter extends AbstractFlowItemAdapter {
	
	private static final Logger logger = Logger.getLogger(StepStateItemAdapter.class);
	private static final Object[] EMPTY = new Object[0];

	@Override
	public Object[] getChildren(Object object) {
		return EMPTY;
	}
	
	public boolean hasChildren(Object object) {
		return false;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof StepState) {
			StepState stepState = (StepState)element;
			Step step = getStep(stepState);
			return super.getText(step);
		}
		return "";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(Object object, String id) {
		if (object instanceof StepState){
			return Activator.getImageDescriptor("step");
		}
		return null;
	}
	
	protected Step getStep(StepState stepState) {
		Step step = null;
		try {
			step = stepState.getUsedStep();
			if (step == null) {
				try {
					step = new Step();
					step.setName(stepState.getStepName());
					step.setOrgRrn(stepState.getOrgRrn());
					PrdManager prdManager = Framework.getService(PrdManager.class);
					step = (Step)prdManager.getActiveProcessDefinition(step);
				} catch (Exception e) {
		        	logger.error(e.getMessage(), e);
		        }
			}
		} catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
		return step;
	}

}
