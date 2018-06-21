package com.graly.framework.core.statemachine;

import java.util.ArrayList;
import java.util.List;

public class SubstateCollection {
	
	private State owner;
	private List<State> subStates = new ArrayList<State>();
	
	public SubstateCollection(State owner) {
        this.owner = owner;
    }
	
	 public void add(State subState) throws StateMachineException { 
		 if(owner == subState) {
             throw new StateMachineException(
                 "State cannot be a substate to itself.");
         } else if(getSubStates().contains(subState)) {
             throw new StateMachineException(
                 "State is already a substate to this state.");
         } else if(subState.getSuperState() != null) {
             throw new StateMachineException(
                 "State is already a substate to another State.");
         }
		 
		 subState.setSuperState(owner);
         getSubStates().add(subState);
	 }
	 
     public void remove(State subState) throws StateMachineException  {
         if(getSubStates().contains(subState)) {
        	 subState.setSuperState(null);;
             getSubStates().remove(subState);

             if(owner.getInitialState() == subState) {
                 owner.setInitialState(null);
             }
         }
     }

	public void setSubStates(List<State> subStates) {
		this.subStates = subStates;
	}

	public List<State> getSubStates() {
		return subStates;
	}
}
