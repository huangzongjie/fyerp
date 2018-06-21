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
@DiscriminatorValue("SPLITLOT")
public class SpiltLotHis extends LotHis{
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "toSeqNo ASC")
	@JoinColumn(name = "WIPHIS_RRN", referencedColumnName = "OBJECT_RRN")
	private List<LotHisSMC> smcs;

	
	public SpiltLotHis() {
		super();
	}
	
	public SpiltLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_SPLITLOT);
	}

	public void setSmcs(List<LotHisSMC> smcs) {
		this.smcs = smcs;
	}

	public List<LotHisSMC> getSmcs() {
		return smcs;
	}
	
}
