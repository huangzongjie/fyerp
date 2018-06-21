package com.graly.mes.prd.workflow.context.exe.variableinstance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.prd.workflow.context.exe.VariableInstance;

@Entity
@DiscriminatorValue("D")
public class DateInstance extends VariableInstance {

	private static final long serialVersionUID = 1L;

	@Column(name="DATE_VALUE")
	protected Date value = null;

	public boolean isStorable(Object value) {
		if (value == null)
			return true;
		return (Date.class.isAssignableFrom(value.getClass()));
	}

	protected Object getObject() {
		return value;
	}

	protected void setObject(Object value) {
		this.value = (Date) value;
	}
}
