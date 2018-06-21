package com.graly.mes.prd.designer.common.command;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.notation.Label;

public class LabelMoveCommand extends Command {

	Label label = null;
	Point location = null;
	IFigure parent = null;
	Point oldOffset = new Point();

	public LabelMoveCommand(Label label, IFigure parent,
			Point location) {
		this.label = label;
		this.parent = parent;
		this.location = location;
	}

	public void execute() {
		oldOffset = label.getOffset();
		Point newOffset = label.getOffset().getCopy();
		parent.translateToAbsolute(newOffset);
		newOffset.translate(location);
		parent.translateToRelative(newOffset);
		label.setOffset(newOffset);
	}

	public void redo() {
		execute();
	}

	public void undo() {
		label.setOffset(oldOffset);
	}
}