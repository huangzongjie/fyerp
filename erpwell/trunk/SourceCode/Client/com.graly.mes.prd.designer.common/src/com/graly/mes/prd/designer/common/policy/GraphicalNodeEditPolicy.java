package com.graly.mes.prd.designer.common.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import com.graly.mes.prd.designer.common.command.AbstractEdgeCreateCommand;
import com.graly.mes.prd.designer.common.command.AbstractEdgeMoveCommand;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;

public abstract class GraphicalNodeEditPolicy extends org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy {
	
	protected abstract boolean canStart();
	protected abstract boolean canStop();
	protected abstract AbstractEdgeCreateCommand createEdgeCreateCommand();
	protected abstract AbstractEdgeMoveCommand createEdgeMoveCommand();

	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		if (canStop()) {
			AbstractEdgeCreateCommand command = (AbstractEdgeCreateCommand)request.getStartCommand();
			command.setTarget(getNode());
			return command;
		} else {
			return null;
		}
	}
	
	protected Node getNode() {
		return (Node)getHost().getModel();		
	}
	
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		if (canStart()) {
			AbstractEdgeCreateCommand command = createEdgeCreateCommand();
			command.setSource(getNode());
			command.setEdge((Edge)request.getNewObject());
			request.setStartCommand(command);
			return command;
		} else {
			return null;
		}
	}
	
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		if (request.getTarget() != null) {
			AbstractEdgeMoveCommand command = createEdgeMoveCommand();
			command.setEdge((Edge)request.getConnectionEditPart().getModel());
			command.setTarget((Node)request.getTarget().getModel());
			return command;
		}
		return null;
	}

	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		if (request.getTarget() != null) {
			AbstractEdgeMoveCommand command = createEdgeMoveCommand();
			command.setEdge((Edge)request.getConnectionEditPart().getModel());
			command.setSource((Node)request.getTarget().getModel());
			return command;
		}
		return null;
	}	
	
}
