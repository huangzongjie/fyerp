package com.graly.promisone.base.ui.exceptionhandler;

import org.apache.log4j.Logger;

import com.graly.promisone.runtime.exceptionhandler.IExceptionHandler;
import com.graly.promisone.base.ui.util.UI;

public class OutOfMemoryHandler implements IExceptionHandler {

	private static final Logger logger = Logger.getLogger(OutOfMemoryHandler.class);
	
	@Override
	public void handleException(Thread thread, Throwable thrownException,
			Throwable triggerException) {
		try {
			logger.error("ThrowableHandler handling an error!", thrownException);
			UI.showError(thrownException.getMessage(), thrownException, null);

		} catch (Throwable error) {
			logger.fatal("While handling an exception, another one occured!", error); 
		}
	}
}
