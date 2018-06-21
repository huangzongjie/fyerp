package com.graly.erp.ppm.model;

import java.math.BigDecimal;
import java.util.Comparator;

public class MpsLineComparator implements Comparator<MpsLine> {
	
	/*
	 * @return ����ֵС��0������ǰ��
	 */
	public int compare(MpsLine obj1, MpsLine obj2) {
		
		MpsLine line1 = (MpsLine)obj1;
		MpsLine line2 = (MpsLine)obj2;
		
		//�������������
		if (line1.getDateDelivered().compareTo(line2.getDateDelivered()) != 0) {
			return line1.getDateDelivered().compareTo(line2.getDateDelivered());
		}
		
		//���ȼ��ߵ�����(����ߣ����)
		if (line1.getPriority() != null && line2.getPriority() != null) {
			if (line1.getPriority().compareTo(line2.getPriority()) != 0) {
				return line1.getPriority().compareTo(line2.getPriority());
			}
		}
		
		//�ܼ�ֵ�������
		if (line1.getRefernectPrice() != null && line2.getRefernectPrice() != null) {
			BigDecimal totalPrice1 = line1.getQtyMps().multiply(line1.getRefernectPrice());
			BigDecimal totalPrice2 = line2.getQtyMps().multiply(line2.getRefernectPrice());
			return -totalPrice1.compareTo(totalPrice2);
		}
		
		//���������ڶ̵�����
		if (line1.getStandTime()!= null && line2.getStandTime() != null) {
			BigDecimal totalTime1 = line1.getQtyMps().multiply(line1.getStandTime());
			BigDecimal totalTime2 = line2.getQtyMps().multiply(line2.getStandTime());
			return totalTime1.compareTo(totalTime2);
		}
		
		return 0;
	}
}