package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.AbstractSemanticElement;

public class Description extends AbstractSemanticElement {
	
	private String description;
	
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		firePropertyChange("description", oldDescription, newDescription);
	}
	
	public String getDescription() {
		return description;
	}

}
