package com.graly.framework.base.ui.exceptionhandler;

import org.apache.log4j.Logger;

import com.graly.framework.runtime.exceptionhandler.IExceptionHandler;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;

public class ClientParameterExceptionHandler implements IExceptionHandler {

	private static final Logger logger = Logger.getLogger(ClientParameterExceptionHandler.class);
	
	@Override
	public void handleException(Thread thread, Throwable thrownException,
			Throwable triggerException) {
		try {
			logger.error("ThrowableHandler handling an error!", triggerException);
			String errorCode;
			Object[] parameters = new Object[] {};
			ClientParameterException ex = (ClientParameterException)triggerException;
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
			
			UI.showError(triggerException.getMessage(), triggerException, null);
		} catch (Throwable error) {
			logger.fatal("While handling an exception, another one occured!", error); 
		}
	}
}
