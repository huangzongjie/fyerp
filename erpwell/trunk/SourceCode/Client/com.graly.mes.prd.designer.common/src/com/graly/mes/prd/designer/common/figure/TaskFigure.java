package com.graly.mes.prd.designer.common.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class TaskFigure extends Label {

	private boolean selected;
	private boolean hasFocus;
	
	public TaskFigure() {
		super();
		setLabelAlignment(PositionConstants.LEFT);
	}

	private Rectangle getSelectionRectangle() {
		Rectangle bounds = getTextBounds();
		bounds.expand(new Insets(2, 2, 0, 0));
		translateToParent(bounds);
		bounds.intersect(getBounds());
		return bounds;
	}

	protected void paintFigure(Graphics graphics) {
		if (selected) {
			graphics.pushState();
			graphics.setBackgroundColor(ColorConstants.menuBackgroundSelected);
			graphics.fillRectangle(getSelectionRectangle());
			graphics.popState();
			graphics.setForegroundColor(ColorConstants.white);
		}
		if (hasFocus) {
			graphics.pushState();
			graphics.setXORMode(true);
			graphics.setForegroundColor(ColorConstants.menuBackgroundSelected);
			graphics.setBackgroundColor(ColorConstants.white);
			graphics.drawFocus(getSelectionRectangle().resize(-1, -1));
			graphics.popState();
		}
		super.paintFigure(graphics);
	}

	public void setSelected(boolean b) {
		if (selected != b) {
			selected = b;
			repaint();
		}
	}

	public void setFocus(boolean b) {
		if (hasFocus != b) {
			hasFocus = b;
			repaint();
		}
	}

}
