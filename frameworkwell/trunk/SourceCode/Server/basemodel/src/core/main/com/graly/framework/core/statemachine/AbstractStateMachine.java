package com.graly.framework.core.statemachine;

import java.util.Stack;

public abstract class AbstractStateMachine {
	
	private State currentState = null;
	private boolean initialized = false;        
	
	protected void initialize(State initialState) throws StateMachineException { 
        if(initialState == null) {
        	throw new StateMachineException("initialState is null.");
        }
        State superState = initialState;
        Stack<State> superStateStack = new Stack<State>();           

        // If the initial state is a substate, travel up the state 
        // hierarchy in order to descend from the top state to the initial
        // state.
        while(superState != null) {
        	superStateStack.push(superState);
        	superState = superState.getSuperState();
        }
        // While there are superstates to traverse.
        while(superStateStack.size() > 0) {
        	superState = (State)superStateStack.pop();
        	superState.entry();
        }
        setCurrentState(initialState.enterByHistory());
        initialized = true;
    }
	
	public State send(String eventId) throws StateMachineException {
		TransitionResult result = getCurrentState().dispatch(eventId);
		if(result.isHasFired()) {
            setCurrentState(result.getNewState());
        } else {
        	throw new StateNotAllowException();
        }
		return getCurrentState();
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public State getCurrentState() {
		return currentState;
	}
}
