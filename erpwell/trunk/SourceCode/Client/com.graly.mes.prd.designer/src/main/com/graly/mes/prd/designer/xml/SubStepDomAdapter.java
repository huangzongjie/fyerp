package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.model.SubStep;;

public class SubStepDomAdapter extends XmlAdapter {
	
	protected void initialize() {
		super.initialize();
		SubStep subStep = (SubStep)getSemanticElement();
		if (subStep != null) {
			setAttribute("name", subStep.getName());
			setAttribute("version", subStep.getVersion());
		}
	}
	
	public void initialize(SemanticElement jpdlElement) {
		super.initialize(jpdlElement);
		SubStep subStep = (SubStep)jpdlElement;
		subStep.setName(getAttribute("name"));
		subStep.setVersion(getAttribute("version"));
		subStep.addPropertyChangeListener(this);
	}
	
	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("step".equals(evt.getPropertyName())) {
			SubStep subStep = (SubStep)evt.getNewValue();
			setAttribute("name", subStep.getName());
			setAttribute("version", subStep.getVersion());
		} else if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		} else if ("version".equals(evt.getPropertyName())) {
			setAttribute("version", (String)evt.getNewValue());
		}
	}
	
	protected void doModelUpdate(String name, String newValue) {
		SubStep subStep = (SubStep)getSemanticElement();
		if ("name".equals(name)) {
			subStep.setName(newValue);
		} else if ("version".equals(name)) {
			subStep.setVersion(newValue);
		}
	}
	
	protected void doModelAdd(XmlAdapter child) {
		// a subprocess cannot have any child nodes
	}
	
	protected void doModelRemove(XmlAdapter child) {
		// a subprocess cannot have any child nodes
	}
}
