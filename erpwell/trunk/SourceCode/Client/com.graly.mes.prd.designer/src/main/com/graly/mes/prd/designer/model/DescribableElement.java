package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.SemanticElement;

public interface DescribableElement extends SemanticElement {
	
	public void setDescription(Description description);
	public Description getDescription();

}
