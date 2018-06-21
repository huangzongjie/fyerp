package com.graly.framework.core.exception;

public class WrappedException extends Exception {
 
	private static final long serialVersionUID = -8068323167952050687L;

    // the class name of the original exception
    private String className;

    private WrappedException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getClassName() {
        return className;
    }

    public boolean sameAs(String className) {
        return this.className == null ? className == null : this.className.equals(className);
    }

    public static Throwable wrap(Throwable t) {
        if (t == null) {
            return null;
        }
        if (t instanceof ClientException) {
        	return t;
        }
        if (t instanceof ClientParameterException) {
        	return t;
        }
        if (t instanceof WrappedException) {
            return (WrappedException) t;
        }
        String exceptionClass = t.getClass().getName();
        String message = "Exception: " + exceptionClass + ". message: "
                + t.getMessage();
        Throwable cause =  wrap(t.getCause());
        WrappedException we = new WrappedException(message, cause);
        we.className = exceptionClass;
        we.setStackTrace(t.getStackTrace());
        return we;
    }
}
