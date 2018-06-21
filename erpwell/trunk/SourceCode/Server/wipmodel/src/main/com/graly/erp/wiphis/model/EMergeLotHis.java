package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("MERGE")
public class EMergeLotHis extends LotHis {
	
	public EMergeLotHis() {
		super();
	}
	
	public EMergeLotHis(Lot lot){
		super(lot);
		this.setTransType(LotHis.TRANS_MERGE);
	}
	
}
