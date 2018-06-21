package com.graly.mes.prd.designer.common.figure;

import java.util.ArrayList;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

public class EdgeFigure extends PolylineConnection {
	
	private static final Font FONT = new Font(null, "Arial", 9, SWT.NORMAL);
	private Label label;
	
	public EdgeFigure() {
		setTargetDecoration();
//		setConnectionRouter(new ManhattanConnectionRouter());
		setConnectionRouter(new BendpointConnectionRouter());
		setRoutingConstraint(new ArrayList());
		addLabel();
	}

	private void setTargetDecoration() {
		PolygonDecoration arrow = new PolygonDecoration();
		arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		arrow.setBackgroundColor(ColorConstants.white);
		arrow.setForegroundColor(ColorConstants.lightGray);
		arrow.setOpaque(true);
		arrow.setScale(10, 5);
		setTargetDecoration(arrow);
	}

	private void addLabel() {
		ConnectionEndpointLocator relationshipLocator = new ConnectionEndpointLocator(this, false);
		relationshipLocator.setUDistance(10);
		relationshipLocator.setVDistance(-10);
		label = new Label();
		label.setForegroundColor(ColorConstants.darkGray);
		label.setFont(FONT);
		add(label, relationshipLocator);
		label.setVisible(false);
	}
	
	public void paintFigure(Graphics g) {
		g.setForegroundColor(ColorConstants.lightGray);
		super.paintFigure(g);
	}
	
	public Label getLabel() {
		return label;
	}
	
}
