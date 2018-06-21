package com.graly.mes.prd.workflow.configuration.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.JbpmConfiguration;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.configuration.ObjectFactory;
import com.graly.mes.prd.workflow.context.exe.Converter;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;

/**
 * provides access to the list of converters and ensures that the converter objects are unique.
 */
public abstract class Converters {

	private static final Logger logger = Logger.getLogger(Converters.class);

	static final int CONVERTERS_BY_CLASS_NAMES = 0;
	static final int CONVERTERS_BY_DATABASE_ID = 1;
	static final int CONVERTERS_IDS = 2;

	static Map converterMapsMap = new HashMap();

	// public methods

	public static Converter getConverterByClassName(String className) {
		Converter converter = (Converter) getConvertersByClassNames().get(
				className);
		if (converter == null) {
			throw new JbpmException("converter '" + className + "' is not declared in jbpm.converter.properties");
		}
		return converter;
	}

	public static Converter getConverterByDatabaseId(String converterDatabaseId) {
		return (Converter) getConvertersByDatabaseId().get(converterDatabaseId);
	}

	public static String getConverterId(Converter converter) {
		return (String) getConvertersIds().get(converter);
	}

	// maps class names to unique converter objects
	static Map getConvertersByClassNames() {
		return getConverterMaps()[CONVERTERS_BY_CLASS_NAMES];
	}

	// maps converter database-id-strings to unique converter objects 
	static Map getConvertersByDatabaseId() {
		return getConverterMaps()[CONVERTERS_BY_DATABASE_ID];
	}

	// maps unique converter objects to their database-id-string
	static Map getConvertersIds() {
		return getConverterMaps()[CONVERTERS_IDS];
	}

	static Map[] getConverterMaps() {
		Map[] converterMaps = null;
		synchronized (converterMapsMap) {
			ObjectFactory objectFactory = JbpmConfiguration.Configs.getObjectFactory();
			converterMaps = (Map[]) converterMapsMap.get(objectFactory);
			if (converterMaps == null) {
				converterMaps = createConverterMaps(objectFactory);
				converterMapsMap.put(objectFactory, converterMaps);
			}
		}
		return converterMaps;
	}

	static Map<String, Converter>[] createConverterMaps(ObjectFactory objectFactory) {
		Map[] converterMaps = new Map[3];
		converterMaps[CONVERTERS_BY_CLASS_NAMES] = new HashMap<String, Converter>();
		converterMaps[CONVERTERS_BY_DATABASE_ID] = new HashMap<String, Converter>();
		converterMaps[CONVERTERS_IDS] = new HashMap<Converter, String>();

		Map<String, Converter> convertersByClassNames = converterMaps[CONVERTERS_BY_CLASS_NAMES];
		Map<String, Converter> convertersByDatabaseId = converterMaps[CONVERTERS_BY_DATABASE_ID];
		Map<Converter, String> convertersIds = converterMaps[CONVERTERS_IDS];

		Properties converterProperties = null;
		if (objectFactory.hasObject("resource.converter")) {
			String resource = (String) objectFactory.createObject("resource.converter");
			converterProperties = ClassLoaderUtil.getProperties(resource);
		} else {
			converterProperties = new Properties();
		}

		Iterator iter = converterProperties.keySet().iterator();
		while (iter.hasNext()) {
			String converterDatabaseId = (String) iter.next();
			if (converterDatabaseId.length() != 1)
				throw new JbpmException(
						"converter-ids must be of length 1 (to be stored in a char)");
			if (convertersByDatabaseId.containsKey(converterDatabaseId))
				throw new JbpmException("duplicate converter id : '" + converterDatabaseId + "'");
			String converterClassName = converterProperties.getProperty(converterDatabaseId);
			try {
				Class converterClass = ClassLoaderUtil.loadClass(converterClassName);
				Converter converter = (Converter) converterClass.newInstance();
				logger.debug("adding converter '" + converterDatabaseId + "', '"
						+ converterClassName + "'");
				convertersByClassNames.put(converterClassName, converter);
				convertersByDatabaseId.put(converterDatabaseId, converter);
				convertersIds.put(converter, converterDatabaseId);
			} catch (Exception e) {
				// NOTE that Error's are not caught because that might halt the JVM and mask the original Error.
				logger.debug("couldn't instantiate converter '" + converterClassName + "': " + e);
			}
		}

		return converterMaps;
	}
}
