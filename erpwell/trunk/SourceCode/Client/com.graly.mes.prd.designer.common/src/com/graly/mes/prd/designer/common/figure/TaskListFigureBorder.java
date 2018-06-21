package com.graly.mes.prd.designer.common.figure;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import com.graly.mes.prd.designer.common.Constants;

public class TaskListFigureBorder extends AbstractBorder {
	
	private static final Insets INSETS = new Insets(2,1,2,1);
	
	public Insets getInsets(IFigure figure) {
		return INSETS;
	}
	
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		graphics.setForegroundColor(ColorConstants.lightGray);
		Rectangle rect = getPaintRectangle(figure, insets);
		graphics.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
		graphics.setForegroundColor(Constants.veryLightGray);
		graphics.drawLine(rect.x, rect.y + 1, rect.x + rect.width - 1, rect.y + 1);
	}
	
}