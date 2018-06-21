package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("AOU")
public class AouLotHis extends LotHis {
	private static final long serialVersionUID = 1L;

	public AouLotHis() {
		super();
	}

	public AouLotHis(Lot lot) {
		super(lot);
		setTransType(LotHis.TRANS_AOU);
	}

}
