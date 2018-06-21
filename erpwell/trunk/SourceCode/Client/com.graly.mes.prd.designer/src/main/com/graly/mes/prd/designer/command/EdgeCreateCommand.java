package com.graly.mes.prd.designer.command;

import java.util.ArrayList;
import java.util.List;

import com.graly.mes.prd.designer.common.command.AbstractEdgeCreateCommand;
import com.graly.mes.prd.designer.common.model.NamedElement;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.common.notation.NotationElement;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.Transition;

public class EdgeCreateCommand extends AbstractEdgeCreateCommand {
	
	public void execute() {
		if(getTransitionSource().equals(getTransitionTarget())){
			return;//如果是自己连自己则不执行
		}
		super.execute();
		initializeTransitionAttributes();
		getTransition().setSource(getTransitionSource());
		getTransition().setTo(getTransitionTarget().getName());
		getTransitionSource().addTransition(getTransition());
	}
	

	public void undo() {
		getTransitionSource().removeTransition(getTransition());
		getTransition().setSource(null);
	}
	
	private void initializeTransitionAttributes() {
		initializeToAttribute();
		if (getTransitionSource().getTransitions().length > 0) {
			getTransition().setName("to " + getTransition().getTo());
			edge.getLabel().setText(getTransition().getName());
		}
	}
	
	private void initializeToAttribute() {
		List sourcePath = getPathToRootFrom(source);
		List targetPath = getPathToRootFrom(target);
		NotationElement common = findFirstCommonElement(sourcePath, targetPath);
		getTransition().setTo(getPrefix(sourcePath, common) + getSuffix(common, targetPath));
	}
	
	private List getPathToRootFrom(NotationElement notationElement) {
		List result = new ArrayList();
		while (notationElement != null) {
			result.add(notationElement);
			if (notationElement instanceof Node) {
				notationElement = (NotationElement)((Node)notationElement).getContainer();
			} else {
				notationElement = null;
			}
		}
		return result;
	}
	
	private NotationElement findFirstCommonElement(List sourcePath, List targetPath) {
		NotationElement result = null;
		int i = 1;
		while (i <= sourcePath.size() && i <= targetPath.size()) {
			if (sourcePath.get(sourcePath.size() - i) == targetPath.get(targetPath.size() - i)) {
				result = (NotationElement)sourcePath.get(sourcePath.size() - i);
			}
			i++;
		}
		return result;
	}
	
	private String getPrefix(List sourcePath, NotationElement common) {
		StringBuffer result = new StringBuffer("");
		if (source != target) {
			int i = 1;
			while (i < sourcePath.size() && sourcePath.get(i) != common) {
				result.append("../");
				i++;
			}
		}
		return result.toString();
	}
	
	private String getSuffix(NotationElement common, List targetPath) {
		StringBuffer result = new StringBuffer();
		if (source != target) {
			int i = 0;
			while (i < targetPath.size() && targetPath.get(i) != common) {
				result.insert(0, getSemanticElementName((NotationElement)targetPath.get(i++)));
				if (i < targetPath.size() && targetPath.get(i) != common) {
					result.insert(0, "/");
				}
			}
		} else {
			result.append(getSemanticElementName(common));
		}
		return result.toString();
	}
	
	private String getSemanticElementName(NotationElement notationElement) {
		return ((NamedElement)notationElement.getSemanticElement()).getName();
	}
	
	private NodeElement getTransitionSource() {
		return (NodeElement)source.getSemanticElement();
	}
	
	private NodeElement getTransitionTarget() {
		return (NodeElement)target.getSemanticElement();
	}
	
	private Transition getTransition() {
		return (Transition)edge.getSemanticElement();
	}	
}
