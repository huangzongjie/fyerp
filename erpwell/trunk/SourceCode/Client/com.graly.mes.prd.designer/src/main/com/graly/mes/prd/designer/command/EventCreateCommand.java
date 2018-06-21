package com.graly.mes.prd.designer.command;

import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.model.SemanticElementFactory;
import com.graly.mes.prd.designer.model.Event;
import com.graly.mes.prd.designer.model.EventContainer;

public class EventCreateCommand extends Command {
	
	private EventContainer eventContainer;
	private Event event;
	private SemanticElementFactory factory;
	
	public EventCreateCommand(SemanticElementFactory factory) {
		this.factory = factory;
	}
	
	public void execute() {
		if (event == null) {
			event = (Event)factory.createById("com.graly.mes.prd.designer.event");
		}
		eventContainer.addEvent(event);
	}
	
	public void undo() {
		eventContainer.removeEvent(event);
	}
	
	public void setEventContainer(EventContainer eventContainer) {
		this.eventContainer = eventContainer;
	}
	
}
