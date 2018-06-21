package com.graly.mes.prd.designer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;

public class FlowEditorInput implements IEditorInput {

	private ProcessDefinition processDefinition;
	private String processName;

	public FlowEditorInput(String processName) {
		this.processName = processName;
	}

	public FlowEditorInput(String processName, ProcessDefinition processDefinition) {
		this.processName = processName;
		this.processDefinition = processDefinition;
	}

	public String getFlowDocument() {
		String text = getProcessDefinition().getFlowDocument();
		if (text == null) {
			text = "";
		}
		return text;
	}
	
	public String getFlowContent() {
		String text = getProcessDefinition().getFlowContent();
		if (text == null) {
			text = "";
		}
		return text;
	}
	
	public void setFlowDocument(String stream) {
		getProcessDefinition().setFlowDocument(stream);
	}
	
	public void setFlowContent(String stream) {
		getProcessDefinition().setFlowContent(stream);
	}
	
	@Override
	public boolean exists() {
		if (getProcessDefinition() != null) {
			return true;
		}
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return processName;
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

}
