package com.graly.mes.prd.designer.common.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.AutoexposeHelper;
import org.eclipse.gef.ExposeHelper;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.MouseWheelHelper;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.editparts.ViewportAutoexposeHelper;
import org.eclipse.gef.editparts.ViewportExposeHelper;
import org.eclipse.gef.editparts.ViewportMouseWheelHelper;
import com.graly.mes.prd.designer.common.figure.NodeContainerFigure;
import com.graly.mes.prd.designer.common.figure.NodeFigureFactory;
import com.graly.mes.prd.designer.common.notation.AbstractNodeContainer;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NotationElement;
import com.graly.mes.prd.designer.common.policy.ContainerHighlightEditPolicy;
import com.graly.mes.prd.designer.common.policy.XYLayoutEditPolicy;

public abstract class NodeContainerGraphicalEditPart 
	extends AbstractNodeGraphicalEditPart  
	implements NodeEditPart {
	
	public NodeContainerGraphicalEditPart(NotationElement notationElement) {
		super(notationElement);
	}
	
	protected IFigure createFigure() {
		return NodeFigureFactory.INSTANCE.createFigure((AbstractNodeContainer)getNotationElement());
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventType = evt.getPropertyName();
		if ("constraint".equals(eventType) || "name".equals(eventType)) {
			refreshVisuals();
		} else if ("leavingEdgeAdd".equals(eventType) 
				|| "leavingEdgeRemove".equals(eventType)) {
			refreshSourceConnections();
		} else if ("arrivingEdgeAdd".equals(eventType)
				|| "arrivingEdgeRemove".equals(eventType)) {
			refreshTargetConnections();
		} else if ("nodeAdd".equals(eventType)
				|| "nodeRemove".equals(eventType)){
			refreshChildren();
		}
	}
	
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy("LayoutEditPolicy", createXYLayoutEditPolicy());
		installEditPolicy("Selection Feedback", new ContainerHighlightEditPolicy());
	}
	
	public void refreshVisuals() {
		Rectangle constraint = null;
		if (((Node)getNotationElement()).getConstraint() != null) {
			constraint = new Rectangle(((Node)getNotationElement()).getConstraint());
		} else {
			constraint = new Rectangle(new Point(0, 0), new Dimension(-1, -1));			
		}
		((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), constraint);
	}
	
	protected List getModelChildren() {
		return ((AbstractNodeContainer)getNotationElement()).getNodes();
	}
	
	public Object getAdapter(Class key) {
		if (key == AutoexposeHelper.class)
			return new ViewportAutoexposeHelper(this);
		if (key == ExposeHelper.class)
			return new ViewportExposeHelper(this);
		if (key == MouseWheelHelper.class)
			return new ViewportMouseWheelHelper(this);
		return super.getAdapter(key);
	}

	public IFigure getContentPane() {
		return ((NodeContainerFigure)getFigure()).getContentPane();
	}
	
	protected abstract XYLayoutEditPolicy createXYLayoutEditPolicy();

}
