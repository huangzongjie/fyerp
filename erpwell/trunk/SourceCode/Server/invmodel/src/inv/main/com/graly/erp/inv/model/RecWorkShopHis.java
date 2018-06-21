package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.wip.model.Lot;
//车间接收历史
@Entity
@DiscriminatorValue("RECEIVE")
public class RecWorkShopHis extends WorkShopHis {
	
	public RecWorkShopHis() {
		super();
	}
	
	public RecWorkShopHis(Lot lot){
		super(lot);
	}
	
}
