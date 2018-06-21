package com.graly.erp.ppm.model;

import java.math.BigDecimal;
import java.util.Comparator;

public class MpsLineComparator implements Comparator<MpsLine> {
	
	/*
	 * @return 返回值小于0的排在前面
	 */
	public int compare(MpsLine obj1, MpsLine obj2) {
		
		MpsLine line1 = (MpsLine)obj1;
		MpsLine line2 = (MpsLine)obj2;
		
		//交货期早的先做
		if (line1.getDateDelivered().compareTo(line2.getDateDelivered()) != 0) {
			return line1.getDateDelivered().compareTo(line2.getDateDelivered());
		}
		
		//优先级高的先做(１最高５最低)
		if (line1.getPriority() != null && line2.getPriority() != null) {
			if (line1.getPriority().compareTo(line2.getPriority()) != 0) {
				return line1.getPriority().compareTo(line2.getPriority());
			}
		}
		
		//总价值大的先做
		if (line1.getRefernectPrice() != null && line2.getRefernectPrice() != null) {
			BigDecimal totalPrice1 = line1.getQtyMps().multiply(line1.getRefernectPrice());
			BigDecimal totalPrice2 = line2.getQtyMps().multiply(line2.getRefernectPrice());
			return -totalPrice1.compareTo(totalPrice2);
		}
		
		//总生产周期短的先做
		if (line1.getStandTime()!= null && line2.getStandTime() != null) {
			BigDecimal totalTime1 = line1.getQtyMps().multiply(line1.getStandTime());
			BigDecimal totalTime2 = line2.getQtyMps().multiply(line2.getStandTime());
			return totalTime1.compareTo(totalTime2);
		}
		
		return 0;
	}
}