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
@DiscriminatorValue("MERGEOUT")
public class MergeOutHis extends LotHis{
	
	public MergeOutHis() {
		super();
	}
	
	public MergeOutHis(Lot lot){
		super(lot);
		this.setTransType(LotStateMachine.TRANS_MERGEOUT);
	}
	
}
