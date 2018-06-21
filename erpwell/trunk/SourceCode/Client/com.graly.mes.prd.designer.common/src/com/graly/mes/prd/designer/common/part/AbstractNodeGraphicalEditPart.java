package com.graly.mes.prd.designer.common.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import com.graly.mes.prd.designer.common.figure.AbstractNodeFigure;
import com.graly.mes.prd.designer.common.figure.NodeFigureFactory;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NotationElement;
import com.graly.mes.prd.designer.common.policy.ComponentEditPolicy;
import com.graly.mes.prd.designer.common.policy.GraphicalNodeEditPolicy;

public abstract class AbstractNodeGraphicalEditPart 
	extends AbstractNotationElementGraphicalEditPart  
	implements NodeEditPart {
	
	public AbstractNodeGraphicalEditPart(NotationElement notationElement) {
		super(notationElement);
	}
	
	protected IFigure createFigure() {
		return NodeFigureFactory.INSTANCE.createFigure((Node)getNotationElement());
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventType = evt.getPropertyName();
		if ("constraint".equals(eventType) || "name".equals(eventType)) {
			refreshVisuals();
		} else if ("leavingEdgeAdd".equals(eventType) 
				|| "leavingEdgeRemove".equals(eventType)
				|| "leavingEdgeRefresh".equals(eventType)) {
			refreshSourceConnections();
		} else if ("arrivingEdgeAdd".equals(eventType)
				|| "arrivingEdgeRemove".equals(eventType)
				|| "arrivingEdgeRefresh".equals(eventType)) {
			refreshTargetConnections();
		} 
	}
	
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
		return getNodeFigure().getLeavingConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		return getNodeFigure().getArrivingConnectionAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		return getNodeFigure().getLeavingConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		return getNodeFigure().getArrivingConnectionAnchor();
	}

	protected List getModelSourceConnections() {
		return ((Node)getNotationElement()).getLeavingEdges();
	}
	
	protected List getModelTargetConnections() {
		return ((Node)getNotationElement()).getArrivingEdges();
	}
	
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy("ComponentEditPolicy", createComponentEditPolicy());
		installEditPolicy("GraphicalNodeEditPolciy", createGraphicalNodeEditPolicy());
	}
	
	protected abstract ComponentEditPolicy createComponentEditPolicy();
	protected abstract GraphicalNodeEditPolicy createGraphicalNodeEditPolicy();
	
	private AbstractNodeFigure getNodeFigure() {
		return (AbstractNodeFigure)getFigure();
	}

}
