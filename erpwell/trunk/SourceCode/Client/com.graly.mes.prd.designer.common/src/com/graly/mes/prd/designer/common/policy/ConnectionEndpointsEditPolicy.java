package com.graly.mes.prd.designer.common.policy;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.GraphicalEditPart;

public class ConnectionEndpointsEditPolicy extends org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy {
	
	protected void addSelectionHandles(){
		super.addSelectionHandles();
		getConnectionFigure().setLineWidth(2);
	}

	protected PolylineConnection getConnectionFigure(){
		return (PolylineConnection)((GraphicalEditPart)getHost()).getFigure();
	}

	protected void removeSelectionHandles(){
		super.removeSelectionHandles();
		getConnectionFigure().setLineWidth(1);
	}	

}
