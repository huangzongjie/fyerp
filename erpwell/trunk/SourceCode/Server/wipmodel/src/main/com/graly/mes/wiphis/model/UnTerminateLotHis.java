package com.graly.mes.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("UNTERMLOT")
public class UnTerminateLotHis extends LotHis{
	
	public UnTerminateLotHis() {
		super();
	}
	
	public UnTerminateLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_UNTERMLOT);
	}
	
}
