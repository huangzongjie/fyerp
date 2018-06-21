package com.graly.mes.prd.designer.common.policy;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import com.graly.mes.prd.designer.common.command.AbstractNodeCreateCommand;
import com.graly.mes.prd.designer.common.command.ChangeConstraintCommand;
import com.graly.mes.prd.designer.common.notation.AbstractNotationElement;
import com.graly.mes.prd.designer.common.notation.Node;

public abstract class XYLayoutEditPolicy extends org.eclipse.gef.editpolicies.XYLayoutEditPolicy {

	protected abstract AbstractNodeCreateCommand createNodeCreateCommand();

	protected Command getCreateCommand(CreateRequest request) {
		AbstractNodeCreateCommand createCommand = createNodeCreateCommand();
		initCreateCommand(createCommand, request);
		return createCommand;
	}

	protected Command createAddCommand(EditPart arg0, Object arg1) {
		return null;
	}

	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		ChangeConstraintCommand locationCommand = new ChangeConstraintCommand();
		locationCommand.setNode((Node)child.getModel());
		locationCommand.setNewConstraint((Rectangle)constraint);
		return locationCommand;
	}
	
	protected void initCreateCommand(AbstractNodeCreateCommand createCommand, CreateRequest request) {
		createCommand.setNode((Node)request.getNewObject());
		createCommand.setParent((AbstractNotationElement)getHost().getModel());
		Rectangle rectangle = (Rectangle)getConstraintFor(request);
		createCommand.setLocation(rectangle.getLocation());
		createCommand.setLabel("create a node");
	}

	protected Command getDeleteDependantCommand(Request arg0) {
		return null;
	}
	
	protected EditPolicy createchildEditPolicy(EditPart child) {
		return new NonResizableEditPolicy();
	}
	
}
