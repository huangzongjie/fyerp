package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("DOU")
public class DouLotHis extends LotHis {
	private static final long serialVersionUID = 1L;

	public DouLotHis() {
		super();
	}

	public DouLotHis(Lot lot) {
		super(lot);
		setTransType(LotHis.TRANS_DOU);
	}

}