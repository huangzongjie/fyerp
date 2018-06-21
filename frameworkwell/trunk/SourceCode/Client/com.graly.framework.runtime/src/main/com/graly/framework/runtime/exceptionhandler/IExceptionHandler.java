package com.graly.framework.runtime.exceptionhandler;

public interface IExceptionHandler {
	public void handleException(Thread thread, Throwable thrownException, Throwable triggerException);
}
