package com.graly.promisone.base.ui.exceptionhandler;

import org.apache.log4j.Logger;

import com.graly.promisone.runtime.exceptionhandler.IExceptionHandler;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.UI;
import com.graly.promisone.core.exception.ClientException;

public class ClientExceptionHandler implements IExceptionHandler {

	private static final Logger logger = Logger.getLogger(ClientExceptionHandler.class);
	
	@Override
	public void handleException(Thread thread, Throwable thrownException,
			Throwable triggerException) {
		try {
			logger.error("ThrowableHandler handling an error!", thrownException);
			ClientException ex = (ClientException)thrownException;
			if (ex.getErrorCode() != null && !"".equals(ex.getErrorCode().trim())){
				String errMessage = Message.getString(ex.getErrorCode());
				if (errMessage == null || "".equals(errMessage.trim())){
					errMessage = ex.getErrorCode();
				}
				UI.showError(errMessage);
				return;
			}
			UI.showError(thrownException.getMessage(), thrownException, null);
		} catch (Throwable error) {
			logger.fatal("While handling an exception, another one occured!", error); 
		}
	}
}
