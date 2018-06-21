package com.graly.mes.prd.workflow.instantiation;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.JbpmException;

public class ConfigurationPropertyInstantiator implements Instantiator {

	static final Logger logger = Logger.getLogger(ConfigurationPropertyInstantiator.class);
	
	private static final Class[] parameterTypes = new Class[] { String.class };

	public Object instantiate(Class clazz, String configuration) {
		Object newInstance = null;
		try {
			// create the object
			newInstance = clazz.newInstance();

			// set the configuration with the bean-style setter
			Method setter = clazz.getDeclaredMethod("setConfiguration", parameterTypes);
			setter.setAccessible(true);
			setter.invoke(newInstance, new Object[] { configuration });

		} catch (Exception e) {
			logger.error("couldn't instantiate '" + clazz.getName() + "'", e);
			throw new JbpmException(e);
		}
		return newInstance;
	}
}
