package com.graly.promisone.runtime.exceptionhandler;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.util.ISafeRunnableRunner;

public class SaveRunnableRunner implements ISafeRunnableRunner {

	public SaveRunnableRunner() {
		super();
	}
	
	@Override
	public void run(ISafeRunnable code) {
		try {
			code.run();
		} catch (Throwable t) {
			ExceptionHandlerManager.asyncHandleException(t);
		}
	}

}
