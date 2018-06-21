package com.graly.erp.wiphis.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.erp.wip.model.ManufactureOrderLine;

@Entity
@DiscriminatorValue("MERGE")
public class MergeMoLineHis extends MoLineHis {
	private static final long serialVersionUID = 1L;

	@Column(name="MERGE_NEW_RRN")
	private Long mergeNewRrn;
	
	@Column(name="DATE_MERGE")
	private Date dateMerge;
	
	@Column(name="MERGE_BY")
	private Long mergeBy;
	
	public MergeMoLineHis() {
		super();
	}

	public MergeMoLineHis(ManufactureOrderLine moLine) {
		super(moLine);
		this.setDateMerge(moLine.getDateMerge());
		this.setMergeBy(moLine.getMergeBy());
		this.setMergeNewRrn(moLine.getMergeNewRrn());
		this.setTransType(MoLineHis.TRANS_MERGE);
	}

	public Long getMergeNewRrn() {
		return mergeNewRrn;
	}

	public void setMergeNewRrn(Long mergeNewRrn) {
		this.mergeNewRrn = mergeNewRrn;
	}

	public Date getDateMerge() {
		return dateMerge;
	}

	public void setDateMerge(Date dateMerge) {
		this.dateMerge = dateMerge;
	}

	public Long getMergeBy() {
		return mergeBy;
	}

	public void setMergeBy(Long mergeBy) {
		this.mergeBy = mergeBy;
	}
}
