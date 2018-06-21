package com.graly.promisone.runtime.model;

import java.io.Serializable;
import org.w3c.dom.Element;

public interface Extension extends Serializable {
	
	String getExtensionPoint();
	
	Element getElement();
	
	void setElement(Element element);
	
	Object[] getContributions();
	
	void setContributions(Object[] contributions);
	
	ComponentInstance getComponent();
	
	void setComponent(ComponentInstance component);
	
}
