package com.graly.mes.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("HOLDLOT")
public class HoldLotHis extends LotHis{
	
	public HoldLotHis() {
		super();
	}
	
	public HoldLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_HOLDLOT);
	}
	
}
