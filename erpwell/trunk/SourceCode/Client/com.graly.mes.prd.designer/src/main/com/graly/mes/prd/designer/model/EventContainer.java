package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.SemanticElement;


public interface EventContainer extends SemanticElement {
	
	public void addEvent(Event event);	
	public void removeEvent(Event event);	
	public Event[] getEvents();
}
