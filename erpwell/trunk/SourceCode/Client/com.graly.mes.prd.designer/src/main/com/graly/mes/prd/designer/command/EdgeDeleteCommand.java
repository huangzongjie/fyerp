package com.graly.mes.prd.designer.command;

import com.graly.mes.prd.designer.common.command.AbstractEdgeDeleteCommand;
import com.graly.mes.prd.designer.model.AbstractNode;
import com.graly.mes.prd.designer.model.Transition;

public class EdgeDeleteCommand extends AbstractEdgeDeleteCommand {
	
	public void execute() {
		((AbstractNode)source.getSemanticElement()).removeTransition((Transition)edge.getSemanticElement());
	}
	
	public void undo() {
		((AbstractNode)source.getSemanticElement()).addTransition((Transition)edge.getSemanticElement());
	}
	
}
