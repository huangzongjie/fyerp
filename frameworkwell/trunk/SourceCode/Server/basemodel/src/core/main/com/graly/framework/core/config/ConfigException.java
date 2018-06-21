package com.graly.framework.core.config;

public class ConfigException extends RuntimeException
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Create a new ConfigException.
	 */
	public ConfigException() {
		super();
	}

	/**
	 * Create a new ConfigException.
	 * @param  message the detail message (which is saved for later retrieval
	 *         by the {@link #getMessage()} method).
	 */
	public ConfigException(String message) {
		super(message);
	}

	/**
	 * Create a new ConfigException.
	 * @param  message the detail message (which is saved for later retrieval
	 *         by the {@link #getMessage()} method).
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public ConfigException(String message, Throwable cause)	{
		super(message, cause);
	}

	/**
	 * Create a new ConfigException.
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public ConfigException(Throwable cause)	{
		super(cause);
	}
}
