package com.graly.mes.prd.designer;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

public class DeleteAction extends Action {
	
	private AbstractGraphicalViewer viewer;
	public DeleteAction(AbstractGraphicalViewer viewer) {
		this.viewer = viewer;
	}
	
	public void run() {
		execute(createDeleteCommand(getSelectedObjects()));
	}
	
	protected void execute(Command command) {
		if (command == null || !command.canExecute())
			return;
		viewer.getEditDomain().getCommandStack().execute(command);
	}
	
	public Command createDeleteCommand(List objects) {
		if (objects.isEmpty())
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;

		GroupRequest deleteReq = new GroupRequest(RequestConstants.REQ_DELETE);
		deleteReq.setEditParts(objects);

		CompoundCommand compoundCmd = new CompoundCommand(GEFMessages.DeleteAction_ActionDeleteCommandName);
		for (int i = 0; i < objects.size(); i++) {
			EditPart object = (EditPart) objects.get(i);
			Command cmd = object.getCommand(deleteReq);
			if (cmd != null) compoundCmd.add(cmd);
		}

		return compoundCmd;
	}

	protected List getSelectedObjects() {
		if (!(viewer.getSelection() instanceof IStructuredSelection))
			return Collections.EMPTY_LIST;
		return ((IStructuredSelection)viewer.getSelection()).toList();
	}
}
