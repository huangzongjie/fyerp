package com.graly.mes.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("CHANGELOT")
public class ChangeLotHis extends LotHis{
	
	public ChangeLotHis() {
		super();
	}
	
	public ChangeLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_CHANGELOT);
		this.setHisSeq(0L);
	}
	
}
