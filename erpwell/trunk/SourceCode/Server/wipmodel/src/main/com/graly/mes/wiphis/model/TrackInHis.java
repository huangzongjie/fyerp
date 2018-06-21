package com.graly.mes.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("TRACKIN")
public class TrackInHis extends LotHis{
	
	public TrackInHis() {
		super();
	}
	
	public TrackInHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_TRACKIN);
	}
	
}
