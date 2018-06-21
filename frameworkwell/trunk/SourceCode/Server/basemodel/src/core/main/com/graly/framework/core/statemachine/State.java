package com.graly.framework.core.statemachine;

public class State {

	private State superState = null;
	private State initialState = null;
	private State historyState = null;

	private SubstateCollection subStates;
	private TransitionCollection transitions;
	
	private int level;
	private String stateId;
	private TransitionResult notFiredResult = new TransitionResult(false, null, null);        
	
    public State(String stateId) {
    	initializeState(stateId);
    }
    
    private void initializeState(String stateId) {
        this.setStateId(stateId);
        setSubStates(new SubstateCollection(this));
        setTransitions(new TransitionCollection(this));
        level = 1;
    }
    
	public TransitionResult dispatch(String eventId) { 
         return dispatch(this, eventId);
    }       
	
	private TransitionResult dispatch(State origin, String eventId) {       
        TransitionResult transResult = notFiredResult;
        // If there are any Transitions for this event.
        if(getTransitions().getTransitions().get(eventId) != null) {
            // Iterate through the Transitions until one of them fires.
            for (Transition trans : getTransitions().getTransitions().get(eventId)) {
                transResult = trans.fire(origin);
                if(transResult.isHasFired()) {                   
                    // Break out of loop. We're finished.
                    break;
                }
            }
        }
        // Else if there are no Transitions for this event and there is a 
        // superstate.
        else if(superState != null) {
            // Dispatch the event to the superstate.
            transResult = superState.dispatch(origin, eventId);
        }
        return transResult;
    }
	
	public void exit() {
		// If there is a superstate.
		if (superState != null) {
			// Set the superstate's history state to this state. This lets
			// the superstate remember which of its substates was last 
			// active before exiting.
			superState.setHistoryState(this);
		}
	}

	public void entry() {
	}

	public State enterByHistory() {
		State result = this;
		// If there is no history type.
		if (initialState != null) {
			// Enter the initial state.
			result = initialState.enterShallow();
		}
		return result;
	}

	private State enterShallow() {
		entry();
		State result = this;
		// If the lowest level has not been reached.
		if (initialState != null) {
			// Enter the next level initial state.
			result = initialState.enterShallow();
		}
		return result;
	}

	public void setSuperState(State superState) throws StateMachineException {
		if(this == superState) {
            throw new StateMachineException(
                "The superstate cannot be the same as this state.");
        }
        this.superState = superState;
        if(superState == null) {
            level = 1;
        } else {
        	level = superState.level + 1;
        }
	}

	public State getSuperState() {
		return superState;
	}

	public void setInitialState(State initialState) throws StateMachineException {
		if (this == initialState) {
			throw new StateMachineException(
					"State cannot be an initial state to itself.");
		} else if (initialState.getSuperState() != this) {
			throw new StateMachineException("State is not a direct substate.");
		}
		this.initialState = historyState = initialState;
	}

	public State getInitialState() {
		return initialState;
	}

	public void setHistoryState(State historyState) {
		this.historyState = historyState;
	}

	public State getHistoryState() {
		return historyState;
	}

	public void setLevel(int level) {
		this.level = level;
		for (State subState : getSubStates().getSubStates()) {
			subState.level = level + 1;
		}
	}

	public int getLevel() {
		return level;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getStateId() {
		return stateId;
	}

	public void setSubStates(SubstateCollection subStates) {
		this.subStates = subStates;
	}

	public SubstateCollection getSubStates() {
		return subStates;
	}
	
	public void setTransitions(TransitionCollection transitions) {
		this.transitions = transitions;
	}

	public TransitionCollection getTransitions() {
		return transitions;
	}



}
