package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.model.Description;
import com.graly.mes.prd.designer.model.Event;
import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.model.Transition;

public class StartStateDomAdapter extends XmlAdapter {
	
	private static final String[] CHILD_ELEMENTS = {"description", "task", "transition", "event", "exception-handler"};
	private static HashMap NODE_TYPES = null;
	
	protected String[] getChildElements() {
		return CHILD_ELEMENTS;
	}
	
	protected Map getNodeTypes() {
		if (NODE_TYPES == null) {
			NODE_TYPES = new HashMap();
			NODE_TYPES.put("description", "description");
			NODE_TYPES.put("transition", "transition");
			NODE_TYPES.put("event", "event");
		}
		return NODE_TYPES;
	}
	
	protected void initialize() {
		super.initialize();
		StartState startState = (StartState)getSemanticElement();
		if (startState != null) {
			setAttribute("name", startState.getName());
			addElement(startState.getDescription());
			addElements(startState.getTransitions());
			addElements(startState.getEvents());
		}
	}

	public void initialize(SemanticElement jpdlElement) {
		super.initialize(jpdlElement);
		StartState startState = (StartState)jpdlElement;
		startState.setName(getAttribute("name"));
		startState.addPropertyChangeListener(this);
	}

	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("description".equals(evt.getPropertyName())) {
			setElement("description", (SemanticElement)evt.getOldValue(), (Description)evt.getNewValue());
		} else if ("transitionAdd".equals(evt.getPropertyName())) {
			addElement((Transition)evt.getNewValue());
		} else if ("transitionRemove".equals(evt.getPropertyName())) {
			removeElement((Transition)evt.getOldValue());
		} else if ("eventAdd".equals(evt.getPropertyName())) {
			addElement((Event)evt.getNewValue());
		} else if ("eventRemove".equals(evt.getPropertyName())) {
			removeElement((Event)evt.getOldValue());
		} else if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		}
	}

	protected void doModelUpdate(String name, String newValue) {
		StartState startState = (StartState)getSemanticElement();
		if ("name".equals(name)) {
			startState.setName(newValue);
		}
	}
	
	protected void doModelAdd(XmlAdapter child) {
		String type = child.getElementType();
		SemanticElement jpdlElement = createSemanticElementFor(child);
		child.initialize(jpdlElement);
		StartState startState = (StartState)getSemanticElement();
		if ("description".equals(getNodeType(type))) {
			startState.setDescription((Description)jpdlElement);
		} else if ("transition".equals(type)) {
			startState.addTransition((Transition)jpdlElement);
		} else if ("event".equals(type)) {
			startState.addEvent((Event)jpdlElement);
		} 
	}
	
	protected void doModelRemove(XmlAdapter child) {
		String type = child.getElementType();
		StartState startState = (StartState)getSemanticElement();
		if ("description".equals(getNodeType(type))) {
			startState.setDescription(null);
		} else if ("transition".equals(type)) {
			startState.removeTransition((Transition)child.getSemanticElement());
		} else if ("event".equals(type)) {
			startState.removeEvent((Event)child.getSemanticElement());
		} 
	}

}
