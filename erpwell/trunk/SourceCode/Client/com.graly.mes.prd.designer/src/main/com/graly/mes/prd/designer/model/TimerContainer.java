package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.SemanticElement;

public interface TimerContainer extends SemanticElement {

	void addTimer(Timer timer);
	void removeTimer(Timer timer);
	Timer[] getTimers();
	
}
