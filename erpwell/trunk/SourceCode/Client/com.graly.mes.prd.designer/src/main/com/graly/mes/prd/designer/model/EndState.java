package com.graly.mes.prd.designer.model;


public class EndState extends AbstractNode {
	
	
	@Override
	public boolean isPossibleChildOf(NodeElementContainer nodeElementContainer) {
		return nodeElementContainer instanceof ProcessDefinition && ((ProcessDefinition)nodeElementContainer).getEndState() == null;
	}

	public void addTransition(Transition transition) {
		// No transitions can be added to a decision node
	}
	
	public void removeTransition(Transition transition) {
		// No transitions can be added to a decision node
	}
	
	public Transition[] getTransitions() {
		// No transitions can be added to a decision node
		return new Transition[0];
	}

}
