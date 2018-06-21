package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.wip.model.Lot;
//车间使用
@Entity
@DiscriminatorValue("WIN")
public class WinWorkShopHis extends WorkShopHis {
	private static final long serialVersionUID = 1L;

	public WinWorkShopHis() {
		super();
	}
	
	public WinWorkShopHis(Lot lot){
		super(lot);
	}
	
}
