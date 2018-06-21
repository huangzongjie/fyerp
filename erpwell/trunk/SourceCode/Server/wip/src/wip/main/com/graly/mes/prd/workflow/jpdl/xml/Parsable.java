package com.graly.mes.prd.workflow.jpdl.xml;

import org.dom4j.Element;

public interface Parsable {
	void read(Element element, JpdlXmlReader jpdlReader);
	void write(Element element);
}
