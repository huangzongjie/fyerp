package com.graly.promisone.core.exception;

public class WrappedException extends Exception {
 
	private static final long serialVersionUID = -8068323167952050687L;

    // the class name of the original exception
    private String className;

    private WrappedException(String message, WrappedException cause) {
        super(message, cause);
    }

    public String getClassName() {
        return className;
    }

    public boolean sameAs(String className) {
        return this.className == null
            ?  className == null : this.className.equals(className);
    }

    public static WrappedException wrap(Throwable t) {
        if (t == null) {
            return null;
        }
        if (t instanceof WrappedException) {
            return (WrappedException) t;
        }
        String exceptionClass = t.getClass().getName();
        String message = "Exception: " + exceptionClass + ". message: "
                + t.getMessage();
        WrappedException cause =  wrap(t.getCause());
        WrappedException we = new WrappedException(message, cause);
        we.className = exceptionClass;
        we.setStackTrace(t.getStackTrace());
        return we;
    }
}
