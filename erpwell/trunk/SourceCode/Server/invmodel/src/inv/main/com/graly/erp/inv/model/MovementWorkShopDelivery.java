package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("D")//≥µº‰≈‰ÀÕ
public class MovementWorkShopDelivery extends MovementWorkShop  {
	private static final long serialVersionUID = 1L;
	
	public MovementWorkShopDelivery(){
		this.setDocType(MovementWorkShopDelivery.DOCTYPE_DEL);
	}
}
