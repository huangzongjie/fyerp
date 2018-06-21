package com.graly.mes.prd.designer.common.notation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.graly.mes.prd.designer.common.model.SemanticElement;

public interface NotationElement extends PropertyChangeListener {

	void setFactory(NotationElementFactory factory);
	NotationElementFactory getFactory();
	void setSemanticElement(SemanticElement semanticElement);
	SemanticElement getSemanticElement();
	void addPropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
	void propertyChange(PropertyChangeEvent evt);
	void register();
	void unregister();
	AbstractNotationElement getRegisteredNotationElementFor(SemanticElement semanticElement);
}
