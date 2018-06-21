package com.graly.mes.prd.designer.common.policy;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.BendpointRequest;
import com.graly.mes.prd.designer.common.command.AbstractBendpointCommand;
import com.graly.mes.prd.designer.common.command.BendpointCreateCommand;
import com.graly.mes.prd.designer.common.command.BendpointDeleteCommand;
import com.graly.mes.prd.designer.common.command.BendpointMoveCommand;
import com.graly.mes.prd.designer.common.notation.Edge;

public class BendpointEditPolicy extends org.eclipse.gef.editpolicies.BendpointEditPolicy {

	protected Command getCreateBendpointCommand(BendpointRequest request) {
		BendpointCreateCommand command = new BendpointCreateCommand();
		fillCommand(request, command);
		return command;
	}

	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		BendpointDeleteCommand command = new BendpointDeleteCommand();
		fillCommand(request, command);
		return command;
	}

	protected Command getMoveBendpointCommand(BendpointRequest request) {
		BendpointMoveCommand command = new BendpointMoveCommand();
		fillCommand(request, command);
		return command;
	}

	private void fillCommand(BendpointRequest request, AbstractBendpointCommand command) {
		Point p = request.getLocation();
		Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
		Point ref2 = getConnection().getTargetAnchor().getReferencePoint();
		command.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
		command.setEdge((Edge)request.getSource().getModel());
		command.setIndex(request.getIndex());
	}

}
