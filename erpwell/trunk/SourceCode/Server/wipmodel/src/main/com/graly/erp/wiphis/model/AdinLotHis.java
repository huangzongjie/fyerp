package com.graly.erp.wiphis.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("ADIN")
public class AdinLotHis extends LotHis {
	private static final long serialVersionUID = 1L;

	public AdinLotHis() {
		super();
	}

	public AdinLotHis(Lot lot) {
		super(lot);
		setTransType(LotHis.TRANS_ADIN);
	}

}
