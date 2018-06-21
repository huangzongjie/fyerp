package com.graly.mes.prd.designer.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class Logger {
	
	public static void logInfo(String message) {
		log(IStatus.INFO, IStatus.OK, message, null);
	}
	
	public static void logError(Throwable exception) {
		logError("Unexpected Exception", exception);
	}
	
	public static void logError(String message, Throwable exception) {
		log(IStatus.ERROR, IStatus.OK, message, exception);
	}
	
	public static void log(int severity, int code, String message, Throwable exception) {
		log(createStatus(severity, code, message, exception));
	}
	
	public static IStatus createStatus(int severity, int code, String message, Throwable exception) {
		Activator activator = Activator.getDefault();
		Bundle bundle = activator.getBundle();
		String name = bundle.getSymbolicName();
		return new Status(
			severity,
			name,
			code,
			message,
			exception);
	}
	
	public static void log(IStatus status) {
		Activator.getDefault().getLog().log(status);
	}

}
