package com.graly.erp.wiphis.model;

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
import com.graly.mes.wiphis.model.LotHis;

@Entity
@DiscriminatorValue("SPLITOUT")
public class ESplitOutHis extends LotHis{
	
	public ESplitOutHis() {
		super();
	}
	
	public ESplitOutHis(Lot lot){
		super(lot);
		this.setTransType(LotHis.TRANS_SPLIT);
	}
	
}
