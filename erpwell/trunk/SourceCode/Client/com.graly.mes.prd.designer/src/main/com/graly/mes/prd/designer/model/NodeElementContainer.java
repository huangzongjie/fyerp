package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.SemanticElement;

public interface NodeElementContainer extends SemanticElement {

	void addNodeElement(NodeElement node);
	void removeNodeElement(NodeElement node);
	NodeElement[] getNodeElements();
	NodeElement getNodeElementByName(String nodeName);
	boolean canAdd(NodeElement node);

}
