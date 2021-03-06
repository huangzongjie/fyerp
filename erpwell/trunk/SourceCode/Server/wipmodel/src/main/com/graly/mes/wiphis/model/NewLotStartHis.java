package com.graly.mes.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("NEWLOTSTART")
public class NewLotStartHis extends LotHis{
	
	public NewLotStartHis() {
		super();
	}
	
	public NewLotStartHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_NEWLOTSTART);
	}
	
}
