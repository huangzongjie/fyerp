package com.graly.mes.prd.workflow.context.exe.converter;

import com.graly.mes.prd.workflow.bytes.ByteArray;
import com.graly.mes.prd.workflow.context.exe.Converter;

public class BytesToByteArrayConverter implements Converter {

	private static final long serialVersionUID = 1L;

	public boolean supports(Object value) {
		if (value == null)
			return true;
		return (value.getClass() == byte[].class);
	}

	public Object convert(Object o) {
		return new ByteArray((byte[]) o);
	}

	public Object revert(Object o) {
		return ((ByteArray) o).getBytes();
	}

}
