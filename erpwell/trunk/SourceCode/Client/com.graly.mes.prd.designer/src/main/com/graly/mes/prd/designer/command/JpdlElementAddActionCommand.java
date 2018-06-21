package com.graly.mes.prd.designer.command;

import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.model.AbstractNode;
import com.graly.mes.prd.designer.model.Action;
import com.graly.mes.prd.designer.model.Event;
import com.graly.mes.prd.designer.model.ProcessDefinition;

public class JpdlElementAddActionCommand extends Command {
	
	private SemanticElement target;
	private String eventType;
	private Event event;
	private Action action;
	
	public void setTarget(SemanticElement target) {
		this.target = target;
	}
	
	public void setActionId(String actionId) {
		this.eventType = getEventType(actionId);
	}
	
	public void execute() {
		if (action == null) {
			createAction();
		}
		if (target instanceof Event) {
			addAction((Event)target);
		} else {
			addAction(target);
		} 
}
	
	public void undo() {
		if (target instanceof Event) {
			removeAction((Event)target);
		} else {
			removeAction(event);
			if (event.getActionElements().length == 0) {
				removeEvent(target);
			}
		}
	}
	
	private void createAction() {
		action = (Action)target.getFactory().createById("com.graly.mes.prd.designer.action");
	}
	
	private void createEvent() {
		event = (Event)target.getFactory().createById("com.graly.mes.prd.designer.event");
		event.setType(eventType);
	}
	
	private void addAction(Event event) {
		event.addActionElement(action);
	}
	
	private void removeAction(Event event) {
		event.removeActionElement(action);
	}
	
	private void removeEvent(SemanticElement element) {
		if (element instanceof ProcessDefinition) {
			((ProcessDefinition)element).removeEvent(event);
		} else if (element instanceof AbstractNode) {
			((AbstractNode)element).removeEvent(event);
		} 
	}
	
	private void addAction(SemanticElement element) {
		if (event == null) {
			prepareEvent(element);
		}
		if (event.getActionElements().length == 0) {
			addEvent(element);
		}
		addAction(event);
	}
	
	private void addEvent(SemanticElement element) {
		if (element instanceof ProcessDefinition) {
			((ProcessDefinition)element).addEvent(event);
		} else if (element instanceof AbstractNode) {
			((AbstractNode)element).addEvent(event);
		} 
	}
	
	private Event[] getEvents(SemanticElement element) {
		if (element instanceof ProcessDefinition) {
			return ((ProcessDefinition)element).getEvents();
		} else if (element instanceof AbstractNode) {
			return ((AbstractNode)element).getEvents();
		} else {
			return new Event[0];
		}		
	}
	
	private void prepareEvent(SemanticElement element) {
		Event[] events = getEvents(element);
		for (int i = 0; i < events.length; i++) {
			if (events[i].getType().equals(eventType)) {
				event = events[i];
			}
		}
		if (event == null) {
			createEvent();
		}		
	}
	
	
	private String getEventType(String actionId) {
		if ("beforeSignal".equals(actionId)) return "before-signal";
		if ("afterSignal".equals(actionId)) return "after-signal";
		if ("nodeEnter".equals(actionId)) return "node-enter";
		if ("nodeLeave".equals(actionId)) return "node-leave";
		if ("processStart".equals(actionId)) return "process-start";
		if ("processEnd".equals(actionId)) return "process-end";
		return null;
	}
	
}
