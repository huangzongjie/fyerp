package com.graly.promisone.core.constraint;

public class ConstraintContext {
	public int OK_ID = 0;
	public int FAILED_ID = 1;
	
	private String returnCode;
	private String returnMessage;
	
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnCode() {
		return returnCode;
	}
	
	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}
	public String getReturnMessage() {
		return returnMessage;
	}
}
