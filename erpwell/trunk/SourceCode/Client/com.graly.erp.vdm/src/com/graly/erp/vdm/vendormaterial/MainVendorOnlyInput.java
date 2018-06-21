package com.graly.erp.vdm.vendormaterial;

import com.graly.framework.base.entitymanager.adapter.EntityItemInput;

public class MainVendorOnlyInput extends EntityItemInput {
	private boolean isQueryMainVendorOnly = false;
	private long displaCount = 0L;

	public MainVendorOnlyInput(){
		super(null);
	}

	public MainVendorOnlyInput(boolean isQueryMainVendorOnly){
		this();
		this.isQueryMainVendorOnly = isQueryMainVendorOnly;
	}

	public boolean isQueryMainVendorOnly() {
		return isQueryMainVendorOnly;
	}

	public void setQueryMainVendorOnly(boolean isQueryMainVendorOnly) {
		this.isQueryMainVendorOnly = isQueryMainVendorOnly;
	}

	public long getDisplaCount() {
		return displaCount;
	}

	public void setDisplaCount(long displaCount) {
		this.displaCount = displaCount;
	}
}
