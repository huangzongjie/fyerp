package com.graly.mes.prd.designer.model;

import com.graly.mes.prd.designer.common.model.AbstractNamedElement;

public class CancelTimer extends AbstractNamedElement implements ActionElement {
	
	public String getName() {
		String result = super.getName();
		if (result == null) {
			result = "";
		}
		return result;
	}

}
