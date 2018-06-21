package com.graly.mes.prd.designer.common.figure;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

public class FixedAnchor extends AbstractConnectionAnchor {
	
	public FixedAnchor(IFigure owner) {
		super(owner);
	}
	
	public Point getLocation(Point reference) {
		return new Point(18, 0);
	}
	
}