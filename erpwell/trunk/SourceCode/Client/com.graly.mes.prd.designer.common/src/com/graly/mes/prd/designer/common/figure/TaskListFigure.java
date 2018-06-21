package com.graly.mes.prd.designer.common.figure;

import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;

public class TaskListFigure extends Figure {

	public TaskListFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(true);
		layout.setSpacing(2);
		setLayoutManager(layout);
		setBorder(new CompoundBorder(new TaskListFigureBorder(),
				new MarginBorder(1)));
	}

	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension size = super.getPreferredSize(wHint, hHint);
		size.height = Math.max(size.height, 10);
		return size;
	}

}
