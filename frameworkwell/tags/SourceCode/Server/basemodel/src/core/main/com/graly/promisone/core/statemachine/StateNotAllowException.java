package com.graly.promisone.core.statemachine;

import com.graly.promisone.core.exception.ClientException;

public class StateNotAllowException extends StateMachineException {

	public StateNotAllowException() {
		this.setErrorCode("error.state_not_allow");
	}
}
