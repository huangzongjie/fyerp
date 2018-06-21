package com.graly.erp.xz.pur.po;

import org.eclipse.jface.viewers.StructuredViewer;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class XZColorEntityTableManager extends EntityTableManager {
//	public RequisitionLineEntityBlock prLineBlock;
	public XZPOLineEntityBlock poLineBlock;

	public XZColorEntityTableManager(ADTable adTable) {
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

	public XZPOLineEntityBlock getPoLineBlock() {
		return poLineBlock;
	}

	public void setPoLineBlock(XZPOLineEntityBlock poLineBlock) {
		this.poLineBlock = poLineBlock;
	}
}
