package com.graly.mes.prd.workflow.context.exe.matcher;

import java.io.Serializable;

import com.graly.mes.prd.workflow.context.exe.JbpmTypeMatcher;

public class SerializableMatcher implements JbpmTypeMatcher {

	private static final long serialVersionUID = 1L;

	public boolean matches(Object value) {
		return (Serializable.class.isAssignableFrom(value.getClass()));
	}
}
