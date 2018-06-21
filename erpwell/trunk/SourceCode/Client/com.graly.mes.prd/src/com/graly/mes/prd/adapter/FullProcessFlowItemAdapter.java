package com.graly.mes.prd.adapter;

import java.util.List;

public class FullProcessFlowItemAdapter extends ProcessItemAdapter {
	private static final Object[] EMPTY = new Object[0];
	
	@Override
	public Object[] getElements(Object object) {
		if(object != null && object instanceof List) {
			return new Object[]{((List)object).get(0)};
		} else if(object != null) {
			return new Object[]{object};
		}
		return EMPTY;
	}
}
