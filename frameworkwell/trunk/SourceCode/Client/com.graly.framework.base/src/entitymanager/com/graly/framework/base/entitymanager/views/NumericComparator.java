package com.graly.framework.base.entitymanager.views;

import java.math.BigDecimal;
import java.util.Comparator;


public class NumericComparator implements Comparator<Object> {

	@Override
	public int compare(Object arg0, Object arg1) {
		if(arg0 instanceof BigDecimal && arg1 instanceof BigDecimal) {
			BigDecimal v1 = (BigDecimal)arg0;
			BigDecimal v2 = (BigDecimal)arg1;
			return v1.compareTo(v2);
		}
		else if(arg0 instanceof Long && arg1 instanceof Long) {
			Long v1 = (Long)arg0;
			Long v2 = (Long)arg1;
			return v1.compareTo(v2);
		}
		else if(arg0 instanceof Double && arg1 instanceof Double) {
			Double v1 = (Double)arg0;
			Double v2 = (Double)arg1;
			return v1.compareTo(v2);
		}
		else if(arg0 instanceof Integer && arg1 instanceof Integer) {
			Integer v1 = (Integer)arg0;
			Integer v2 = (Integer)arg1;
			return v1.compareTo(v2);
		}
		return 0;
	}

}
