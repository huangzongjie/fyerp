package com.graly.mes.prd.workflow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.configuration.ObjectFactory;
import com.graly.mes.prd.workflow.configuration.ObjectFactoryImpl;
import com.graly.mes.prd.workflow.configuration.ObjectFactoryParser;
import com.graly.mes.prd.workflow.configuration.ObjectInfo;
import com.graly.mes.prd.workflow.configuration.ValueInfo;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;

public class JbpmConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JbpmConfiguration.class);
	
	static ObjectFactory defaultObjectFactory = null;
	static Map<String, JbpmConfiguration> instances = new HashMap<String, JbpmConfiguration>();
	
	static void reset() {
		defaultObjectFactory = null;
		instances = new HashMap<String, JbpmConfiguration>();
	}
	
	ObjectFactory objectFactory = null;
	static ThreadLocal<Stack<JbpmConfiguration>> jbpmConfigurationsStacks = new ThreadLocal<Stack<JbpmConfiguration>>();
	
	public JbpmConfiguration(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public static JbpmConfiguration getInstance() {
		return getInstance(null);
	}

	public static JbpmConfiguration getInstance(String resource) {
		JbpmConfiguration instance = null;
		synchronized (instances) {
			if (resource == null) {
				resource = "jbpm.cfg.xml";
			}
			instance = (JbpmConfiguration) instances.get(resource);
			if (instance == null) {
				if (defaultObjectFactory != null) {
					logger.debug("creating jbpm configuration from given default object factory '" + defaultObjectFactory + "'");
					instance = new JbpmConfiguration(defaultObjectFactory);
				} else {
					try {
						logger.info("using jbpm configuration resource '" + resource + "'");
						InputStream jbpmCfgXmlStream = ClassLoaderUtil.getStream(resource);

						// if a resource SHOULD BE used, but is not found in the classpath
						// throw exception (otherwise, the user wants to load own stuff
						// but is confused, if it is not found and not loaded, without
						// any notice)
						if (jbpmCfgXmlStream == null)
							throw new JbpmException("jbpm configuration resource '" + resource + "' is not available");

						ObjectFactory objectFactory = parseObjectFactory(jbpmCfgXmlStream);
						instance = createJbpmConfiguration(objectFactory);
					} catch (RuntimeException e) {
						throw new JbpmException("couldn't parse jbpm configuration from resource '"	+ resource + "'", e);
					}
				}
				instances.put(resource, instance);
			}
		}
		return instance;
	}

	protected static ObjectFactory parseObjectFactory(InputStream inputStream) {
		logger.debug("loading defaults in jbpm configuration");
		ObjectFactoryParser objectFactoryParser = new ObjectFactoryParser();
		ObjectFactoryImpl objectFactoryImpl = new ObjectFactoryImpl();
		objectFactoryParser.parseElementsFromResource("com/graly/mes/prd/workflow/default.jbpm.cfg.xml", objectFactoryImpl);

		if (inputStream != null) {
			logger.debug("loading specific configuration...");
			objectFactoryParser.parseElementsStream(inputStream, objectFactoryImpl);
		}
		return objectFactoryImpl;
	}
	
	public static JbpmConfiguration parseXmlString(String xml) {
		logger.debug("creating jbpm configuration from xml string");
		InputStream inputStream = null;
		if (xml != null) {
			inputStream = new ByteArrayInputStream(xml.getBytes());
		}
		ObjectFactory objectFactory = parseObjectFactory(inputStream);
		return createJbpmConfiguration(objectFactory);
	}
	  
	protected static JbpmConfiguration createJbpmConfiguration(ObjectFactory objectFactory) {
		JbpmConfiguration jbpmConfiguration = new JbpmConfiguration(objectFactory);
		// now we make the bean jbpm.configuration always availble
		if (objectFactory instanceof ObjectFactoryImpl) {
			ObjectFactoryImpl objectFactoryImpl = (ObjectFactoryImpl) objectFactory;
			ObjectInfo jbpmConfigurationInfo = new ValueInfo("jbpmConfiguration", jbpmConfiguration);
			objectFactoryImpl.addObjectInfo(jbpmConfigurationInfo);
		}
		return jbpmConfiguration;
	}
	
	public static JbpmConfiguration parseInputStream(InputStream inputStream) {
		ObjectFactory objectFactory = parseObjectFactory(inputStream);
		logger.debug("creating jbpm configuration from input stream");
		return createJbpmConfiguration(objectFactory);
	}

	public static JbpmConfiguration parseResource(String resource) {
		InputStream inputStream = null;
		logger.debug("creating jbpm configuration from resource '" + resource + "'");
		if (resource != null) {
			inputStream = ClassLoaderUtil.getStream(resource);
		}
		ObjectFactory objectFactory = parseObjectFactory(inputStream);
		return createJbpmConfiguration(objectFactory);
	}
		  
	public abstract static class Configs {
		public static ObjectFactory getObjectFactory() {
			ObjectFactory objectFactory = null;
			objectFactory = getInstance().objectFactory;
			return objectFactory;
		}

		public static void setDefaultObjectFactory(ObjectFactory objectFactory) {
			defaultObjectFactory = objectFactory;
		}

		public static boolean hasObject(String name) {
			ObjectFactory objectFactory = getObjectFactory();
			return objectFactory.hasObject(name);
		}

		public static synchronized Object getObject(String name) {
			ObjectFactory objectFactory = getObjectFactory();
			return objectFactory.createObject(name);
		}

		public static String getString(String name) {
			return (String) getObject(name);
		}

		public static long getLong(String name) {
			return ((Long) getObject(name)).longValue();
		}

		public static int getInt(String name) {
			return ((Integer) getObject(name)).intValue();
		}

		public static boolean getBoolean(String name) {
			return ((Boolean) getObject(name)).booleanValue();
		}
	}
}
