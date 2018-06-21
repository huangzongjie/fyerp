package com.graly.mes.prd.designer.common.part;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.IActionFilter;
import com.graly.mes.prd.designer.common.notation.AbstractNotationElement;

public interface NotationElementGraphicalEditPart  
extends EditPart, PropertyChangeListener, IActionFilter {
	
	public AbstractNotationElement getNotationElement();

}
