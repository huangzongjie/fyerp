package com.graly.erp.wip.workcenter.receive;

import org.eclipse.swt.graphics.Color;

import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.ListItemAdapter;
import com.graly.mes.wip.model.Lot;

public class MoBomItemAdapter extends ListItemAdapter {
	public static Color Function = SWTResourceCache.getColor("Function");
	GainableMoBoms gainable;

	public MoBomItemAdapter(GainableMoBoms gainable) {
		super();
		this.gainable = gainable;
	}

	@Override
	public Color getForeground(Object element, String id) {
		if(element instanceof ManufactureOrderBom) {
			ManufactureOrderBom bom = (ManufactureOrderBom)element;
			if(bom.getMaterial() != null
					&& Lot.LOTTYPE_MATERIAL.equals(bom.getMaterial().getLotType())) {
				return Function;
			}
			if(gainable.getReceivedFinishedMoBoms() != null
					&& gainable.getReceivedFinishedMoBoms().contains(bom))
				return Function;
		}
		return super.getForeground(element, id);
	}
}
