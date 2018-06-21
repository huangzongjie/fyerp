package com.graly.promisone.base.ui.exceptionhandler;

import org.apache.log4j.Logger;

import com.graly.promisone.runtime.exceptionhandler.IExceptionHandler;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.UI;
import com.graly.promisone.core.exception.ClientException;
import com.graly.promisone.core.exception.ClientParameterException;

public class ClientParameterExceptionHandler implements IExceptionHandler {

	private static final Logger logger = Logger.getLogger(ClientParameterExceptionHandler.class);
	
	@Override
	public void handleException(Thread thread, Throwable thrownException,
			Throwable triggerException) {
		try {
			logger.error("ThrowableHandler handling an error!", thrownException);
			String errorCode;
			Object[] parameters = new Object[] {};
			ClientParameterException ex = (ClientParameterException)thrownException;
			errorCode = ex.getErrorCode();
			parameters = ex.getParameters();
			
			if (errorCode != null && !"".equals(errorCode.trim())){
				String errMessage = Message.getString(errorCode);
				if (errMessage == null || "".equals(errMessage.trim())){
					errMessage = errorCode;
				} else {
					errMessage = String.format(errMessage, parameters);
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
