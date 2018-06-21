package com.graly.mes.prd.designer.editor;

import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.w3c.dom.Element;

import com.graly.mes.prd.designer.common.editor.AbstractContentProvider;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NodeContainer;
import com.graly.mes.prd.designer.common.notation.NotationElement;
import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.NodeElementContainer;
import com.graly.mes.prd.designer.model.Transition;

public class JpdlContentProvider extends AbstractContentProvider{
	
	protected String getNotationInfoFileName(String semanticInfoFileName) {
		if ("processdefinition.xml".equals(semanticInfoFileName)) {
			return "gpd.xml";
		} else {
			return super.getNotationInfoFileName(semanticInfoFileName);
		}
	}

	protected String getSemanticInfoFileName(String notationInfoFileName) {
		if ("gpd.xml".equals(notationInfoFileName)) {
			return "processdefinition.xml";
		} else {
			return super.getSemanticInfoFileName(notationInfoFileName);
		}
	}

	protected void addNodes(NodeContainer nodeContainer, Element notationInfo) {
		NodeElementContainer nodeElementContainer = (NodeElementContainer)nodeContainer.getSemanticElement();
		addNodes(nodeContainer, nodeElementContainer.getNodeElements(), notationInfo);
	}
	
	protected void addEdges(Node node, Element notationInfo) {
		NodeElement nodeElement = (NodeElement)node.getSemanticElement();
		addEdges(node, nodeElement.getTransitions(), notationInfo);
	}
	
	protected SemanticElement getEdgeSemanticElement(Node node, Element notationInfo, int index) {
		Transition[] transitions = ((NodeElement)node.getSemanticElement()).getTransitions();
		return index < transitions.length ? transitions[index] : null;
	}
	
	protected SemanticElement getNodeSemanticElement(NodeContainer nodeContainer, Element notationInfo, int index) {
		NodeElementContainer nodeElementContainer = (NodeElementContainer)nodeContainer.getSemanticElement();
		return nodeElementContainer.getNodeElementByName(notationInfo.getAttribute("name"));
	}
	
	protected SemanticElement findDestination(Edge edge, Node source) {
		NotationElement notationElement = source.getContainer();
		String pathCopy = ((Transition)edge.getSemanticElement()).getTo();
		while (pathCopy.length() > 3 && "../".equals(pathCopy.substring(0, 3)) && notationElement != null) {
			notationElement = ((Node)notationElement).getContainer();
			pathCopy = pathCopy.substring(3);
		}
		if (notationElement == null) return null;
		SemanticElement parent = (SemanticElement)notationElement.getSemanticElement();
		StringTokenizer tokenizer = new StringTokenizer(pathCopy, "/");
		while (parent != null && tokenizer.hasMoreTokens()) {
			if (!(parent instanceof NodeElementContainer)) return null;
			parent = ((NodeElementContainer)parent).getNodeElementByName(tokenizer.nextToken()); 
		}
		return (NodeElement)parent;
	}
	
	public boolean saveToInput(IEditorInput input, RootContainer rootContainer){
		return true;
	}
	
	public void addNotationInfo(RootContainer rootContainer, IEditorInput input){
		
	}
	
	
}
