package com.graly.mes.prd.workflow;

/**
 * base exception used for all exceptions thrown in jBPM.
 */
public class JbpmException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JbpmException() {
		super();
	}

	public JbpmException(String message, Throwable cause) {
		super(message, cause);
	}

	public JbpmException(String message) {
		super(message);
	}

	public JbpmException(Throwable cause) {
		super(cause);
	}
}
