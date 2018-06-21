package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.NamedElement;

public interface NodeElement extends NamedElement, EventContainer {

	void addTransition(Transition transition);
	void removeTransition(Transition transition);
	Transition[] getTransitions();
	
	boolean isPossibleChildOf(NodeElementContainer nodeElementContainer);
	
}
