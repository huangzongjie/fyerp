package com.graly.mes.prd.designer.common.policy;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import com.graly.mes.prd.designer.common.command.LabelMoveCommand;
import com.graly.mes.prd.designer.common.notation.Label;

public class LabelGraphicalNodeEditPolicy extends NonResizableEditPolicy {
	
	public Command getMoveCommand(ChangeBoundsRequest request) {
	  Label model = (Label)getHost().getModel();
	  Point delta = request.getMoveDelta();
	  LabelMoveCommand command = new LabelMoveCommand(model,getParentFigure(),delta);
	  return command; 
	}
	
	public IFigure getParentFigure() {
		return ((GraphicalEditPart)getHost().getParent()).getFigure();
	}
}