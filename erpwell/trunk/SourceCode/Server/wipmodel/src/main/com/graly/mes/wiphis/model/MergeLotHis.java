package com.graly.mes.wiphis.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotStateMachine;

@Entity
@DiscriminatorValue("MERGELOT")
public class MergeLotHis extends LotHis{
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "fromSeqNo ASC")
	@JoinColumn(name = "WIPHIS_RRN", referencedColumnName = "OBJECT_RRN")
	private List<LotHisSMC> smcs;

	public MergeLotHis() {
		super();
	}
	
	public MergeLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_MERGELOT);
	}

	public void setSmcs(List<LotHisSMC> smcs) {
		this.smcs = smcs;
	}

	public List<LotHisSMC> getSmcs() {
		return smcs;
	}
	
}
