package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.model.Description;
import com.graly.mes.prd.designer.model.EndState;
import com.graly.mes.prd.designer.model.Event;

public class EndStateDomAdapter extends XmlAdapter {
	
	private static final String[] CHILD_ELEMENTS = {"description", "event"};
	private static HashMap NODE_TYPES = null;
	
	protected String[] getChildElements() {
		return CHILD_ELEMENTS;
	}
	
	protected Map getNodeTypes() {
		if (NODE_TYPES == null) {
			NODE_TYPES = new HashMap();
			NODE_TYPES.put("description", "description");
			NODE_TYPES.put("event", "event");
			NODE_TYPES.put("exception-handler", "exception-handler");
		}
		return NODE_TYPES;
	}
	
	public void initialize(SemanticElement jpdlElement) {
		super.initialize(jpdlElement);
		EndState endState = (EndState)jpdlElement;
		endState.setName(getAttribute("name"));
		endState.addPropertyChangeListener(this);
	}

	protected void initialize() {
		super.initialize();
		EndState endState = (EndState)getSemanticElement();
		if (endState != null) {
			setAttribute("name", endState.getName());
			addElement(endState.getDescription());
			addElements(endState.getEvents());
		}
	}

	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("eventAdd".equals(evt.getPropertyName())) {
			addElement((Event)evt.getNewValue());
		} else if ("description".equals(evt.getPropertyName())) {
			setElement("description", (SemanticElement)evt.getOldValue(), (Description)evt.getNewValue());
		} else if ("eventRemove".equals(evt.getPropertyName())) {
			removeElement((Event)evt.getOldValue());
		} else if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		}
	}
	
	protected void doModelUpdate(String name, String newValue) {
		EndState endState = (EndState)getSemanticElement();
		if ("name".equals(name)) {
			endState.setName(newValue);
		}
	}
	
	protected void doModelAdd(XmlAdapter child) {
		String type = child.getElementType();
		SemanticElement jpdlElement = createSemanticElementFor(child);
		child.initialize(jpdlElement);
		EndState endState = (EndState)getSemanticElement();
		if ("event".equals(type)) {
			endState.addEvent((Event)jpdlElement);
		} else if ("description".equals(getNodeType(type))) {
			endState.setDescription((Description)jpdlElement);
		} 
	}
	
	protected void doModelRemove(XmlAdapter child) {
		String type = child.getElementType();
		EndState endState = (EndState)getSemanticElement();
		if ("event".equals(type)) {
			endState.removeEvent((Event)child.getSemanticElement());
		} else if ("description".equals(getNodeType(type))) {
			endState.setDescription(null);
		} 
	}

}
