package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("CLOSE")
public class CloseMoLineHis extends MoLineHis {
	
	public CloseMoLineHis() {
		super();
	}
	
	public CloseMoLineHis(ManufactureOrderLine moLine){
		super(moLine);
		this.setTransType(MoLineHis.TRANS_CLOSE);
	}
	
}
