package com.graly.framework.core.statemachine;

public class StateNotAllowException extends StateMachineException {

	private static final long serialVersionUID = 1L;
	
	public StateNotAllowException() {
		this.setErrorCode("error.state_not_allow");
	}
}
