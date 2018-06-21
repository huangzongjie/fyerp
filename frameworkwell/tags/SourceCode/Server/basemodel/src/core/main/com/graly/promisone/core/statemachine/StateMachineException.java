package com.graly.promisone.core.statemachine;

import com.graly.promisone.core.exception.ClientException;

public class StateMachineException extends ClientException {
	
	public StateMachineException() {
	}
	
	public StateMachineException(String errorCode) {
		this.setErrorCode(errorCode);
	}
}
