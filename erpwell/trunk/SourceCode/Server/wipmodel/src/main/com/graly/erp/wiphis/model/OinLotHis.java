package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("OIN")
public class OinLotHis extends LotHis {
	
	public OinLotHis() {
		super();
	}
	
	public OinLotHis(Lot lot){
		super(lot);
		this.setTransType(LotHis.TRANS_OIN);
	}
	
}
