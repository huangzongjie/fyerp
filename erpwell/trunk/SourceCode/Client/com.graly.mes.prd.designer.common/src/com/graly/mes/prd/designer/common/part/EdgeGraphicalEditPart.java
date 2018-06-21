package com.graly.mes.prd.designer.common.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import com.graly.mes.prd.designer.common.figure.EdgeFigure;
import com.graly.mes.prd.designer.common.notation.BendPoint;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Label;
import com.graly.mes.prd.designer.common.notation.AbstractNotationElement;
import com.graly.mes.prd.designer.common.notation.NotationElement;
import com.graly.mes.prd.designer.common.policy.BendpointEditPolicy;
import com.graly.mes.prd.designer.common.policy.ConnectionEndpointsEditPolicy;
import com.graly.mes.prd.designer.common.policy.ConnectionEditPolicy;

public abstract class EdgeGraphicalEditPart 
	extends AbstractConnectionEditPart
	implements NotationElementGraphicalEditPart {
	
	public EdgeGraphicalEditPart(NotationElement notationElement) {
		setModel(notationElement);
	}
	
	private Edge getEdge() {
		return (Edge)getModel();
	}
	
	protected IFigure createFigure() {
		EdgeFigure result = new EdgeFigure();
		result.setRoutingConstraint(constructFigureBendpointList(result));
		return result;
	}
	
	private List constructFigureBendpointList(EdgeFigure f) {
		ArrayList result = new ArrayList();
		List modelBendpoints = getEdge().getBendPoints();
		for (int i = 0; i < modelBendpoints.size(); i++) {
			BendPoint bendpoint = (BendPoint)modelBendpoints.get(i);
			RelativeBendpoint figureBendpoint = new RelativeBendpoint(f);
			figureBendpoint.setRelativeDimensions(
					bendpoint.getFirstRelativeDimension(), 
					bendpoint.getSecondRelativeDimension());
			figureBendpoint.setWeight((i + 1) / (modelBendpoints.size() + 1));
			result.add(figureBendpoint);
		}
		return result;
	}
	
	private void refreshBendpoints() {
		EdgeFigure f = (EdgeFigure)getFigure();
		f.setRoutingConstraint(constructFigureBendpointList(f));
	}
	
	protected abstract ConnectionEditPolicy getConnectionEditPolicy();
	
	protected void createEditPolicies() {
		installEditPolicy("ConnectionEditPolicy", getConnectionEditPolicy());
		installEditPolicy("Connection Endpoint Policy", new ConnectionEndpointsEditPolicy());
		installEditPolicy("Connection Bendpoint Policy", new BendpointEditPolicy());
	}

	public void activate() {
		if (!isActive()) {
			getEdge().addPropertyChangeListener(this);
			super.activate();
		}
	}
	
	public void deactivate() {
		if (isActive()) {
			getEdge().removePropertyChangeListener(this);
			super.deactivate();
		}
	}
	
	protected List getModelChildren() {
		ArrayList result = new ArrayList();
		Label label = getEdge().getLabel();
		if (label != null) {
			result.add(label);
		}
		return result;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if ("bendPointAdd".equals(propertyName) 
				|| "bendPointSet".equals(propertyName) 
				|| "bendPointRemove".equals(propertyName)) {
			refreshBendpoints();
		} else if ("target".equals(propertyName)) {
			setTarget((EditPart)getViewer().getEditPartRegistry().get(evt.getNewValue()));
			refresh();
		}
	}
	

	public AbstractNotationElement getNotationElement() {
		return (AbstractNotationElement)getModel();
	}

	public boolean testAttribute(Object target, String name, String value) {
		return false;
	}
	
	
}
