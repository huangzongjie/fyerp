package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.SemanticElement;

public interface ActionElementContainer extends SemanticElement {

	void addActionElement(ActionElement actionElement);
	void removeActionElement(ActionElement actionElement);
	ActionElement[] getActionElements();
	
}
