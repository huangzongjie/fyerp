package com.graly.mes.prd.workflow.instantiation;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.JbpmException;

public class ConstructorInstantiator implements Instantiator {

	static final Logger logger = Logger.getLogger(ConstructorInstantiator.class);
	
	private static final Class[] parameterTypes = new Class[] { String.class };

	public Object instantiate(Class clazz, String configuration) {
		Object newInstance = null;
		try {
			Constructor constructor = clazz.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			newInstance = constructor.newInstance(new Object[] { configuration });
		} catch (Exception e) {
			 logger.error("couldn't instantiate '" + clazz.getName() + "'", e);
			throw new JbpmException(e);
		}
		return newInstance;
	}
}
