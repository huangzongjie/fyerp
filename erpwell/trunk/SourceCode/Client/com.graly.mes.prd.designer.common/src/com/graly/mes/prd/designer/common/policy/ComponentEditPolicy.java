package com.graly.mes.prd.designer.common.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;
import com.graly.mes.prd.designer.common.command.AbstractNodeDeleteCommand;
import com.graly.mes.prd.designer.common.notation.Node;

public abstract class ComponentEditPolicy extends org.eclipse.gef.editpolicies.ComponentEditPolicy {
	
	protected abstract AbstractNodeDeleteCommand createDeleteCommand();
	
	protected Command createDeleteCommand(GroupRequest request) {
		AbstractNodeDeleteCommand nodeDeleteCommand = createDeleteCommand();
		nodeDeleteCommand.setNode((Node)getHost().getModel());
		return nodeDeleteCommand;
	}
	
}
