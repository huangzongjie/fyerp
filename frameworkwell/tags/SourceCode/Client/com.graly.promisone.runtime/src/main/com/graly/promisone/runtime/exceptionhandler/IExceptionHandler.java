package com.graly.promisone.runtime.exceptionhandler;

public interface IExceptionHandler {
	public void handleException(Thread thread, Throwable thrownException, Throwable triggerException);
}
