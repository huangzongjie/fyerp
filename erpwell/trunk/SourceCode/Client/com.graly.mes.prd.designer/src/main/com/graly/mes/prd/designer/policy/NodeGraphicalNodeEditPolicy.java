package com.graly.mes.prd.designer.policy;

import java.util.ArrayList;
import java.util.List;

import com.graly.mes.prd.designer.common.command.AbstractEdgeCreateCommand;
import com.graly.mes.prd.designer.common.command.AbstractEdgeMoveCommand;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NotationElement;
import com.graly.mes.prd.designer.common.policy.GraphicalNodeEditPolicy;
import com.graly.mes.prd.designer.command.EdgeCreateCommand;
import com.graly.mes.prd.designer.command.EdgeMoveCommand;
import com.graly.mes.prd.designer.model.AbstractNode;
import com.graly.mes.prd.designer.model.EndState;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.ProcessDefinition;
import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.model.Transition;
import com.graly.mes.prd.designer.notation.JpdlNode;

public class NodeGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	protected boolean canStart() {
		SemanticElement semanticElement = ((NotationElement)getNode()).getSemanticElement();
		if (semanticElement instanceof EndState) {
			return false;
		} else if(semanticElement instanceof AbstractNode && 
				((AbstractNode)semanticElement).getTransitions().length >= 1){			
			return false;//如果该节点已经作为source存在过了，所以不能再作为source了	
		} else {
			return true;
		}
	}
	
	protected boolean canStop() {
		SemanticElement semanticElement = ((NotationElement)getNode()).getSemanticElement();
		NodeElement[] nodes = ((ProcessDefinition)((JpdlNode)getNode()).getContainer().getSemanticElement()).getNodeElements();
		if (semanticElement instanceof StartState) {
			return false;
		} else if(semanticElement instanceof AbstractNode){
			AbstractNode thisNode = (AbstractNode)semanticElement;
			for(NodeElement node : nodes){
				
				if(node instanceof AbstractNode){
					if(((AbstractNode)node).getTransitions().length >= 1){
					if(thisNode.getName().equals(((AbstractNode)node).getTransitions()[0].getTo()))
						return false;//如果有一个节点的target是这个节点,那么它不可以再作为target
					}
				}
			}
			return true;
		} else {
			return true;
		}
	}
	
	protected AbstractEdgeCreateCommand createEdgeCreateCommand() {
		return new EdgeCreateCommand();
	}

	protected AbstractEdgeMoveCommand createEdgeMoveCommand() {
		return new EdgeMoveCommand();
	}
}
