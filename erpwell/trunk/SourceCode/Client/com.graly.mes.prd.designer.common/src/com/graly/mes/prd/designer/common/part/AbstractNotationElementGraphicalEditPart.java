package com.graly.mes.prd.designer.common.part;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.notation.AbstractNotationElement;
import com.graly.mes.prd.designer.common.notation.NotationElement;

public abstract class AbstractNotationElementGraphicalEditPart 
	extends AbstractGraphicalEditPart 
	implements NotationElementGraphicalEditPart {
	
	public AbstractNotationElementGraphicalEditPart(NotationElement notationElement) {
		setModel(notationElement);
	}
	
	protected void createEditPolicies() {
	}
	
	protected SemanticElement getSemanticElement() {
		return (SemanticElement)getNotationElement().getSemanticElement();
	}
	
	public void activate() {
		if (!isActive()) {
			getNotationElement().addPropertyChangeListener(this);
			super.activate();
		}
	}
	
	public void deactivate() {
		if (isActive()) {
			getNotationElement().removePropertyChangeListener(this);
			super.deactivate();
		}
	}
			
	public AbstractNotationElement getNotationElement() {
		return (AbstractNotationElement)getModel();
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		return false;
	}
}
