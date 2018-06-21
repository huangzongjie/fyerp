package com.graly.framework.core.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransitionCollection {

	private State owner = null;
	private Map<String, ArrayList<Transition>> transitions;
	
	public TransitionCollection(State owner) {
        this.owner = owner;
        setTransitions(new HashMap<String, ArrayList<Transition>>());
    }
	
	public void add(String eventId, Transition trans) throws StateMachineException {
		if(eventId == null || "".equals(eventId.trim())) {
            throw new StateMachineException(
                "Event ID is null or empty.");
        }
        else if(trans.getSource() != null) {
            throw new StateMachineException(
                "This Transition has already been added to another State.");
        }         

        trans.setSource(owner);
        if(getTransitions().get(eventId) == null) {
            // Create new list of Transitions for the specified event ID.
            getTransitions().put(eventId, new ArrayList<Transition>());
        }            

        // Add Transition.
        getTransitions().get(eventId).add(trans);
    }
	
    public void remove(String eventId, Transition trans) throws StateMachineException {
    	if(eventId == null || "".equals(eventId.trim())) {
            throw new StateMachineException(
                "Event ID is null or empty.");
        }

        // If there are Transitions at the specified event id.
        if(getTransitions().get(eventId) != null) {
        	getTransitions().get(eventId).remove(trans);

            // If there are no more Transitions at the specified event id.
            if(getTransitions().get(eventId).size() == 0) {
                // Indicate that there are no Transitions at this event id.
            	getTransitions().put(eventId, null);
            }
        }
    }

	public void setTransitions(Map<String, ArrayList<Transition>> transitions) {
		this.transitions = transitions;
	}

	public Map<String, ArrayList<Transition>> getTransitions() {
		return transitions;
	}

}
