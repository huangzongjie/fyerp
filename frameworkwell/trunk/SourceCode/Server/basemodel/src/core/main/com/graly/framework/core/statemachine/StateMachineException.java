package com.graly.framework.core.statemachine;

import com.graly.framework.core.exception.ClientException;

public class StateMachineException extends ClientException {
	
	private static final long serialVersionUID = 1L;
	
	public StateMachineException() {
	}
	
	public StateMachineException(String errorCode) {
		this.setErrorCode(errorCode);
	}
}
