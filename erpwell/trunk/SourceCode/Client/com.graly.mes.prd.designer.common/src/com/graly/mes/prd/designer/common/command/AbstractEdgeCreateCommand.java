package com.graly.mes.prd.designer.common.command;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.notation.BendPoint;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;

public abstract class AbstractEdgeCreateCommand extends Command {
	
	protected Node source;
	protected Node target;
	protected Edge edge;
	
	public void execute() {
		if (source == target && edge.getBendPoints().isEmpty()) {
			addBendPoints();
		}
	}
	
	private void addBendPoints() {
		Rectangle constraint = source.getConstraint();
		int horizontal = - (constraint.width / 2 + 25);
		int vertical = horizontal * constraint.height / constraint.width;
		BendPoint first = new BendPoint();
		first.setRelativeDimensions(new Dimension(horizontal, 0), new Dimension(horizontal, 0));
		BendPoint second = new BendPoint();
		second.setRelativeDimensions(new Dimension(horizontal, vertical), new Dimension(horizontal, vertical));
		edge.addBendPoint(first);
		edge.addBendPoint(second);
	}
	
	public boolean canExecute() {
		if (source == null || target == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public void setSource(Node newSource) {
		source = newSource;
	}
	
	public void setEdge(Edge newEdge) {
		edge = newEdge;
	}
	
	public void setTarget(Node newTarget) {
		target = newTarget;
	}
	
}
