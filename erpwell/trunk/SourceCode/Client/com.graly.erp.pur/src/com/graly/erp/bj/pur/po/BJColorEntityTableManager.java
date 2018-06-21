package com.graly.erp.bj.pur.po;

import org.eclipse.jface.viewers.StructuredViewer;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class BJColorEntityTableManager extends EntityTableManager {
//	public RequisitionLineEntityBlock prLineBlock;
	public BJPOLineEntityBlock poLineBlock;

	public BJColorEntityTableManager(ADTable adTable) {
		super(adTable);
	}

	public void updateView(StructuredViewer viewer) {
		super.updateView(viewer);

//		if (getPrLineBlock() != null) {
//			getPrLineBlock().compareWithRefValue();
//		}
//		if (getPoLineBlock() != null) {
//			getPoLineBlock().compareWithRelValue();
//		}
	}

//	public RequisitionLineEntityBlock getPrLineBlock() {
//		return prLineBlock;
//	}

//	public void setPrLineBlock(RequisitionLineEntityBlock prLineBlock) {
//		this.prLineBlock = prLineBlock;
//	}

	public BJPOLineEntityBlock getPoLineBlock() {
		return poLineBlock;
	}

	public void setPoLineBlock(BJPOLineEntityBlock poLineBlock) {
		this.poLineBlock = poLineBlock;
	}
}
