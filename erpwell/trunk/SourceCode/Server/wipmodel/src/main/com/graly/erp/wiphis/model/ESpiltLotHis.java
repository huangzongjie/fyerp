package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("SPLIT")
public class ESpiltLotHis extends LotHis {
	
	public ESpiltLotHis() {
		super();
	}
	
	public ESpiltLotHis(Lot lot){
		super(lot);
		this.setTransType(LotHis.TRANS_SPLIT);
	}
	
}
