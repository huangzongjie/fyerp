package com.graly.mes.prd.workflow.context.exe.matcher;

import com.graly.mes.prd.workflow.context.exe.JbpmTypeMatcher;

public class ClassNameMatcher implements JbpmTypeMatcher {

	private static final long serialVersionUID = 1L;

	String className = null;

	public boolean matches(Object value) {
		boolean matches = false;

		Class valueClass = value.getClass();

		while ((!matches) && (valueClass != null)) {
			if (className.equals(valueClass.getName())) {
				matches = true;
			} else {
				Class[] interfaces = valueClass.getInterfaces();
				for (int i = 0; (i < interfaces.length) && (!matches); i++) {
					if (className.equals(interfaces[i].getName())) {
						matches = true;
					}
				}
				if (!matches) {
					valueClass = valueClass.getSuperclass();
				}
			}
		}
		return matches;
	}
}
