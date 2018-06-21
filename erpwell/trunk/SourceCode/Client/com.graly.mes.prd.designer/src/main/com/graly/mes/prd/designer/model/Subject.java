package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.AbstractSemanticElement;

public class Subject extends AbstractSemanticElement {
	
	private String subject;
	
	public void setSubject(String newSubject) {
		String oldSubject = subject;
		subject = newSubject;
		firePropertyChange("subject", oldSubject, newSubject);
	}
	
	public String getSubject() {
		return subject;
	}

}
