package com.graly.mes.prd.designer.command;

import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.model.Event;
import com.graly.mes.prd.designer.model.EventContainer;

public class EventDeleteCommand extends Command {
	
	private EventContainer eventContainer;
	private Event event;
	
	public void execute() {
		eventContainer.removeEvent(event);
	}
	
	public void undo() {
		eventContainer.addEvent(event);
	}
	
	public void setEventContainer(EventContainer eventContainer) {
		this.eventContainer = eventContainer;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	
}
