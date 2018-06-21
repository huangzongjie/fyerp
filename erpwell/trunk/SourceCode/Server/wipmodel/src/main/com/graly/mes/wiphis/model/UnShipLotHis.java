package com.graly.mes.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("UNSHIPLOT")
public class UnShipLotHis extends LotHis{
	
	public UnShipLotHis() {
		super();
	}
	
	public UnShipLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_UNSHIPLOT);
	}
	
}
