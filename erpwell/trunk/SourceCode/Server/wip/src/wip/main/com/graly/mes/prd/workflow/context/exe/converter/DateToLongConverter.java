package com.graly.mes.prd.workflow.context.exe.converter;

import java.util.Date;

import com.graly.mes.prd.workflow.context.exe.Converter;

public class DateToLongConverter implements Converter {

	private static final long serialVersionUID = 1L;

	public boolean supports(Object value) {
		if (value == null)
			return true;
		return (Date.class.isAssignableFrom(value.getClass()));
	}

	public Object convert(Object o) {
		return new Long(((Date) o).getTime());
	}

	public Object revert(Object o) {
		return new Date(((Long) o).longValue());
	}
}
