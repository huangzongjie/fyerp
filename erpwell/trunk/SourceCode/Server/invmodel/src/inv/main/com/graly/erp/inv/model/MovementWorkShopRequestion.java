package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("R")//MaterialTransfer物料领用
public class MovementWorkShopRequestion extends MovementWorkShop  {
	private static final long serialVersionUID = 1L;
	
	public MovementWorkShopRequestion(){
		this.setDocType(MovementWorkShopRequestion.DOCTYPE_MAN);
	}
}
