package com.graly.mes.prd.designer.common.model;


public interface NamedElement extends SemanticElement {

	public void setName(String newName);
	public String getName();
	public boolean isNameMandatory();

}
