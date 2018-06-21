package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.AbstractSemanticElement;

public class Text extends AbstractSemanticElement {
	
	private String text;
	
	public void setText(String newText) {
		String oldText = text;
		text = newText;
		firePropertyChange("text", oldText, newText);
	}
	
	public String getText() {
		return text;
	}

}
