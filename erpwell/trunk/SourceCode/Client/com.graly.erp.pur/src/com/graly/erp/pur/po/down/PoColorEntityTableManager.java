package com.graly.erp.pur.po.down;

import com.graly.erp.pur.request.ColorEntityTableManager;
import com.graly.erp.pur.request.RequisitionLineEntityBlock;
import com.graly.framework.activeentity.model.ADTable;

public class PoColorEntityTableManager extends ColorEntityTableManager {
	public RequisitionLineEntityBlock prLineBlock;
	public PoDownLineEntityBlock poDownLineEntityBlock;
	public PoColorEntityTableManager(ADTable adTable) {
		super(adTable);
	}
	public RequisitionLineEntityBlock getPrLineBlock() {
		return prLineBlock;
	}
	public void setPrLineBlock(RequisitionLineEntityBlock prLineBlock) {
		this.prLineBlock = prLineBlock;
	}
	public PoDownLineEntityBlock getPoDownLineEntityBlock() {
		return poDownLineEntityBlock;
	}
	public void setPoDownLineEntityBlock(PoDownLineEntityBlock poDownLineEntityBlock) {
		this.poDownLineEntityBlock = poDownLineEntityBlock;
	}

}
