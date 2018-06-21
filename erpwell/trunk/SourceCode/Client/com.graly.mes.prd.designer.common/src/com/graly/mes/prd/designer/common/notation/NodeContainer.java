package com.graly.mes.prd.designer.common.notation;

import java.util.List;

public interface NodeContainer extends NotationElement {

	void addNode(Node node);
	void removeNode(Node node);
	List<Node> getNodes();
}
