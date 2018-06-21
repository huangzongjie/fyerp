package com.graly.mes.prd.workflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Specialized object input stream that allows classes to be fetched through a
 * custom class loader if the stream is unable to resolve them in the usual
 * manner.
 * 
 */
public class CustomLoaderObjectInputStream extends ObjectInputStream {

	private final ClassLoader customLoader;

	/**
	 * Provide a way for subclasses to not have to allocate private data just used
	 * by the platform's implementation of ObjectInputStream.
	 * @throws IOException if an I/O error occurs
	 */
	protected CustomLoaderObjectInputStream() throws IOException {
		customLoader = null;
	}

	/**
	 * Creates a <code>CustomLoaderObjectInputStream</code> that reads from the
	 * specified input stream and resolves classes using the context class loader.
	 * @param in input stream to read from
	 * @throws IOException if an I/O error occurs while reading stream header
	 * @throws SecurityException if untrusted subclass illegally overrides
	 * security-sensitive methods; note that this class does <em>not</em>
	 * override said methods
	 */
	public CustomLoaderObjectInputStream(InputStream in) throws IOException {
		this(in, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Creates a <code>CustomLoaderObjectInputStream</code> that reads from the
	 * specified input stream and resolves classes using the given class loader.
	 * @param in input stream to read from
	 * @param customLoader the loader to use for resolving classes
	 * @throws IOException if an I/O error occurs while reading stream header
	 * @throws IllegalArgumentException if <code>customLoader</code> is <code>null</code>
	 * @throws SecurityException if untrusted subclass illegally overrides
	 * security-sensitive methods; note that this class does <em>not</em>
	 * override said methods
	 */
	public CustomLoaderObjectInputStream(InputStream in,
			ClassLoader customLoader) throws IOException {
		super(in);
		if (customLoader == null) {
			throw new IllegalArgumentException("custom class loader is null");
		}
		this.customLoader = customLoader;
	}

	/**
	 * Returns the loader used by this stream for resolving classes.
	 */
	public ClassLoader getCustomLoader() {
		return customLoader;
	}

	protected Class resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException {
		try {
			return super.resolveClass(desc);
		} catch (ClassNotFoundException e) {
			return Class.forName(desc.getName(), false, customLoader);
		}
	}

	protected Class resolveProxyClass(String[] interfaces) throws IOException,
			ClassNotFoundException {
		try {
			return super.resolveProxyClass(interfaces);
		} catch (ClassNotFoundException e) {
			ClassLoader nonPublicLoader = null;
			// define proxy in class loader of non-public interface(s), if any
			Class[] classes = new Class[interfaces.length];
			for (int i = 0; i < interfaces.length; i++) {
				Class cl = Class.forName(interfaces[i], false, customLoader);
				if ((cl.getModifiers() & Modifier.PUBLIC) == 0) {
					if (nonPublicLoader == null) {
						nonPublicLoader = cl.getClassLoader();
					} else if (nonPublicLoader != cl.getClassLoader()) {
						throw new IllegalAccessError(
								"conflicting non-public interface class loaders");
					}
				}
				classes[i] = cl;
			}
			try {
				return Proxy.getProxyClass(
						nonPublicLoader != null ? nonPublicLoader : customLoader, classes);
			} catch (IllegalArgumentException iae) {
				throw new ClassNotFoundException(
						"could not get proxy class for interfaces: "
								+ Arrays.asList(classes), e);
			}
		}
	}

}
