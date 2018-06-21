package com.graly.mes.prd.workflow.context.exe.variableinstance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.graly.mes.prd.workflow.bytes.ByteArray;
import com.graly.mes.prd.workflow.context.exe.VariableInstance;

@Entity
@DiscriminatorValue("B")
public class ByteArrayInstance extends VariableInstance {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "BYTEARRAY_VALUE_RRN", referencedColumnName = "OBJECT_RRN")
	protected ByteArray value = null;

	public boolean isStorable(Object value) {
		if (value == null)
			return true;
		return (ByteArray.class.isAssignableFrom(value.getClass()));
	}

	protected Object getObject() {
		return value;
	}

	protected void setObject(Object value) {
		this.value = (ByteArray) value;
	}
}
