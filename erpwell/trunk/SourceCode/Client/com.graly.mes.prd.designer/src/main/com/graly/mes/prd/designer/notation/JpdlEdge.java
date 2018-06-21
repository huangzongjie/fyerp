package com.graly.mes.prd.designer.notation;

import java.beans.PropertyChangeEvent;

import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NodeContainer;
import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.ProcessDefinition;

public class JpdlEdge extends Edge {
	
	private RootContainer getRootContainer() {
		Node source = getSource();
		if (source == null) return null;
		NodeContainer container = source.getContainer();
		while (container != null && container instanceof Node) {
			container = ((Node)container).getContainer();
		}
		return (RootContainer)container;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventName = evt.getPropertyName();
		if (eventName.equals("to")) {
			if (getSource() == null)
				return;
			RootContainer rootContainer = getRootContainer();
			if (rootContainer == null) return;
			ProcessDefinition processDefinition = (ProcessDefinition)rootContainer.getSemanticElement();
			if (processDefinition == null) return;
			NodeElement newTarget = processDefinition
					.getNodeElementByName((String) evt.getNewValue());
			NodeElement oldTarget = processDefinition
					.getNodeElementByName((String) evt.getOldValue());
			if (oldTarget != null) {
				Node oldTargetNode = (Node) getRegisteredNotationElementFor(oldTarget);
				if (oldTargetNode != null) {
					oldTargetNode.removeArrivingEdge(this);
				}
			}
			if (newTarget != null) {
				Node targetNode = (Node) getRegisteredNotationElementFor(newTarget);
				if (targetNode != null) {
					targetNode.addArrivingEdge(this);
				}
			}
			getSource().propertyChange(
					new PropertyChangeEvent(this, "leavingEdgeRefresh", null,
							null));
		} else {
			super.propertyChange(evt);
		}
	}

}
