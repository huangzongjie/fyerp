package com.graly.mes.prd.designer.common.command;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NotationElement;

public abstract class AbstractNodeCreateCommand extends Command {
	
	protected Node node;
	protected Point location;
	protected NotationElement parent;
	
	public void execute() {
		setConstraint();
	}
	
	protected void setConstraint() {
		if (location != null) {
			Dimension dimension = new Dimension (-1, -1);
			Rectangle rectangle = node.getConstraint();
			if (rectangle != null && rectangle.getSize() != null) {
				dimension = rectangle.getSize();
			}
			node.setConstraint(new Rectangle(location, dimension));
		}		
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public void setParent(NotationElement parent) {
		this.parent = parent;
	}
	
}
