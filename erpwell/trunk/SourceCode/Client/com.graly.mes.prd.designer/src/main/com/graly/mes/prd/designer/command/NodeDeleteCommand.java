package com.graly.mes.prd.designer.command;

import com.graly.mes.prd.designer.common.command.AbstractEdgeDeleteCommand;
import com.graly.mes.prd.designer.common.command.AbstractNodeDeleteCommand;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.NodeElementContainer;
import com.graly.mes.prd.designer.model.ProcessDefinition;
import com.graly.mes.prd.designer.model.StartState;


public class NodeDeleteCommand extends AbstractNodeDeleteCommand {

	protected void doAdd() {
		SemanticElement toAdd = node.getSemanticElement();
		NodeElementContainer nodeElementContainer = (NodeElementContainer)parent.getSemanticElement();
		if (toAdd instanceof StartState) {
			((ProcessDefinition)nodeElementContainer).addStartState((StartState)toAdd);
		} else {
			nodeElementContainer.addNodeElement((NodeElement)toAdd);
		}
	}
	
	protected void doRemove() {
		SemanticElement toRemove = node.getSemanticElement();
		NodeElementContainer nodeElementContainer = (NodeElementContainer)parent.getSemanticElement();
		if (toRemove instanceof StartState) {
			((ProcessDefinition)nodeElementContainer).removeStartState((StartState)toRemove);
		} else {
			nodeElementContainer.removeNodeElement((NodeElement)toRemove);
		}
	}

	protected AbstractEdgeDeleteCommand createEdgeDeleteCommand() {
		return new EdgeDeleteCommand();
	}
		
}
