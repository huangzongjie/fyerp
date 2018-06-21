package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.model.Description;
import com.graly.mes.prd.designer.model.Event;
import com.graly.mes.prd.designer.model.State;
import com.graly.mes.prd.designer.model.Transition;

public class StateDomAdapter extends XmlAdapter {
	
	private static final String[] CHILD_ELEMENTS = {"description", "event", "transition"};
	private static HashMap NODE_TYPES = null;
	
	protected String[] getChildElements() {
		return CHILD_ELEMENTS;
	}
	
	protected Map getNodeTypes() {
		if (NODE_TYPES == null) {
			NODE_TYPES = new HashMap();
			NODE_TYPES.put("description", "description");
			NODE_TYPES.put("event", "event");
			NODE_TYPES.put("transition", "transition");
		}
		return NODE_TYPES;
	}
	
	protected String getDefaultValue(String attributeName) {
		if ("async".equals(attributeName)) {
			return "false";
		} else {
			return super.getDefaultValue(attributeName);
		}
	}
	
	public void initialize(SemanticElement jpdlElement) {
		super.initialize(jpdlElement);
		State state = (State)jpdlElement;
		state.setAsync(getAttribute("async"));
		state.setName(getAttribute("name"));
		state.addPropertyChangeListener(this);
	}

	protected void initialize() {
		super.initialize();
		State state = (State)getSemanticElement();
		if (state != null) {
			setAttribute("async", state.getAsync());
			setAttribute("name", state.getName());
			addElement(state.getDescription());
			addElements(state.getEvents());
			addElements(state.getTransitions());
		}
	}

	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("eventAdd".equals(evt.getPropertyName())) {
			addElement((Event)evt.getNewValue());
		} else if ("eventRemove".equals(evt.getPropertyName())) {
			removeElement((Event)evt.getOldValue());
		} else if ("transitionAdd".equals(evt.getPropertyName())) {
			addElement((Transition)evt.getNewValue());
		} else if ("transitionRemove".equals(evt.getPropertyName())) {
			removeElement((Transition)evt.getOldValue());
		} else if ("description".equals(evt.getPropertyName())) {
			setElement("description", (SemanticElement)evt.getOldValue(), (Description)evt.getNewValue());
		} else if ("async".equals(evt.getPropertyName())) {
			setAttribute("async", (String)evt.getNewValue());
		} else if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		}
	}
	
	protected void doModelUpdate(String name, String newValue) {
		State state = (State)getSemanticElement();
		if ("name".equals(name)) {
			state.setName(newValue);
		} else if ("async".equals(name)) {
			state.setAsync(newValue);
		}
	}
	
	protected void doModelAdd(XmlAdapter child) {
		String type = child.getElementType();
		SemanticElement jpdlElement = createSemanticElementFor(child);
		child.initialize(jpdlElement);
		State state = (State)getSemanticElement();
		if ("event".equals(type)) {
			state.addEvent((Event)jpdlElement);
		} else if ("transition".equals(type)) {
			state.addTransition((Transition)jpdlElement);
		} else if ("description".equals(getNodeType(type))) {
			state.setDescription((Description)jpdlElement);
		}
	}
	
	protected void doModelRemove(XmlAdapter child) {
		String type = child.getElementType();
		State state = (State)getSemanticElement();
		if ("event".equals(type)) {
			state.removeEvent((Event)child.getSemanticElement());
		} else if ("transition".equals(type)) {
			state.removeTransition((Transition)child.getSemanticElement());
		} else if ("description".equals(getNodeType(type))) {
			state.setDescription(null);
		}
	}
}
