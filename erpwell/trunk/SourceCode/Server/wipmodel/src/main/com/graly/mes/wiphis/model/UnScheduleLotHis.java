package com.graly.mes.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("UNSCHEDLOT")
public class UnScheduleLotHis extends LotHis{
	
	public UnScheduleLotHis() {
		super();
	}
	
	public UnScheduleLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_UNSCHEDLOT);
		this.setHisSeq(0L);
	}
	
}
