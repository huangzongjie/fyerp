package com.graly.erp.pur.request;

import org.eclipse.jface.viewers.StructuredViewer;

import com.graly.erp.pur.po.POLineEntityBlock;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class ColorEntityTableManager extends EntityTableManager {
	public RequisitionLineEntityBlock prLineBlock;
	public POLineEntityBlock poLineBlock;

	public ColorEntityTableManager(ADTable adTable) {
		super(adTable);
	}

	public void updateView(StructuredViewer viewer) {
		super.updateView(viewer);

		if (getPrLineBlock() != null) {
			getPrLineBlock().compareWithRefValue();
		}
		if (getPoLineBlock() != null) {
			getPoLineBlock().compareWithRelValue();
		}
	}

	public RequisitionLineEntityBlock getPrLineBlock() {
		return prLineBlock;
	}

	public void setPrLineBlock(RequisitionLineEntityBlock prLineBlock) {
		this.prLineBlock = prLineBlock;
	}

	public POLineEntityBlock getPoLineBlock() {
		return poLineBlock;
	}

	public void setPoLineBlock(POLineEntityBlock poLineBlock) {
		this.poLineBlock = poLineBlock;
	}
}
