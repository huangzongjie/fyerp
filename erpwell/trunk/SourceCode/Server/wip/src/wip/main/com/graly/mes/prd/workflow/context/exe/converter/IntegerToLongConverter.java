package com.graly.mes.prd.workflow.context.exe.converter;

import com.graly.mes.prd.workflow.context.exe.Converter;

public class IntegerToLongConverter implements Converter {

	private static final long serialVersionUID = 1L;

	public boolean supports(Object value) {
		if (value == null)
			return true;
		return (Integer.class.equals(value.getClass()));
	}

	public Object convert(Object o) {
		return new Long(((Number) o).longValue());
	}

	public Object revert(Object o) {
		return new Integer(((Long) o).intValue());
	}
}
