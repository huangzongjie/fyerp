package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("TRANSFER")
public class TransferLotHis extends LotHis {
	
	public TransferLotHis() {
		super();
	}
	
	public TransferLotHis(Lot lot){
		super(lot);
		this.setTransType(LotHis.TRANS_TRANSFER);
	}
	
}
