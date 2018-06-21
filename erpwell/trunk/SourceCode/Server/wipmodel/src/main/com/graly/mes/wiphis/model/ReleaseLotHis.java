package com.graly.mes.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("RELEASELOT")
public class ReleaseLotHis extends LotHis{
	
	public ReleaseLotHis() {
		super();
	}
	
	public ReleaseLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_RELEASELOT);
	}
	
}
