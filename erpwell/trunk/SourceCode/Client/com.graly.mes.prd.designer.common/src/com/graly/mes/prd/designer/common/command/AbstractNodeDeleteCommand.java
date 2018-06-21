package com.graly.mes.prd.designer.common.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.Logger;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NodeContainer;

public abstract class AbstractNodeDeleteCommand extends Command {

	protected Node node;
	protected NodeContainer parent;
	private ArrayList<Command> edgeDeleteCommands;
	private ArrayList<Command> nodeDeleteCommands;

	public void setNode(Node node) {
		this.node = node;
		this.parent = node.getContainer();
	}
	
	public void execute() {		
		if (edgeDeleteCommands == null) {
			constructEdgeDeleteCommands();
		}
		if (nodeDeleteCommands == null) {
			constructNodeDeleteCommands();
		}
		executeCommands(edgeDeleteCommands);
		executeCommands(nodeDeleteCommands);
		doRemove();
	}
	
	public void undo() {
		doAdd();
		undoCommands(nodeDeleteCommands);
		undoCommands(edgeDeleteCommands);
	}
	
	private void constructNodeDeleteCommands() {
		nodeDeleteCommands = new ArrayList<Command>();
		if (node instanceof NodeContainer) {
			List<Node> nodes = ((NodeContainer)node).getNodes(); 
			for (int i = 0; i < nodes.size(); i++) {
				try {
					AbstractNodeDeleteCommand command = (AbstractNodeDeleteCommand)this.getClass().newInstance();
					command.setNode((Node)nodes.get(i));
					nodeDeleteCommands.add(command);
				} catch (IllegalAccessException e) {
					Logger.logError("problem while creating NodeDeleteCommand", e);
				} catch (InstantiationException e) {
					Logger.logError("problem while creating NodeDeleteCommand", e);
				}
			}
		}
	}
	
	private void addEdgeDeleteCommand(Edge edge) {
		AbstractEdgeDeleteCommand command = createEdgeDeleteCommand();
		command.setEdge(edge);
		edgeDeleteCommands.add(command);
	}
	
	private void addEdgeDeleteCommands(List<Edge> list) {
		for (int i = 0; i < list.size(); i++) {
			addEdgeDeleteCommand((Edge)list.get(i));
		}
	}
	
	private void constructEdgeDeleteCommands() {
		edgeDeleteCommands = new ArrayList<Command>();
		addEdgeDeleteCommands(node.getArrivingEdges());
		addEdgeDeleteCommands(node.getLeavingEdges());
	}
	
	private void executeCommands(List<Command> commands) {
		for (int i = 0; i < commands.size(); i++) {
			((Command)commands.get(i)).execute();
		}
	}
	
	private void undoCommands(List<Command> commands) {
		for (int i = commands.size(); i > 0; i--) {
			((Command)commands.get(i - 1)).undo();
		}
	}
	
	protected abstract AbstractEdgeDeleteCommand createEdgeDeleteCommand();
	protected abstract void doAdd();
	protected abstract void doRemove();
	
}
