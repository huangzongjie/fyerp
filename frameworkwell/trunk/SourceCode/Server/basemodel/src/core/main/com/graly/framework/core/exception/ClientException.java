package com.graly.framework.core.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class ClientException extends Exception {
	
	private static final long serialVersionUID = 829907884555472415L;

	private String errorCode;
	
	public ClientException() {
	}

	public ClientException(String errorCode) {
		this.setErrorCode(errorCode);
	}

	public ClientException(String message, ClientException cause) {
		super(message, cause);
	}

	public ClientException(String message, Throwable cause) {
		super(message, WrappedException.wrap(cause));
	}

	public ClientException(Throwable cause) {
		super(WrappedException.wrap(cause));
		if (cause instanceof ClientException) {
			this.errorCode = ((ClientException)cause).errorCode;
		}
	}

	public ClientException(ClientException cause) {
		super(cause);
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
