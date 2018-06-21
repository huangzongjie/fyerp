package com.graly.mes.prd.designer.common.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToHelper;
import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.common.policy.XYLayoutEditPolicy;

public abstract class RootContainerGraphicalEditPart 
extends AbstractNotationElementGraphicalEditPart { 
	
	public RootContainerGraphicalEditPart(RootContainer rootContainer) {
		super(rootContainer);
	}
	
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		layer.setBorder(new LineBorder(1));
		return layer;
	}
	
	protected List getModelChildren() {
		return ((RootContainer)getNotationElement()).getNodes();
	}
	
	protected abstract XYLayoutEditPolicy createLayoutEditPolicy();
	
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy("LayoutEditPolicy", createLayoutEditPolicy());
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventType = evt.getPropertyName();
		if ("nodeAdd".equals(eventType) || "nodeRemove".equals(eventType)) {
			refreshChildren();
		}
	}

	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			return constructSnapToHelper();
		}
		return super.getAdapter(adapter);
	}

	private Object constructSnapToHelper() {
		Boolean val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
		if (val != null && val.booleanValue()) {
			return new SnapToGrid(this);
		} else {
			return null;
		}		
	}

}
