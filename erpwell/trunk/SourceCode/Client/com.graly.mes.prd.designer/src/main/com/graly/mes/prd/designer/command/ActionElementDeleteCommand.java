package com.graly.mes.prd.designer.command;

import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.model.ActionElement;
import com.graly.mes.prd.designer.model.ActionElementContainer;

public class ActionElementDeleteCommand extends Command {
	
	private ActionElementContainer actionElementContainer;
	private ActionElement actionElement;
	
	public void execute() {
		actionElementContainer.removeActionElement(actionElement);
	}
	
	public void undo() {
		actionElementContainer.addActionElement(actionElement);
	}
	
	public void setActionElementContainer(ActionElementContainer actionElementContainer) {
		this.actionElementContainer = actionElementContainer;
	}
	
	public void setActionElement(ActionElement actionElement) {
		this.actionElement = actionElement;
	}
	
}
