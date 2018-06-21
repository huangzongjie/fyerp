package com.graly.mes.prd.designer.common.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;
import com.graly.mes.prd.designer.common.command.AbstractEdgeDeleteCommand;
import com.graly.mes.prd.designer.common.notation.Edge;

public abstract class ConnectionEditPolicy extends org.eclipse.gef.editpolicies.ConnectionEditPolicy {
	
	protected abstract AbstractEdgeDeleteCommand createDeleteCommand();

	protected Command getDeleteCommand(GroupRequest arg0) {
		AbstractEdgeDeleteCommand command = createDeleteCommand();
		command.setEdge((Edge)getHost().getModel());
		return command;
	}

}
