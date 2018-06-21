package com.graly.mes.prd.designer.model;


public class ProcedureState extends AbstractNode {
	
	private SubProcedure procedure;
	
	public void setProcedure(SubProcedure newProcedure) {
		SubProcedure oldProcedure = procedure;
		procedure = newProcedure;
		firePropertyChange("procedure", oldProcedure, newProcedure);
	}
	
	public SubProcedure getProcedure() {
		if (procedure == null) {
			procedure = (SubProcedure)getFactory().createById("SubProcedure");
		}
		return procedure;
	}
	
}
