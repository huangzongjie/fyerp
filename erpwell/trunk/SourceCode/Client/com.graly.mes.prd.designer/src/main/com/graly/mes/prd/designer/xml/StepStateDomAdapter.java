package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.SubStep;
import com.graly.mes.prd.designer.model.Transition;
import com.graly.mes.prd.designer.model.Description;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.model.StepState;

public class StepStateDomAdapter extends XmlAdapter {

	private static HashMap NODE_TYPES = null;	
	private static String[] CHILD_ELEMENTS = {"step", "transition"};
	
	protected String[] getChildElements() {
		return CHILD_ELEMENTS;
	}
	
	public Map getNodeTypes() {
		if (NODE_TYPES == null) {
			NODE_TYPES = new HashMap();
			NODE_TYPES.put("step", "step");
			NODE_TYPES.put("transition", "transition");
		}
		return NODE_TYPES;
	}
	
	protected void initialize() {
		super.initialize();
		StepState stepState = (StepState)getSemanticElement();
		if (stepState != null) {
			setAttribute("name", stepState.getName());
			addElement(stepState.getStep());
			addElements(stepState.getTransitions());
		}
	}

	public void initialize(SemanticElement semanticElement) {
		super.initialize(semanticElement);
		StepState stepState = (StepState)semanticElement;
		stepState.setName(getAttribute("name"));
		stepState.addPropertyChangeListener(this);
	}
	
	@Override
	protected void doModelAdd(XmlAdapter child) {
		String type = child.getElementType();
		SemanticElement jpdlElement = createSemanticElementFor(child);
		child.initialize(jpdlElement);
		StepState stepState = (StepState)getSemanticElement();
		if ("transition".equals(type)) {
			stepState.addTransition((Transition)jpdlElement);
		} else if ("step".equals(type)) {
			stepState.setStep((SubStep)jpdlElement);
		}
	}

	@Override
	protected void doModelRemove(XmlAdapter child) {
		String type = child.getElementType();
		StepState stepState = (StepState)getSemanticElement();
		if ("transition".equals(type)) {
			stepState.removeTransition((Transition)child.getSemanticElement());
		} else if ("step".equals(type)) {
			stepState.setStep(null);
		}
	}

	@Override
	protected void doModelUpdate(String name, String newValue) {
		StepState stepState = (StepState)getSemanticElement();
		if ("name".equals(name)) {
			stepState.setName(newValue);
		}
	}
	
	@Override
	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("transitionAdd".equals(evt.getPropertyName())) {
			addElement((Transition)evt.getNewValue());
		} else if ("transitionRemove".equals(evt.getPropertyName())) {
			removeElement((Transition)evt.getOldValue());
		} else if ("step".equals(evt.getPropertyName())) {
			setElement("step", (SemanticElement)evt.getOldValue(), (Description)evt.getNewValue());
		} else if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		}
	}

}
