package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.util.XmlUtil;

public abstract class AbstractObjectInfo implements ObjectInfo {

	private static final long serialVersionUID = 1L;

	String name = null;
	boolean isSingleton = false;

	public AbstractObjectInfo() {
	}

	public AbstractObjectInfo(Element element,
			ObjectFactoryParser objectFactoryParser) {
		if (element.hasAttribute("name")) {
			name = element.getAttribute("name");
			objectFactoryParser.addNamedObjectInfo(name, this);
		}
		if ("true".equalsIgnoreCase(element.getAttribute("singleton"))) {
			isSingleton = true;
		}
	}

	protected String getValueString(Element element) {
		String value = null;
		if (element.hasAttribute("value")) {
			value = element.getAttribute("value");
		} else {
			value = XmlUtil.getContentText(element);
		}
		return value;
	}

	public boolean hasName() {
		return (name != null);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSingleton() {
		return isSingleton;
	}

	public void setSingleton(boolean isSingleton) {
		this.isSingleton = isSingleton;
	}
}
