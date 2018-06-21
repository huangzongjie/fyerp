package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("OOU")
public class OouLotHis extends LotHis {
	
	public OouLotHis() {
		super();
	}
	
	public OouLotHis(Lot lot){
		super(lot);
		this.setTransType(LotHis.TRANS_OOU);
	}
	
}
