package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("V")//ƽ̨����������
public class MovementWorkShopVirtualHouse extends MovementWorkShop  {
	private static final long serialVersionUID = 1L;
	
	public MovementWorkShopVirtualHouse(){
		this.setDocType(MovementWorkShopVirtualHouse.DOCTYPE_VIR);
	}
}
