package com.graly.promisone.core.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class ClientParameterException extends ClientException {
	 
	private static final long serialVersionUID = 829907884555472415L;

	private String errorCode;
	private Object[] parameters;
	
	public ClientParameterException() {
	}

	public ClientParameterException(String errorCode) {
		this.setErrorCode(errorCode);
	}

	public ClientParameterException(String errorCode, Object ... parameters) {
		this(errorCode);
		this.setParameters(parameters);
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}
}
