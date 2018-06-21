package com.graly.mes.prd.designer.common.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.DirectEditRequest;
import com.graly.mes.prd.designer.common.command.ChangeNameCommand;
import com.graly.mes.prd.designer.common.model.NamedElement;
import com.graly.mes.prd.designer.common.notation.NotationElement;


public class DirectEditPolicy extends org.eclipse.gef.editpolicies.DirectEditPolicy {
	
	protected Command getDirectEditCommand(DirectEditRequest request) {
		String value = (String) request.getCellEditor().getValue();
		NamedElement namedElement = (NamedElement)((NotationElement)getHost().getModel()).getSemanticElement();
		ChangeNameCommand command = new ChangeNameCommand();
		command.setNamedElement(namedElement);
		command.setName(value);
		return command;
	}
	
	protected void showCurrentEditValue(DirectEditRequest request) {
	}

}
