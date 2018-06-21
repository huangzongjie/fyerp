package com.graly.mes.prd.designer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.w3c.dom.Element;

import com.graly.mes.prd.designer.common.Logger;
import com.graly.mes.prd.designer.common.editor.AbstractContentProvider;
import com.graly.mes.prd.designer.common.model.NamedElement;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.notation.Edge;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NodeContainer;
import com.graly.mes.prd.designer.common.notation.NotationElement;
import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.NodeElementContainer;
import com.graly.mes.prd.designer.model.Transition;

public class DefaultContentProvider extends AbstractContentProvider{
	
	private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	private static String contentFile = "gpd.xml";
	
	@Override
	public boolean saveToInput(IEditorInput element, RootContainer rootContainer) {
		boolean result = true;
		try {
			FlowEditorInput input = (FlowEditorInput)element;
			input.setFlowContent(toNotationInfoXml(rootContainer));
		} catch (Exception e) {
			result = false; 
			Logger.logError("Problem while saving the input.", e);
		}
		return result;
	}	
	
	@Override
	public void addNotationInfo(RootContainer rootContainer, IEditorInput element) { 
		try {
			FlowEditorInput input = (FlowEditorInput)element;
			InputStream inputStream = new ByteArrayInputStream(input.getFlowContent().getBytes());
			if(inputStream.available() == 0) inputStream = null;
			if (inputStream == null) {
				inputStream = createInitialGpdInfo();
			}
			Element notationInfo = documentBuilderFactory.newDocumentBuilder().parse(inputStream).getDocumentElement();
			processRootContainer(rootContainer, notationInfo);
		} catch (Exception e) {
			Logger.logError("Problem adding notation info", e);
			throw new RuntimeException(e);
		}
	}
	
	private InputStream createInitialGpdInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("<root-container></root-container>");	
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}
	
	@Override
	protected void addEdges(Node node, Element notationInfo) {
		NodeElement nodeElement = (NodeElement)node.getSemanticElement();
		addEdges(node, nodeElement.getTransitions(), notationInfo);
	}

	@Override
	protected void addNodes(NodeContainer nodeContainer, Element notationInfo) {
		NodeElementContainer nodeElementContainer = (NodeElementContainer)nodeContainer.getSemanticElement();
		addNodes(nodeContainer, nodeElementContainer.getNodeElements(), notationInfo);
	}

	@Override
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

	@Override
	protected SemanticElement getEdgeSemanticElement(Node node,
			Element notationInfo, int index) {
		Transition[] transitions = ((NodeElement)node.getSemanticElement()).getTransitions();
		return index < transitions.length ? transitions[index] : null;
	}

	@Override
	protected SemanticElement getNodeSemanticElement(NodeContainer nodeContainer,
			Element notationInfo, int index) {
		NodeElementContainer nodeElementContainer = (NodeElementContainer)nodeContainer.getSemanticElement();
		return nodeElementContainer.getNodeElementByName(notationInfo.getAttribute("name"));
	}
}
