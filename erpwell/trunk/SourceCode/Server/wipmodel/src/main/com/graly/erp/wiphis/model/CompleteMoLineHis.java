package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("COMPLETE")
public class CompleteMoLineHis extends MoLineHis {
	
	public CompleteMoLineHis() {
		super();
	}
	
	public CompleteMoLineHis(ManufactureOrderLine moLine){
		super(moLine);
		this.setTransType(MoLineHis.TRANS_COMPLETE);
	}
	
}
