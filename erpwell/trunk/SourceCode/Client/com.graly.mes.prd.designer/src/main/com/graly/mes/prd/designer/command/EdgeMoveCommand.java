package com.graly.mes.prd.designer.command;

import com.graly.mes.prd.designer.common.command.AbstractEdgeMoveCommand;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.Transition;

public class EdgeMoveCommand extends AbstractEdgeMoveCommand {
	
	protected void doMoveSource(Node oldSource, Node newSource) {
		oldSource.removeLeavingEdge(getEdge());
		((NodeElement)oldSource.getSemanticElement()).removeTransition((Transition)getEdge().getSemanticElement());
		((NodeElement)newSource.getSemanticElement()).addTransition((Transition)getEdge().getSemanticElement());		
	}
	
	protected void doMoveTarget(Node target) {
		((Transition)getEdge().getSemanticElement()).setTo(((NodeElement)target.getSemanticElement()).getName());
	}
	
}
