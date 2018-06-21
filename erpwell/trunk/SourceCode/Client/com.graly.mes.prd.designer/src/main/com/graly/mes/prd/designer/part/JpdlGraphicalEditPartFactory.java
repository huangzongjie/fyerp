package com.graly.mes.prd.designer.part;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Display;

import com.graly.mes.prd.designer.common.command.AbstractEdgeDeleteCommand;
import com.graly.mes.prd.designer.common.command.AbstractNodeCreateCommand;
import com.graly.mes.prd.designer.common.command.AbstractNodeDeleteCommand;
import com.graly.mes.prd.designer.common.notation.AbstractNodeContainer;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Label;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NodeContainer;
import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.common.part.EdgeGraphicalEditPart;
import com.graly.mes.prd.designer.common.part.LabelGraphicalEditPart;
import com.graly.mes.prd.designer.common.part.NodeContainerGraphicalEditPart;
import com.graly.mes.prd.designer.common.part.NodeGraphicalEditPart;
import com.graly.mes.prd.designer.common.part.RootContainerGraphicalEditPart;
import com.graly.mes.prd.designer.common.policy.ComponentEditPolicy;
import com.graly.mes.prd.designer.common.policy.ConnectionEditPolicy;
import com.graly.mes.prd.designer.common.policy.GraphicalNodeEditPolicy;
import com.graly.mes.prd.designer.common.policy.XYLayoutEditPolicy;
import com.graly.mes.prd.designer.command.EdgeDeleteCommand;
import com.graly.mes.prd.designer.command.NodeCreateCommand;
import com.graly.mes.prd.designer.command.NodeDeleteCommand;
import com.graly.mes.prd.designer.dialog.PropertySetupDialog;
import com.graly.mes.prd.designer.model.EndState;
import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.policy.NodeGraphicalNodeEditPolicy;

public class JpdlGraphicalEditPartFactory implements EditPartFactory {
	public EditPart createEditPart(EditPart context, Object model) {
		if (model == null) return null;
		if (model instanceof RootContainer) {
			return createRootContainerGraphicalEditPart(model);
		} else if (model instanceof AbstractNodeContainer) {
			return createNodeContainerGraphicalEditPart(model);
		} else if (model instanceof Node){
			return createNodeGraphicalEditPart(model);
		} else if (model instanceof Edge) {
			return createEdgeGraphicalEditPart(model);
		} else if (model instanceof Label) {
			return createLabelGraphicalEditPart(model);
		}
		return null;
	}
	
	private EditPart createNodeContainerGraphicalEditPart(Object model) {
		return new NodeContainerGraphicalEditPart((NodeContainer)model) {
			protected XYLayoutEditPolicy createXYLayoutEditPolicy() {
				return getLayoutEditPolicy();
			}
			protected ComponentEditPolicy createComponentEditPolicy() {
				return getComponentEditPolicy();
			}
			protected GraphicalNodeEditPolicy createGraphicalNodeEditPolicy() {
				return new NodeGraphicalNodeEditPolicy();
			}
		};
	}
	
	private EditPart createNodeGraphicalEditPart(Object model) {
		return new NodeGraphicalEditPart((Node)model) {
			protected ComponentEditPolicy createComponentEditPolicy() {
				return getComponentEditPolicy();
			}
			protected GraphicalNodeEditPolicy createGraphicalNodeEditPolicy() {
				return new NodeGraphicalNodeEditPolicy();
			}			 
			public void performRequest(Request request) {
				if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
					//do nothing
				} else if (request.getType() == RequestConstants.REQ_OPEN) {
					Node model = (Node) this.getModel();
					if(! (model.getSemanticElement() instanceof StartState || model.getSemanticElement() instanceof EndState)){
						PropertySetupDialog pd = new PropertySetupDialog(Display.getCurrent().getActiveShell(), "Name", " ‰»ÎName", "", null, model);
						pd.open();
					}
				} else {
					super.performRequest(request);
				}
			}
		};
	}

	private EditPart createLabelGraphicalEditPart(Object model) {
		return new LabelGraphicalEditPart((Label)model);
	}

	private EditPart createRootContainerGraphicalEditPart(Object model) {
		return new RootContainerGraphicalEditPart((RootContainer)model) {
			protected XYLayoutEditPolicy createLayoutEditPolicy() {
				return getLayoutEditPolicy();
			}			
		};
	}

	private EditPart createEdgeGraphicalEditPart(Object model) {
		return new EdgeGraphicalEditPart((Edge)model) {
			protected ConnectionEditPolicy getConnectionEditPolicy() {
				return new ConnectionEditPolicy() {
					protected AbstractEdgeDeleteCommand createDeleteCommand() {
						return new EdgeDeleteCommand();
					}				
				};
			}			
		};
	}
	
	private XYLayoutEditPolicy getLayoutEditPolicy() {
		return new XYLayoutEditPolicy() {
			protected AbstractNodeCreateCommand createNodeCreateCommand() {
				return new NodeCreateCommand();
			}					
		};
	}

	private ComponentEditPolicy getComponentEditPolicy() {
		return new ComponentEditPolicy() {
			protected AbstractNodeDeleteCommand createDeleteCommand() {
				return new NodeDeleteCommand();
			}					
		};
	}
}
