package com.graly.erp.spread.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="MOVE")
public class MaterialMove extends MaterialSpread {
	private static final long	serialVersionUID	= 1L;

	@Column(name="TARGET_WORKCENTER")
	private String targetWorkcenter;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="MOVE_DATE")
	private Date moveDate;

	public String getTargetWorkcenter() {
		return targetWorkcenter;
	}

	public void setTargetWorkcenter(String targetWorkcenter) {
		this.targetWorkcenter = targetWorkcenter;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getMoveDate() {
		return moveDate;
	}

	public void setMoveDate(Date moveDate) {
		this.moveDate = moveDate;
	}
}
