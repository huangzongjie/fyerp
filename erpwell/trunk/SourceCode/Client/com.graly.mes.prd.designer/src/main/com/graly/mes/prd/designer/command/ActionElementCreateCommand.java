package com.graly.mes.prd.designer.command;

import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.model.SemanticElementFactory;
import com.graly.mes.prd.designer.model.ActionElement;
import com.graly.mes.prd.designer.model.ActionElementContainer;

public class ActionElementCreateCommand extends Command {
	
	private ActionElementContainer actionElementContainer;
	private ActionElement actionElement;
	private String actionId;
	private SemanticElementFactory factory;
	
	public ActionElementCreateCommand(SemanticElementFactory factory) {
		this.factory = factory;
	}
	
	public void execute() {
		if (actionElement == null) {
			actionElement = (ActionElement)factory.createById(actionId);
		}
		actionElementContainer.addActionElement(actionElement);
	}
	
	public void undo() {
		actionElementContainer.removeActionElement(actionElement);
	}
	
	public void setActionElementContainer(ActionElementContainer actionElementContainer) {
		this.actionElementContainer = actionElementContainer;
	}
	
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	
}
