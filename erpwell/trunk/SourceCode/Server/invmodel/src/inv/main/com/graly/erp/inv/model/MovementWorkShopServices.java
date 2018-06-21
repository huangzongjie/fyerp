package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("F")//服务公司
public class MovementWorkShopServices extends MovementWorkShop  {
	private static final long serialVersionUID = 1L;
	
	public MovementWorkShopServices(){
		this.setDocType(MovementWorkShopServices.DOCTYPE_SER);
	}
}
