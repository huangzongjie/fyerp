package com.graly.mes.prd.designer.common.command;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.notation.BendPoint;
import com.graly.mes.prd.designer.common.notation.Edge;

public abstract class AbstractBendpointCommand extends Command {
	
	protected Edge edge;
	protected int index;
	protected BendPoint bendpoint;
	
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setRelativeDimensions(Dimension dim1, Dimension dim2) {
		bendpoint = new BendPoint();
		bendpoint.setRelativeDimensions(dim1, dim2);
	}
	
}
