package com.graly.mes.prd.designer.model;


public class StartState extends AbstractNode {
	
	public boolean isPossibleChildOf(NodeElementContainer nodeElementContainer) {
		return nodeElementContainer instanceof ProcessDefinition && ((ProcessDefinition)nodeElementContainer).getStartState() == null;
	}
	
	public void initializeName(ProcessDefinition processDefinition) {
		setName(getNamePrefix());
	}

}
