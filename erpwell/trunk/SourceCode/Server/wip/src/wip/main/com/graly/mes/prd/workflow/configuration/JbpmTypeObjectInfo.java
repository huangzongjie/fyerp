package com.graly.mes.prd.workflow.configuration;


import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.configuration.converter.Converters;
import com.graly.mes.prd.workflow.context.exe.Converter;
import com.graly.mes.prd.workflow.context.exe.JbpmType;
import com.graly.mes.prd.workflow.context.exe.JbpmTypeMatcher;
import com.graly.mes.prd.workflow.context.exe.VariableInstance;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class JbpmTypeObjectInfo extends AbstractObjectInfo {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JbpmTypeObjectInfo.class);
	
	ObjectInfo typeMatcherObjectInfo = null;
	Converter converter = null;
	Class variableInstanceClass = null;

	public JbpmTypeObjectInfo(Element jbpmTypeElement,
			ObjectFactoryParser objectFactoryParser) {
		super(jbpmTypeElement, objectFactoryParser);

		try {
			Element typeMatcherElement = XmlUtil.element(jbpmTypeElement, "matcher");
			if (typeMatcherElement == null) {
				throw new ConfigurationException(
						"matcher is a required element in a jbpm-type: "
								+ XmlUtil.toString(jbpmTypeElement));
			}
			Element typeMatcherBeanElement = XmlUtil
					.element(typeMatcherElement);
			typeMatcherObjectInfo = objectFactoryParser
					.parse(typeMatcherBeanElement);

			Element converterElement = XmlUtil.element(jbpmTypeElement, "converter");
			if (converterElement != null) {
				if (!converterElement.hasAttribute("class")) {
					throw new ConfigurationException(
							"class attribute is required in a converter element: "
									+ XmlUtil.toString(jbpmTypeElement));
				}
				String converterClassName = converterElement.getAttribute("class");
				converter = Converters.getConverterByClassName(converterClassName);
			}

			Element variableInstanceElement = XmlUtil.element(jbpmTypeElement, "variable-instance");
			if (!variableInstanceElement.hasAttribute("class")) {
				throw new ConfigurationException(
						"class is a required attribute in element variable-instance: "
								+ XmlUtil.toString(jbpmTypeElement));
			}
			String variableInstanceClassName = variableInstanceElement
					.getAttribute("class");
			variableInstanceClass = ClassLoaderUtil
					.loadClass(variableInstanceClassName);
			if (!VariableInstance.class.isAssignableFrom(variableInstanceClass)) {
				throw new ConfigurationException("variable instance class '"
						+ variableInstanceClassName
						+ "' is not a VariableInstance");
			}
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			// NOTE that Error's are not caught because that might halt the JVM and mask the original Error.
			// Probably the user doesn't need support for this type and doesn't have a required library in the path.
			// So let's log and ignore
			logger.debug("jbpm variables type "
					+ XmlUtil.toString(jbpmTypeElement)
					+ " couldn't be instantiated properly: " + e.toString());
			// now, let's make sure that this JbpmType is ignored by always returning false in the JbpmTypeMatcher
			typeMatcherObjectInfo = new ObjectInfo() {
				private static final long serialVersionUID = 1L;

				public boolean hasName() {
					return false;
				}

				public String getName() {
					return null;
				}

				public boolean isSingleton() {
					return true;
				}

				public Object createObject(ObjectFactoryImpl objectFactory) {
					return new JbpmTypeMatcher() {
						private static final long serialVersionUID = 1L;

						public boolean matches(Object value) {
							return false;
						}
					};
				}
			};
			converter = null;
			variableInstanceClass = null;
		}
	}

	public Object createObject(ObjectFactoryImpl objectFactory) {
		JbpmTypeMatcher jbpmTypeMatcher = (JbpmTypeMatcher) objectFactory
				.createObject(typeMatcherObjectInfo);
		return new JbpmType(jbpmTypeMatcher, converter, variableInstanceClass);
	}
}
