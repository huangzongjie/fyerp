package com.graly.mes.prd.workflow.context.exe;

import java.io.Serializable;

public interface JbpmTypeMatcher extends Serializable {

	/**
	 * evaluates if the value is a match.
	 * @param value is the value object and it will not be null. 
	 */
	boolean matches(Object value);
}
