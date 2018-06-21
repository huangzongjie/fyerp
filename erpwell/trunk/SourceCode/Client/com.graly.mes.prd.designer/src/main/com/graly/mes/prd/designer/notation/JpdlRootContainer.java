package com.graly.mes.prd.designer.notation;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import com.graly.mes.prd.designer.common.model.NamedElement;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.Transition;

public class JpdlRootContainer extends RootContainer {
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventName = evt.getPropertyName();
		if (eventName.equals("startStateAdd") || eventName.equals("nodeElementAdd")) {
			addNode(evt);
		} else if (eventName.equals("startStateRemove") || eventName.equals("nodeElementRemove")) {
			removeNode(evt);
		} else {
			super.propertyChange(evt);
		}
	}

	protected void addArrivingEdges(Node node) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < danglingEdges.size(); i++) {
			String name= ((NamedElement)node.getSemanticElement()).getName();
			Edge edge = (Edge)danglingEdges.get(i);
			String to = ((Transition)edge.getSemanticElement()).getTo();
			if (name != null && name.equals(to)) {
				list.add(edge);
				node.addArrivingEdge(edge);
			}
		}
		danglingEdges.removeAll(list);
	}
	
	protected void addLeavingEdges(Node node) {
		NodeElement nodeElement = (NodeElement)node.getSemanticElement();
		Transition[] transitions = nodeElement.getTransitions();
		for (int i = 0; i < transitions.length; i++) {
			PropertyChangeEvent evt = new PropertyChangeEvent(nodeElement, "transitionAdd", null, transitions[i]);
			node.propertyChange(evt);
		}
	}
	
}
