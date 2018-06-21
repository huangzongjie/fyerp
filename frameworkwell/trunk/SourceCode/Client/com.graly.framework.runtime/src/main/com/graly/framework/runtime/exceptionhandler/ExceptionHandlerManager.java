package com.graly.framework.runtime.exceptionhandler;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.swt.widgets.Display;

public final class ExceptionHandlerManager {
	
	private static final Logger logger = Logger.getLogger(ExceptionHandlerManager.class);

	private static final ExceptionHandlerManager instance = new ExceptionHandlerManager();
	
	private Map<String, IExceptionHandler> exceptionHandlers = new HashMap<String, IExceptionHandler>();
	private Object synchronizedObject = new Object();

	
	private ExceptionHandlerManager() {
    }

    public static ExceptionHandlerManager getInstance() {
    	return instance;
    }
    
	public void addExceptionHandler(String targetType, IExceptionHandler handler) {		
		synchronized(synchronizedObject){
			logger.debug("An exceptionHandler was already defined for "+targetType+" !"); 
			exceptionHandlers.put(targetType, handler);
		}
	}
	
	private boolean handleException(final Thread thread, final Throwable exception, boolean async) {
		final ExceptionHandlerSearchResult handlerSearch = instance.searchHandler(exception);
		if (handlerSearch.getHandler() != null){
			try {
				Runnable runnable = new Runnable(){
					public void run(){
						try {
							handlerSearch.getHandler().handleException(
									thread, exception, handlerSearch.getTriggerException());
						} catch(Throwable x) {
							logger.fatal("Exception occured while handling exception on GUI thread!", x); //$NON-NLS-1$
						}
					}
				};

				if (async)
					Display.getDefault().asyncExec(runnable);
				else
					Display.getDefault().syncExec(runnable);
				
			} catch (Throwable ex) {
				logger.fatal("Exception occured while handling exception on causing thread!", ex); 
		  }
			return true;
		} else {
			logger.fatal("Did not find an ExceptionHandler for this Throwable!", exception); 
			return false;
		}
	}
	
	protected IExceptionHandler getExceptionHandler(String targetType){
		synchronized(synchronizedObject){
			if (exceptionHandlers.containsKey(targetType))
				return exceptionHandlers.get(targetType);
			else
				return null;
		}
	}
	
	public void removeExceptionHandler(String targetType){
		synchronized(synchronizedObject){
			if (exceptionHandlers.containsKey(targetType))
				exceptionHandlers.remove(targetType);
		}
	}
	
	protected IExceptionHandler getExceptionHandler(Class targetType){
		return getExceptionHandler(targetType.getName());
	}
	
	protected boolean haveHandler(Class targetType){
		return exceptionHandlers.containsKey(targetType.getName());
	}
	
	public ExceptionHandlerSearchResult searchHandler(Throwable exception){
		ExceptionHandlerSearchResult rootCauseResult = getTopLevelCauseHandler(exception);
		if (rootCauseResult != null)
			return rootCauseResult;
		
		Class classRun = exception.getClass();
		Throwable exceptionRun = exception;		
		while (exceptionRun != null) {
			
		  classRun = exceptionRun.getClass();
		  while ( (!haveHandler(classRun)) && (!classRun.equals(Throwable.class)) ) {
			  classRun = classRun.getSuperclass();
		  }
			
		  if (!classRun.equals(Throwable.class))
		  	if (haveHandler(classRun))
		  		break;
		  
			exceptionRun = ExceptionUtils.getCause(exceptionRun);
		}
		
		ExceptionHandlerSearchResult result = new ExceptionHandlerSearchResult();
		result.setHandler(getExceptionHandler(classRun));
		if (exceptionRun == null)
			exceptionRun = exception;
		result.setTriggerException(exceptionRun);
		// returns null if none registered
		return result;
	}
	
	public ExceptionHandlerSearchResult getTopLevelCauseHandler(Throwable x) {
		ExceptionHandlerSearchResult handler = null;
		Throwable cause = x.getCause();
		if(cause != null)
			handler = getTopLevelCauseHandler(cause);
		if((handler == null) || (cause == null)) {
			if (haveHandler(x.getClass())) {
				IExceptionHandler eHandler = getExceptionHandler(x.getClass());
				handler = new ExceptionHandlerSearchResult();
				handler.setHandler(eHandler);
				handler.setTriggerException(x);
			}
		}
		return handler;
	}
	
	public static void asyncHandleException(Throwable exception) {
		instance.handleException(Thread.currentThread(), exception, true);
	}

	public static boolean asyncHandleException(Thread thread, Throwable exception) {
		return instance.handleException(thread, exception, true);
	}
	
	public static boolean syncHandleException(Throwable exception) {
		return instance.handleException(Thread.currentThread(), exception, false);
	}
	
	public static boolean syncHandleException(Thread thread, Throwable exception) {
		return instance.handleException(thread, exception, false);
	}

	public static class ExceptionHandlerSearchResult {
		private Throwable triggerException;
		private IExceptionHandler handler;
		
		public IExceptionHandler getHandler() {
			return handler;
		}
		public void setHandler(IExceptionHandler handler) {
			this.handler = handler;
		}
		public Throwable getTriggerException() {
			return triggerException;
		}
		public void setTriggerException(Throwable triggerException) {
			this.triggerException = triggerException;
		}
	}
}
