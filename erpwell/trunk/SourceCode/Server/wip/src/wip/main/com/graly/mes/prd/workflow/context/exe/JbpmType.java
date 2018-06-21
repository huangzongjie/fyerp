package com.graly.mes.prd.workflow.context.exe;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.graly.mes.prd.workflow.JbpmConfiguration;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.configuration.ObjectFactory;
import com.graly.mes.prd.workflow.configuration.ObjectFactoryParser;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;

/**
 * specifies for one java-type how jbpm is able to persist objects of that type in the database. 
 */
public class JbpmType {

	static Map<ObjectFactory, List<JbpmType>> jbpmTypesCache = new HashMap<ObjectFactory, List<JbpmType>>();

	JbpmTypeMatcher jbpmTypeMatcher = null;
	Converter converter = null;
	Class variableInstanceClass = null;

	public JbpmType(JbpmTypeMatcher jbpmTypeMatcher, Converter converter,
			Class variableInstanceClass) {
		this.jbpmTypeMatcher = jbpmTypeMatcher;
		this.converter = converter;
		this.variableInstanceClass = variableInstanceClass;
	}

	public boolean matches(Object value) {
		return jbpmTypeMatcher.matches(value);
	}

	public VariableInstance newVariableInstance() {
		VariableInstance variableInstance = null;
		try {
			variableInstance = (VariableInstance) variableInstanceClass.newInstance();
			variableInstance.setConverter(converter);
		} catch (Exception e) {
			throw new JbpmException(
					"couldn't instantiate variable instance class '" + variableInstanceClass.getName() + "'");
		}
		return variableInstance;
	}

	public static List<JbpmType> getJbpmTypes() {
		List<JbpmType> jbpmTypes = null;
		synchronized (jbpmTypesCache) {
			ObjectFactory objectFactory = JbpmConfiguration.Configs.getObjectFactory();
			jbpmTypes = (List<JbpmType>) jbpmTypesCache.get(objectFactory);
			if (jbpmTypes == null) {
				if (JbpmConfiguration.Configs.hasObject("jbpm.types")) {
					jbpmTypes = (List<JbpmType>) JbpmConfiguration.Configs.getObject("jbpm.types");
				} else {
					jbpmTypes = getDefaultJbpmTypes();
				}
				jbpmTypesCache.put(objectFactory, jbpmTypes);
			}
		}
		return jbpmTypes;
	}

	private static List<JbpmType> getDefaultJbpmTypes() {
		String resource = JbpmConfiguration.Configs.getString("resource.varmapping");
		InputStream is = ClassLoaderUtil.getStream(resource);
		ObjectFactory objectFactory = ObjectFactoryParser.parseInputStream(is);
		return (List<JbpmType>) objectFactory.createObject("jbpm.types");
	}
}
