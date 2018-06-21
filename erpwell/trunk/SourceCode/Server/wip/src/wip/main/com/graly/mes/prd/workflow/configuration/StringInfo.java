package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

public class StringInfo extends AbstractObjectInfo {

	private static final long serialVersionUID = 1L;

	String s = null;

	public StringInfo(Element stringElement, ObjectFactoryParser configParser) {
		super(stringElement, configParser);
		s = getValueString(stringElement);
	}

	public Object createObject(ObjectFactoryImpl objectFactory) {
		return s;
	}
}
