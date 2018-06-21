package com.graly.mes.prd.workflow.context.exe.variableinstance;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.prd.workflow.context.exe.VariableInstance;

@Entity
@DiscriminatorValue("L")
public class LongInstance extends VariableInstance {

	private static final long serialVersionUID = 1L;

	@Column(name="LONG_VALUE")
	protected Long value = null;

	public boolean isStorable(Object value) {
		if (value == null)
			return true;
		return (Long.class == value.getClass());
	}

	protected Object getObject() {
		return value;
	}

	protected void setObject(Object value) {
		this.value = (Long) value;
	}

}
