package com.graly.mes.prd.designer.common.figure;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.PointList;

public class SelfReferencingEdgeFigure extends PolylineConnection {
	
	public SelfReferencingEdgeFigure() {
		createConnection();
//		createDecoration();
	}
	
	public void setPoints(PointList points) {
		super.setPoints(points);
	}
	
	private void createConnection() {
		PointList points = new PointList(4);
		points.addPoint(200, 100);
		points.addPoint(250, 150);
		points.addPoint(250, 50);
		points.addPoint(0, 0);
		setPoints(points);
	}

//	private void createDecoration() {
//		PolygonDecoration arrow = new PolygonDecoration();
//		arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
//		arrow.setScale(5, 2.5);
//		setTargetDecoration(arrow);
//	}
	
}
