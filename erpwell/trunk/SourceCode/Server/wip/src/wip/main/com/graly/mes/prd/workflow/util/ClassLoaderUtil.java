package com.graly.mes.prd.workflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.instantiation.ProcessClassLoader;
/**
 * provides centralized classloader lookup. 
 */
public class ClassLoaderUtil {

	public static Class loadClass(String className) {
		try {
			return getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new JbpmException("class not found '" + className + "'", e);
		}
	}

	public static ClassLoader getClassLoader() {
		return ClassLoaderUtil.class.getClassLoader();
	}

	public static InputStream getStream(String resource) {
		return getClassLoader().getResourceAsStream(resource);
	}

	public static Properties getProperties(String resource) {
		Properties properties = new Properties();
		try {
			properties.load(getStream(resource));
		} catch (IOException e) {
			throw new JbpmException("couldn't load properties file '" + resource + "'", e);
		}
		return properties;
	}
	
	public static ClassLoader getProcessClassLoader(ProcessDefinition processDefinition) {
		return new ProcessClassLoader(ClassLoaderUtil.class.getClassLoader(), processDefinition);
	}

}
