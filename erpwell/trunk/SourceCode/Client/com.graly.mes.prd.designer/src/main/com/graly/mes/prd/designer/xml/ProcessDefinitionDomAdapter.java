package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.model.AbstractNode;
import com.graly.mes.prd.designer.model.ActionElement;
import com.graly.mes.prd.designer.model.Description;
import com.graly.mes.prd.designer.model.Event;
import com.graly.mes.prd.designer.model.ProcessDefinition;
import com.graly.mes.prd.designer.model.StartState;


public class ProcessDefinitionDomAdapter extends XmlAdapter {
	
	private static HashMap NODE_TYPES = null;	
	private static String[] CHILD_ELEMENTS = {
		"description", "start-state", "node-element", "end-state", "action-element", "event"
	};
	private static final String INTER_NODE_TYPE_SPACING = "\n\n\n";
	private static final String INTER_CHILD_SPACING = "\n\n";
	
	protected String getInterNodeTypeSpacing() { 
		return INTER_NODE_TYPE_SPACING;
	}
	
	protected String getInterChildSpacing() {
		return INTER_CHILD_SPACING;
	}
	
	protected String[] getChildElements() {
		return CHILD_ELEMENTS;
	}
	public Map getNodeTypes() {
		if (NODE_TYPES == null) {
			NODE_TYPES = new HashMap();
			NODE_TYPES.put("description", "description");
			NODE_TYPES.put("start-state", "start-state");
			NODE_TYPES.put("node", "node-element");
			NODE_TYPES.put("state", "node-element");
			NODE_TYPES.put("process-state", "node-element");
			NODE_TYPES.put("fork", "node-element");
			NODE_TYPES.put("join", "node-element");
			NODE_TYPES.put("decision", "node-element");
			NODE_TYPES.put("mail-node",	"node-element");
			NODE_TYPES.put("end-state", "end-state");
			NODE_TYPES.put("action", "action-element");
			NODE_TYPES.put("script", "action-element");
			NODE_TYPES.put("create-timer", "action-element");
			NODE_TYPES.put("cancel-timer", "action-element");
			NODE_TYPES.put("mail", "action-element");
			NODE_TYPES.put("event", "event");
			NODE_TYPES.put("procedure-state", "procedure-state");
			NODE_TYPES.put("step-state", "step-state");
		}
		return NODE_TYPES;
	}
		
	protected void initialize() {
		super.initialize();
		ProcessDefinition processDefinition = (ProcessDefinition)getSemanticElement();
		if (processDefinition != null) {
			Map properties = processDefinition.getProperties();
			Iterator iterator = properties.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String)iterator.next();
				setAttribute(key, (String)properties.get(key));
			}
			setAttribute("name", processDefinition.getName());
			addElement(processDefinition.getDescription());
			addElement(processDefinition.getStartState());
			addElements(processDefinition.getNodeElements());
			addElements(processDefinition.getActionElements());
			addElements(processDefinition.getEvents());
		}
	}

	public void initialize(SemanticElement jpdlElement) {
		super.initialize(jpdlElement);
		ProcessDefinition processDefinition = (ProcessDefinition)jpdlElement;
		Map map = getAttributes();
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String next = (String)iterator.next();
			if ("name".equals(next)) {
				processDefinition.setName(getAttribute("name"));						
			}
			processDefinition.setProperty(next, (String)map.get(next));
		}
		processDefinition.addPropertyChangeListener(this);
	}

	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("startStateAdd".equals(evt.getPropertyName())) {
			addElement((StartState)evt.getNewValue());
		} else if ("startStateRemove".equals(evt.getPropertyName())) {
			removeElement((StartState)evt.getOldValue());
		} else if ("nodeElementAdd".equals(evt.getPropertyName())) {
			addElement((SemanticElement)evt.getNewValue());
		} else if ("nodeElementRemove".equals(evt.getPropertyName())) {
			removeElement((SemanticElement)evt.getOldValue());
		} else if ("actionElementAdd".equals(evt.getPropertyName())) {
			addElement((SemanticElement)evt.getNewValue());
		} else if ("actionElementRemove".equals(evt.getPropertyName())) {
			removeElement((SemanticElement)evt.getOldValue());
		} else if ("eventAdd".equals(evt.getPropertyName())) {
			addElement((Event)evt.getNewValue());
		} else if ("eventRemove".equals(evt.getPropertyName())) {
			removeElement((Event)evt.getOldValue());
		} else if ("description".equals(evt.getPropertyName())) {
			setElement("description", (Description)evt.getOldValue(), (Description)evt.getNewValue());
		} else  if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		} else if ("custom".equals(evt.getPropertyName())) {
			String name = ((String[])evt.getNewValue())[0];
			String newValue = ((String[])evt.getNewValue())[1];
			setAttribute(name, newValue);
		}
	}
	
	protected void doModelUpdate(String name, String newValue) {
		ProcessDefinition processDefinition = (ProcessDefinition)getSemanticElement();
		if ("name".equals(name)) {
			processDefinition.setName(newValue);
		}
	}
	
	protected SemanticElement createSemanticElementFor(XmlAdapter child) {
		SemanticElement result = super.createSemanticElementFor(child);
		child.initialize(result);
		return result;
	}
	
	protected SemanticElement getSemanticElementFor(XmlAdapter child) {
		return child.getSemanticElement() != null  ? child.getSemanticElement() : createSemanticElementFor(child);
	}
	
	protected void doModelAdd(XmlAdapter child) {
		SemanticElement jpdlElement = getSemanticElementFor(child);
		if (jpdlElement == null) return;
		String type = child.getElementType();
		ProcessDefinition processDefinition = (ProcessDefinition)getSemanticElement();
		if ("start-state".equals(type)) {
			processDefinition.addStartState((StartState)jpdlElement);
		} else if ("node".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("state".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("procedure-state".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("step-state".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("fork".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("join".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("decision".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("mail-node".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("end-state".equals(type)) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("action".equals(type)) {
			processDefinition.addActionElement((ActionElement)jpdlElement);
		} else if ("script".equals(type)) {
			processDefinition.addActionElement((ActionElement)jpdlElement);
		} else if ("create-timer".equals(type)) {
			processDefinition.addActionElement((ActionElement)jpdlElement);
		} else if ("cancel-timer".equals(type)) {
			processDefinition.addActionElement((ActionElement)jpdlElement);
		} else if ("mail".equals(type)) {
			processDefinition.addActionElement((ActionElement)jpdlElement);
		} else if ("event".equals(type)) {
			processDefinition.addEvent((Event)jpdlElement);
		} else if ("node-element".equals(getNodeType(type))) {
			processDefinition.addNodeElement((AbstractNode)jpdlElement);
		} else if ("action-element".equals(getNodeType(type))) {
			processDefinition.addActionElement((ActionElement)jpdlElement);
		} else if ("description".equals(getNodeType(type))) {
			processDefinition.setDescription((Description)jpdlElement);
		}
	}
	
	protected void doModelRemove(XmlAdapter child) {
		if (child.getSemanticElement() == null) return;
		String type = child.getElementType();
		ProcessDefinition processDefinition = (ProcessDefinition)getSemanticElement();
		if ("start-state".equals(type)) {
			processDefinition.removeStartState((StartState)child.getSemanticElement());
		} else if ("node".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("state".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("procedure-state".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("step-state".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("fork".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("join".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("decision".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("mail-node".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("end-state".equals(type)) {
			processDefinition.removeNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("action".equals(type)) {
			processDefinition.removeActionElement((ActionElement)child.getSemanticElement());
		} else if ("script".equals(type)) {
			processDefinition.removeActionElement((ActionElement)child.getSemanticElement());
		} else if ("create-timer".equals(type)) {
			processDefinition.removeActionElement((ActionElement)child.getSemanticElement());
		} else if ("cancel-timer".equals(type)) {
			processDefinition.removeActionElement((ActionElement)child.getSemanticElement());
		} else if ("mail".equals(type)) {
			processDefinition.removeActionElement((ActionElement)child.getSemanticElement());
		} else if ("event".equals(type)) {
			processDefinition.removeEvent((Event)child.getSemanticElement());
		} else if ("node-element".equals(getNodeType(type))) {
			processDefinition.addNodeElement((AbstractNode)child.getSemanticElement());
		} else if ("action-element".equals(getNodeType(type))) {
			processDefinition.addActionElement((ActionElement)child.getSemanticElement());
		} else if ("description".equals(getNodeType(type))) {
			processDefinition.setDescription(null);
		}
	}


}
