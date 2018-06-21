package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("H")//车间回料
public class MovementWorkShopReclaim extends MovementWorkShop  {
	private static final long serialVersionUID = 1L;
	 public MovementWorkShopReclaim(){
		 this.setDocType(MovementWorkShopReclaim.DOCTYPE_HUI);
	 }
}
