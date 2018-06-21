package com.graly.erp.inv.outother;

import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.OutLineLotDialog;
import com.graly.erp.inv.out.OutLineLotSection;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;

public class OutOtherLintLotSection extends OutLineLotSection {
	public OutOtherLintLotSection(ADBase parent, ADBase child, ADTable adTable,
			OutLineLotDialog olld, boolean isView) {
		super(parent, child, adTable, olld, isView);
	}
	
	//  重载getOutType(), 使出库的参数类型为OutType.OOU
	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.OOU;
	}
}
