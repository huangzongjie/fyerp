package com.graly.mes.prd.workflow.context.def;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.prd.workflow.context.exe.ContextInstance;
import com.graly.mes.prd.workflow.module.def.ModuleDefinition;
import com.graly.mes.prd.workflow.module.exe.ModuleInstance;

@Entity
@DiscriminatorValue("C")
public class ContextDefinition extends ModuleDefinition {

	private static final long serialVersionUID = 1L;

	public ContextDefinition() {
	}

	public ModuleInstance createInstance() {
		return new ContextInstance();
	}
}
