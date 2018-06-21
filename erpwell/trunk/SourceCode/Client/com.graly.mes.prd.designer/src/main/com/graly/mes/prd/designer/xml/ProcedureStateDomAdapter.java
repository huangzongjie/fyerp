package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.model.Transition;
import com.graly.mes.prd.designer.model.Description;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.model.ProcedureState;
import com.graly.mes.prd.designer.model.SubProcedure;

public class ProcedureStateDomAdapter extends XmlAdapter {

	private static HashMap NODE_TYPES = null;	
	private static String[] CHILD_ELEMENTS = {"procedure", "transition"};
	
	protected String[] getChildElements() {
		return CHILD_ELEMENTS;
	}
	
	public Map getNodeTypes() {
		if (NODE_TYPES == null) {
			NODE_TYPES = new HashMap();
			NODE_TYPES.put("procedure", "procedure");
			NODE_TYPES.put("transition", "transition");
		}
		return NODE_TYPES;
	}
	
	protected void initialize() {
		super.initialize();
		ProcedureState procedureState = (ProcedureState)getSemanticElement();
		if (procedureState != null) {
			setAttribute("name", procedureState.getName());
			addElement(procedureState.getProcedure());
			addElements(procedureState.getTransitions());
		}
	}

	public void initialize(SemanticElement semanticElement) {
		super.initialize(semanticElement);
		ProcedureState procedureState = (ProcedureState)semanticElement;
		procedureState.setName(getAttribute("name"));
		procedureState.addPropertyChangeListener(this);
	}
	
	@Override
	protected void doModelAdd(XmlAdapter child) {
		String type = child.getElementType();
		SemanticElement jpdlElement = createSemanticElementFor(child);
		child.initialize(jpdlElement);
		ProcedureState procedureState = (ProcedureState)getSemanticElement();
		if ("transition".equals(type)) {
			procedureState.addTransition((Transition)jpdlElement);
		} else if ("procedure".equals(type)) {
			SubProcedure subPorcedure = (SubProcedure)jpdlElement;
			procedureState.setProcedure(subPorcedure);
		}
	}

	@Override
	protected void doModelRemove(XmlAdapter child) {
		String type = child.getElementType();
		ProcedureState procedureState = (ProcedureState)getSemanticElement();
		if ("transition".equals(type)) {
			procedureState.removeTransition((Transition)child.getSemanticElement());
		} else if ("procedure".equals(type)) {
			procedureState.setProcedure(null);
		}
	}

	@Override
	protected void doModelUpdate(String name, String newValue) {
		ProcedureState procedureState = (ProcedureState)getSemanticElement();
		if ("name".equals(name)) {
			procedureState.setName(newValue);
		}
	}
	
	@Override
	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("transitionAdd".equals(evt.getPropertyName())) {
			addElement((Transition)evt.getNewValue());
		} else if ("transitionRemove".equals(evt.getPropertyName())) {
			removeElement((Transition)evt.getOldValue());
		} else if ("procedure".equals(evt.getPropertyName())) {
			setElement("procedure", (SemanticElement)evt.getOldValue(), (Description)evt.getNewValue());
		} else if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		}
	}

}
