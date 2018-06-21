package com.graly.mes.prd.workflow.context.exe.variableinstance;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.prd.workflow.context.exe.VariableInstance;

@Entity
@DiscriminatorValue("D")
public class DoubleInstance extends VariableInstance {

	private static final long serialVersionUID = 1L;

	@Column(name="DOUBLE_VALUE")
	protected Double value = null;

	public boolean isStorable(Object value) {
		if (value == null)
			return true;
		return (Double.class == value.getClass());
	}

	protected Object getObject() {
		return value;
	}

	protected void setObject(Object value) {
		this.value = (Double) value;
	}
}
