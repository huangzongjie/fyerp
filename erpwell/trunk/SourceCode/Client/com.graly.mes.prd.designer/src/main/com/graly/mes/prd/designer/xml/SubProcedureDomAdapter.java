package com.graly.mes.prd.designer.xml;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.model.NodeElement;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.model.SubProcedure;;

public class SubProcedureDomAdapter extends XmlAdapter {
	
	protected void initialize() {
		super.initialize();
		SubProcedure subProcedure = (SubProcedure)getSemanticElement();
		if (subProcedure != null) {
			setAttribute("name", subProcedure.getName());
			setAttribute("version", subProcedure.getVersion());
		}
	}
	
	public void initialize(SemanticElement jpdlElement) {
		super.initialize(jpdlElement);
		SubProcedure subProcedure = (SubProcedure)jpdlElement;
		subProcedure.setName(getAttribute("name"));
		subProcedure.setVersion(getAttribute("version"));
		subProcedure.addPropertyChangeListener(this);
	}
	
	protected void doPropertyChange(PropertyChangeEvent evt) {
		if ("procedure".equals(evt.getPropertyName())) {
			SubProcedure subProcedure = (SubProcedure)evt.getNewValue();
			setAttribute("name", subProcedure.getName());
			setAttribute("version", subProcedure.getVersion());
		} else if ("name".equals(evt.getPropertyName())) {
			setAttribute("name", (String)evt.getNewValue());
		} else if ("version".equals(evt.getPropertyName())) {
			setAttribute("version", (String)evt.getNewValue());
		}
	}
	
	protected void doModelUpdate(String name, String newValue) {
		SubProcedure subProcedure = (SubProcedure)getSemanticElement();
		if ("name".equals(name)) {
			subProcedure.setName(newValue);
		} else if ("version".equals(name)) {
			subProcedure.setVersion(newValue);
		}
	}
	
	protected void doModelAdd(XmlAdapter child) {
		// a subprocess cannot have any child nodes
	}
	
	protected void doModelRemove(XmlAdapter child) {
		// a subprocess cannot have any child nodes
	}
}
