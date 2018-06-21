package com.graly.erp.inv.transfer;

import org.eclipse.swt.graphics.Color;

import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.ListItemAdapter;
import com.graly.mes.wip.model.Lot;

public class TrsLotItemAdapter extends ListItemAdapter {
	GainableSplitLot gainable;
	
	public TrsLotItemAdapter(GainableSplitLot gainable) {
		super();
		this.gainable = gainable;
	}

	@Override
	public Color getForeground(Object element, String id) {
		if(element instanceof Lot) {
			Lot lot = (Lot)element;
			if(gainable.getGainableSplitLot() != null
					&& gainable.getGainableSplitLot().contains(lot)) {
				return SWTResourceCache.getColor("Function");
			}
		}
		return super.getForeground(element, id);
	}
}
