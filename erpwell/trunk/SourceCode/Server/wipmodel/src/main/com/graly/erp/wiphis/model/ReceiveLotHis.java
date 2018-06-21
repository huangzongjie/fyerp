package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("RECEIVE")
public class ReceiveLotHis extends LotHis {
	
	public ReceiveLotHis() {
		super();
	}
	
	public ReceiveLotHis(Lot lot){
		super(lot);
		this.setTransType(LotHis.TRANS_RECEIVE);
	}
	
}
