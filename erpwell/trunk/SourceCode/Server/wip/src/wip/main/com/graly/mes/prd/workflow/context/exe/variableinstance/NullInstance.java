package com.graly.mes.prd.workflow.context.exe.variableinstance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.prd.workflow.context.exe.VariableInstance;

@Entity
@DiscriminatorValue("N")
public class NullInstance extends VariableInstance {

	private static final long serialVersionUID = 1L;

	public boolean isStorable(Object value) {
		return (value == null);
	}

	protected Object getObject() {
		return null;
	}

	protected void setObject(Object value) {
	}
}
