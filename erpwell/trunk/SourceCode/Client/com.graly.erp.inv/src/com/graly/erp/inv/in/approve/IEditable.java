package com.graly.erp.inv.in.approve;

import java.util.Set;

public interface IEditable {

	boolean isCanEdit(String propertyName);
	
	Set<String> getEditableProperty();
}
