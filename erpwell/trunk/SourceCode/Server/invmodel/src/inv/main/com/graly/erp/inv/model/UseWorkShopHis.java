package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.wip.model.Lot;
//车间使用
@Entity
@DiscriminatorValue("USE")
public class UseWorkShopHis extends WorkShopHis {
	
	public UseWorkShopHis() {
		super();
	}
	
	public UseWorkShopHis(Lot lot){
		super(lot);
	}
	
}
