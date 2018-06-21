package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.model.AbstractNode;

public class StepState extends AbstractNode {
	
	private SubStep step;
	
	public void setStep(SubStep newStep) {
		SubStep oldStep = step;
		step = newStep;
		firePropertyChange("step", oldStep, newStep);
	}
	
	public SubStep getStep() {
		if (step == null) {
			step = (SubStep)getFactory().createById("SubStep");
		}
		return step;
	}
	
}
