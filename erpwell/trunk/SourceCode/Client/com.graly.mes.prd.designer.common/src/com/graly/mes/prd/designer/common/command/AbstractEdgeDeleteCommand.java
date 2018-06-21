package com.graly.mes.prd.designer.common.command;

import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;

public class AbstractEdgeDeleteCommand extends Command {
	
	protected Edge edge;
	protected Node source;
	
	public void setEdge(Edge edge) {
		this.edge = edge;
		this.source = edge.getSource();
	}
	
}
