package com.graly.mes.prd.workflow.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class BeanInfo extends AbstractObjectInfo {

	private static final long serialVersionUID = 1L;

	String className = null;
	ConstructorInfo constructorInfo = null;
	PropertyInfo[] propertyInfos = null;

	public BeanInfo() {
	}

	public BeanInfo(Element beanElement, ObjectFactoryParser objectFactoryParser) {
		super(beanElement, objectFactoryParser);

		// parse constructor or factory
		Element constructorElement = XmlUtil.element(beanElement, "constructor");
		if (constructorElement != null) {
			constructorInfo = new ConstructorInfo(constructorElement, objectFactoryParser);
			constructorInfo.beanInfo = this;
		}

		if (beanElement.hasAttribute("class")) {
			className = beanElement.getAttribute("class");
		} else if ((constructorInfo.factoryRefName == null)
				&& (constructorInfo.factoryClassName == null)) {
			throw new JbpmException("bean element must have a class attribute: " + XmlUtil.toString(beanElement));
		}

		// parse fields
		List<Object> propertyInfoList = new ArrayList<Object>();
		Iterator iter = XmlUtil.elementIterator(beanElement, "field");
		while (iter.hasNext()) {
			Element fieldElement = (Element) iter.next();
			propertyInfoList.add(new FieldInfo(fieldElement, objectFactoryParser));
		}

		// parse properties
		iter = XmlUtil.elementIterator(beanElement, "property");
		while (iter.hasNext()) {
			Element propertyElement = (Element) iter.next();
			propertyInfoList.add(new PropertyInfo(propertyElement, objectFactoryParser));
		}

		propertyInfos = (PropertyInfo[]) propertyInfoList.toArray(new PropertyInfo[propertyInfoList.size()]);
	}

	public Object createObject(ObjectFactoryImpl objectFactory) {
		Object object = null;

		if (constructorInfo == null) {
			if (className == null)
				throw new JbpmException("bean '" + getName()
						+ "' doesn't have a class or constructor specified");
			try {
				Class clazz = objectFactory.loadClass(className);
				object = clazz.newInstance();
			} catch (Exception e) {
				throw new JbpmException("couldn't instantiate bean '"
						+ getName() + "' of type '" + className + "'", e);
			}
		} else {
			object = constructorInfo.createObject(objectFactory);
		}

		if (className == null)
			className = object.getClass().getName();

		if (propertyInfos != null) {
			for (int i = 0; i < propertyInfos.length; i++) {
				propertyInfos[i].injectProperty(object, objectFactory);
			}
		}

		return object;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
