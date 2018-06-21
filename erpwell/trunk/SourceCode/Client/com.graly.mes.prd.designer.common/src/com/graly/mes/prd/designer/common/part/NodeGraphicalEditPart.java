package com.graly.mes.prd.designer.common.part;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;
import com.graly.mes.prd.designer.common.figure.NodeFigure;
import com.graly.mes.prd.designer.common.model.NamedElement;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NotationElement;
import com.graly.mes.prd.designer.common.policy.DirectEditPolicy;
import com.graly.mes.prd.designer.common.util.CellEditorLocator;

public abstract class NodeGraphicalEditPart 
	extends AbstractNodeGraphicalEditPart  
	implements NodeEditPart {
	
	private DirectEditManager manager;
	
	public NodeGraphicalEditPart(NotationElement notationElement) {
		super(notationElement);
	}
	
	protected void createEditPolicies() {
		super.createEditPolicies();
		//installEditPolicy("DirectEditPolicy", new DirectEditPolicy());
	}
	
	private String getSemanticElementLabel() {
		String result = ((NamedElement)getSemanticElement()).getName();
		if (result == null) {
			result = getSemanticElement().getNamePrefix();
		}
		return result;
	}
	
	public void refreshVisuals() {
		getNodeFigure().setName(getSemanticElementLabel());
		Rectangle constraint = null;
		if (((Node)getNotationElement()).getConstraint() != null) {
			constraint = new Rectangle(((Node)getNotationElement()).getConstraint());
		} else {
			constraint = new Rectangle(new Point(0, 0), new Dimension(-1, -1));			
		}
		((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), constraint);
	}
	
	private NodeFigure getNodeFigure() {
		return (NodeFigure)getFigure();
	}

	private void performDirectEdit() {
		if (getNodeFigure().getNameLabel() == null) return;
		if (manager == null) {
			initializeManager();
		}
		manager.show();
	}
	
	private void initializeManager() {
		CellEditorLocator locator = new CellEditorLocator(getNodeFigure().getNameLabel());
		manager = new DirectEditManager(this, TextCellEditor.class, locator) {
			protected void initCellEditor() {
				Text text = (Text) getCellEditor().getControl();
				String name = ((NamedElement)getSemanticElement()).getName();
				getCellEditor().setValue(name);
				text.selectAll();
			}			
		};
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			//do nothing
		} else {
			super.performRequest(request);
		}
	}

}
