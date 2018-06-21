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
@DiscriminatorValue("UNSCRAPLOT")
public class UnScrapLotHis extends LotHis{
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name = "WIPHIS_RRN", referencedColumnName = "OBJECT_RRN")
	private List<LotHisSBD> sbds;

	
	public UnScrapLotHis() {
		super();
	}
	
	public UnScrapLotHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_UNSCRAPLOT);
	}

	public void setSbds(List<LotHisSBD> sbds) {
		this.sbds = sbds;
	}

	public List<LotHisSBD> getSbds() {
		return sbds;
	}
	
}
