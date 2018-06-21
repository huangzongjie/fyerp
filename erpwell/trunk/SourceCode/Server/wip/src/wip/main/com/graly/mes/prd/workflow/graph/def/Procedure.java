package com.graly.mes.prd.workflow.graph.def;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("R")
public class Procedure extends ProcessDefinition {	
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}