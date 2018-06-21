package com.graly.erp.inv.rejectin;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.otherin.OtherInLineProperties;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;

public class RejectInLineProperties extends OtherInLineProperties {

	public RejectInLineProperties() {
		super();
	}

	public RejectInLineProperties(EntityBlock masterParent, ADTable table,
			Object parentObject, boolean flag) {
		super(masterParent, table, parentObject, flag);
	}

	protected MovementIn.InType getInType() {
		return MovementIn.InType.RIN;
	}
}
