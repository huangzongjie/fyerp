package com.graly.mes.prd.designer.common.figure;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;

public class AbstractNodeFigure extends Figure {

	protected ConnectionAnchor connectionAnchor = null;

	public ConnectionAnchor getLeavingConnectionAnchor() {
		return connectionAnchor;
	}

	public ConnectionAnchor getArrivingConnectionAnchor() {
		return connectionAnchor;
	}

}
