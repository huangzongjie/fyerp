package com.graly.erp.wiphis.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.erp.wip.model.ManufactureOrderLine;

@Entity
@DiscriminatorValue("UNMERGE")
public class UnMergeMoLineHis extends MoLineHis {
	private static final long serialVersionUID = 1L;
	
	@Column(name="DATE_UNMERGE")
	private Date dateUnMerge;

	
	public UnMergeMoLineHis() {
		super();
	}

	public UnMergeMoLineHis(ManufactureOrderLine moLine) {
		super(moLine);
		this.setDateUnMerge(moLine.getDateUnMerge());
		this.setTransType(MoLineHis.TRANS_UNMERGE);
	}
	
	public Date getDateUnMerge() {
		return dateUnMerge;
	}

	public void setDateUnMerge(Date dateUnMerge) {
		this.dateUnMerge = dateUnMerge;
	}
}
