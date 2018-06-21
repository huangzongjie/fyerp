package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.model.Action;
import com.graly.mes.prd.designer.model.Condition;
import com.graly.mes.prd.designer.model.Description;
import com.graly.mes.prd.designer.model.Script;
import com.graly.mes.prd.designer.model.Transition;

public class TransitionDomAdapter extends XmlAdapter {
	
	private static final String[] CHILD_ELEMENTS = {"description", "condition", "action-element"};
	private static HashMap NODE_TYPES = null;
	
	protected String[] getChildElements() {
		return CHILD_ELEMENTS;
	}
	
	protected Map getNodeTypes() {
		if (NODE_TYPES == null) {
			NODE_TYPES = new HashMap();
			NODE_TYPES.put("description", "description");
			NODE_TYPES.put("condition", "condition");
			NODE_TYPES.put("action", "action-element");
			NODE_TYPES.put("script", "action-element");
		}
		return NODE_TYPES;
	}
	
	protected void initialize() {
		super.initialize();
		Transition transition = (Transition)getSemanticElement();
		if (transition != null) {
			setAttribute("to", transition.getTo());
			setAttribute("name", transition.getName());
			addElement(transition.getDescription());
			addElements(transition.getActionElements());
		}
	}
	
	public void initialize(SemanticElement jpdlElement) {
		super.initialize(jpdlElement);
		Transition transition = (Transition)jpdlElement;
		transition.setTo(getAttribute("to"));
		transition.setName(getAttribute("name"));
		transition.addPropertyChangeListener(this);
	}

	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("condition".equals(evt.getPropertyName())) {
			setElement("condition", (SemanticElement)evt.getOldValue(), (SemanticElement)evt.getNewValue());
		} else if ("description".equals(evt.getPropertyName())) {
			setElement("description", (SemanticElement)evt.getOldValue(), (Description)evt.getNewValue());
		} else if ("actionElementAdd".equals(evt.getPropertyName())) {
			addElement((SemanticElement)evt.getNewValue());
		} else if ("actionElementRemove".equals(evt.getPropertyName())) {
			removeElement((SemanticElement)evt.getOldValue());
		} else if ("to".equals(evt.getPropertyName())) {
			setAttribute("to", (String)evt.getNewValue());
		} else if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		}
	}
	
	protected void doModelUpdate(String name, String newValue) {
		Transition transition = (Transition)getSemanticElement();
		if ("to".equals(name)) {
			transition.setTo(newValue);
		} else if ("name".equals(name)) {
			transition.setName(newValue);
		}
	}
	
	protected void doModelAdd(XmlAdapter child) {
		String type = child.getElementType();
		SemanticElement jpdlElement = createSemanticElementFor(child);
		child.initialize(jpdlElement);
		Transition transition = (Transition)getSemanticElement();
		if ("condition".equals(type)) {
			transition.setCondition((Condition)jpdlElement);
		} else if ("description".equals(getNodeType(type))) {
			transition.setDescription((Description)jpdlElement);
		} else if ("action".equals(type)) {
			transition.addActionElement((Action)jpdlElement);
		} else if ("script".equals(type)) {
			transition.addActionElement((Script)jpdlElement);
		}
	}
	
	protected void doModelRemove(XmlAdapter child) {
		String type = child.getElementType();
		Transition transition = (Transition)getSemanticElement();
		if ("condition".equals(type)) {
			transition.setCondition(null);
		} else if ("description".equals(getNodeType(type))) {
			transition.setDescription(null);
		} else if ("action".equals(type)) {
			transition.removeActionElement((Action)child.getSemanticElement());
		} else if ("script".equals(type)) {
			transition.removeActionElement((Script)child.getSemanticElement());
		}
	}

}
