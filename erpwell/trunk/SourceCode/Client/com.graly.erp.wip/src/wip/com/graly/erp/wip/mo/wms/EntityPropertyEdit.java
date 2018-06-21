package com.graly.erp.wip.mo.wms;

import java.util.HashSet;
import java.util.Set;

public class EntityPropertyEdit implements IEditable {
	protected Set<String> editablePros;
	
	public EntityPropertyEdit(Set<String> editablePros) {
		this.editablePros = editablePros;
	}

	@Override
	public boolean isCanEdit(String propertyName) {
		if(this.getEditableProperty().contains(propertyName)) {
			return true;
		}
		return false;
	}

	@Override
	public Set<String> getEditableProperty() {
		if(editablePros == null)
			editablePros = new HashSet<String>();
		return editablePros;
	}

}
