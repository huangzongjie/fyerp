package com.graly.erp.wip.mo.wms;

import java.util.Set;

public interface IEditable {

	boolean isCanEdit(String propertyName);
	
	Set<String> getEditableProperty();
}
