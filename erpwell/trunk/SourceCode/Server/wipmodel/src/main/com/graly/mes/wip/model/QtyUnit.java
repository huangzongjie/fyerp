package com.graly.mes.wip.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="WIP_QTYUNIT")
public class QtyUnit extends ProcessUnit{
	private static String UNIT_TYPE = "QtyUnit";
	
	public static String getUnitType(){
		return UNIT_TYPE;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
