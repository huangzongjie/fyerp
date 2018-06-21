package com.graly.mes.prd.designer.common.xml;

import com.graly.mes.prd.designer.common.model.GenericElement;
import com.graly.mes.prd.designer.common.model.SemanticElement;

public class GenericElementNameProvider implements XmlAdapterNameProvider {

	public String getName(SemanticElement element) {
		if (!(element instanceof GenericElement)) return null;
		return ((GenericElement)element).getName();
	}

}
