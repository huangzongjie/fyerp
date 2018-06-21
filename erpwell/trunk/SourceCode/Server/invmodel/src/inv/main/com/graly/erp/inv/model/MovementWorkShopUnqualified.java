package com.graly.erp.inv.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("U")//����Ʒ����
public class MovementWorkShopUnqualified extends MovementWorkShop  {
	private static final long serialVersionUID = 1L;
	
	public MovementWorkShopUnqualified(){
		this.setDocType(MovementWorkShopUnqualified.DOCTYPE_UNQ);
	}
}
