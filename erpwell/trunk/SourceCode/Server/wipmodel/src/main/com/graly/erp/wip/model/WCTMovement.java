package com.graly.erp.wip.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_WCT_MOVMT")
public class WCTMovement extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	public static final String DOC_TYPE_WCTM="WCTM";
	public static final String DOC_STATUS_DRAFTED = "DRAFTED";
	public static final String DOC_STATUS_APPROVED = "APPROVED";
	
	@Column(name="DOC_ID")
	private String docId;
	
	@Column(name="DOC_TYPE")
	private String docType;
	
	@Column(name="DOC_STATUS")
	private String docStatus;
	
	@Column(name="SRC_WORKCENTER_RRN")
	private Long srcWorkcenterRrn;
	
	@Column(name="SRC_WORKCENTER_NAME")
	private String srcWorkcenterName;
	
	@Column(name="DES_WORKCENTER_RRN")
	private Long desWorkcenterRrn;

	@Column(name="DES_WORKCENTER_NAME")
	private String desWorkcenterName;

	@Column(name="RECIPIENT")
	private Long recipient;

	@Column(name="GIVER")
	private Long giver;

	@Column(name="DATE_MOVEMENT")
	private Date dateMovement;
	
	@OneToMany(mappedBy="movement",fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@OrderBy(value = "lineNo ASC")
	private List<WCTMovementLine> wctMovementLines;

	@Column(name="TOTAL_LINES")
	private Long totalLines = 0L;
	
	@Column(name="DATE_APPROVE")
	private Date dateApprove;
	
	@Column(name="APPROVE_BY")
	private Long approveBy;
	
	public Long getRecipient() {
		return recipient;
	}

	public void setRecipient(Long recipient) {
		this.recipient = recipient;
	}

	public Long getGiver() {
		return giver;
	}

	public void setGiver(Long giver) {
		this.giver = giver;
	}

	public Date getDateMovement() {
		return dateMovement;
	}

	public void setDateMovement(Date dateMovement) {
		this.dateMovement = dateMovement;
	}
	
	public List<WCTMovementLine> getWCTMovementLines() {
		return wctMovementLines;
	}

	public void setWCTMovementLines(List<WCTMovementLine> wctMovementLines) {
		this.wctMovementLines = wctMovementLines;
	}

	public Long getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(Long totalLines) {
		this.totalLines = totalLines;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public List<WCTMovementLine> getWctMovementLines() {
		return wctMovementLines;
	}

	public void setWctMovementLines(List<WCTMovementLine> wctMovementLines) {
		this.wctMovementLines = wctMovementLines;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public Date getDateApprove() {
		return dateApprove;
	}

	public void setDateApprove(Date dateApprove) {
		this.dateApprove = dateApprove;
	}

	public Long getApproveBy() {
		return approveBy;
	}

	public void setApproveBy(Long approveBy) {
		this.approveBy = approveBy;
	}

	public Long getSrcWorkcenterRrn() {
		return srcWorkcenterRrn;
	}

	public void setSrcWorkcenterRrn(Long srcWorkcenterRrn) {
		this.srcWorkcenterRrn = srcWorkcenterRrn;
	}

	public String getSrcWorkcenterName() {
		return srcWorkcenterName;
	}

	public void setSrcWorkcenterName(String srcWorkcenterName) {
		this.srcWorkcenterName = srcWorkcenterName;
	}

	public Long getDesWorkcenterRrn() {
		return desWorkcenterRrn;
	}

	public void setDesWorkcenterRrn(Long desWorkcenterRrn) {
		this.desWorkcenterRrn = desWorkcenterRrn;
	}

	public String getDesWorkcenterName() {
		return desWorkcenterName;
	}

	public void setDesWorkcenterName(String desWorkcenterName) {
		this.desWorkcenterName = desWorkcenterName;
	}
}
