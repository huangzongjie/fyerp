package com.graly.mes.prd.workflow.context.exe.converter;

import com.graly.mes.prd.workflow.context.exe.Converter;

public class DoubleToStringConverter implements Converter {

	private static final long serialVersionUID = 1L;

	public boolean supports(Object value) {
		if (value == null)
			return true;
		return (value.getClass() == Double.class);
	}

	public Object convert(Object o) {
		return o.toString();
	}

	public Object revert(Object o) {
		return new Double((String) o);
	}

}
