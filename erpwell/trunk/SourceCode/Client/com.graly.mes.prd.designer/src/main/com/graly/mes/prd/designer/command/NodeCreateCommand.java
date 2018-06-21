package com.graly.mes.prd.designer.command;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

import com.graly.framework.base.ui.util.Message;
import com.graly.mes.prd.designer.common.command.AbstractNodeCreateCommand;
import com.graly.mes.prd.designer.common.notation.NodeContainer;
import com.graly.mes.prd.designer.dialog.PropertySetupDialog;
import com.graly.mes.prd.designer.model.AbstractNode;
import com.graly.mes.prd.designer.model.EndState;
import com.graly.mes.prd.designer.model.NodeElementContainer;
import com.graly.mes.prd.designer.model.ProcedureState;
import com.graly.mes.prd.designer.model.ProcessDefinition;
import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.model.StepState;
import com.graly.mes.prd.designer.model.SubProcedure;

public class NodeCreateCommand extends AbstractNodeCreateCommand {
	private String DIALOG_TITLE = Message.getString("common.createnode_dialog_tile");
	private String DIALOG_INFO = Message.getString("common.createnode_dialog_info");
		
	public void execute() {
		super.execute();
		if(getAbstractNode() instanceof StartState 
				|| getAbstractNode() instanceof EndState){
		    setName();
            addAbstractNode(getNodeElementContainer(), getAbstractNode());
		} else {
			getAbstractNode().initializeName(getNodeElementContainer());
			String initName = getAbstractNode().getName();
			node.setContainer((NodeContainer) parent);
			PropertySetupDialog propertyDialog = new PropertySetupDialog(Display.getCurrent().getActiveShell(),
					DIALOG_TITLE, DIALOG_INFO, initName, null,node);
			int result = propertyDialog.open();
			if(result == Dialog.OK){
				String subProcedureName =null;
				if (getAbstractNode() instanceof ProcedureState){
					subProcedureName = ((ProcedureState)getAbstractNode()).getProcedure().getName();
				} else if(getAbstractNode() instanceof StepState){
					subProcedureName = ((StepState)getAbstractNode()).getStep().getName();
				}
				addAbstractNode(getNodeElementContainer(), getAbstractNode());
				if (subProcedureName != null){
					if (getAbstractNode() instanceof ProcedureState){
						setSubProcedure(subProcedureName);
					} else if(getAbstractNode() instanceof StepState){
						((StepState)getAbstractNode()).getStep().setName(subProcedureName);
					}
					
				}
			}
		}
	}
	
	public void addAbstractNode(NodeElementContainer nodeElementContainer, AbstractNode abstractNode) {
		if (abstractNode instanceof StartState && nodeElementContainer instanceof ProcessDefinition) {
			((ProcessDefinition)nodeElementContainer).addStartState((StartState)abstractNode);
		} else {
			nodeElementContainer.addNodeElement(abstractNode);
		}
	}
	
	private void removeAbstractNode(NodeElementContainer nodeElementContainer, AbstractNode abstractNode) {
		if (abstractNode instanceof StartState && nodeElementContainer instanceof ProcessDefinition) {
			((ProcessDefinition)nodeElementContainer).removeStartState((StartState)abstractNode);
		} else {
			nodeElementContainer.removeNodeElement(abstractNode);
		}
	}
	
	public NodeElementContainer getNodeElementContainer() {
		return (NodeElementContainer)parent.getSemanticElement();
	}
	
	public AbstractNode getAbstractNode() {
		return (AbstractNode)node.getSemanticElement();
	}
	protected void setAbstractNode(AbstractNode abNode){
		node.setSemanticElement(abNode);
	}
	
	public boolean canExecute() {
		return getNodeElementContainer().canAdd(getAbstractNode());
	}
	
	protected void setName() {
		if (getAbstractNode().getName() == null) {
			if(getAbstractNode() instanceof StartState){
				getAbstractNode().setName("START");
			} else if(getAbstractNode() instanceof EndState) {
				getAbstractNode().setName("END");
			} 
		}		
	}
	
	public void undo() {
		removeAbstractNode(getNodeElementContainer(), getAbstractNode());
	}
	
	public void setSubProcedure(String newName) {
		if (getAbstractNode() instanceof ProcedureState) {
			ProcedureState procedureState = (ProcedureState)getAbstractNode();
			SubProcedure subProcedure = procedureState.getProcedure();
			subProcedure.setName(newName);
		}		
	}
}
