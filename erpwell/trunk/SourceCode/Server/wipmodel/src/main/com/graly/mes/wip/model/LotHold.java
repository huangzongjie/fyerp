package com.graly.mes.wip.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "WIP_LOT_HLD")
public class LotHold implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "OBJECT_RRN", sequenceName = "OBJECT_RRN", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBJECT_RRN")
	@Column(name = "OBJECT_RRN")
	protected Long objectRrn;

	@Column(name = "LOT_RRN")
	private Long lotRrn;

	@Column(name = "SEQ_NO")
	private Long seqNo;

	@Column(name = "HOLD_USER_RRN")
	private Long holdUserRrn;
	
	@Column(name = "HOLD_CODE")
	private String holdCode;

	@Column(name = "HOLD_REASON")
	private String holdReason;

	@Column(name = "HOLD_PWD")
	private String holdPwd;

	@Column(name="PRE_COM_CLASS")
	private String preComClass;
	
	@Column(name="PRE_STATE")
	private String preState;
	
	@Column(name="PRE_SUB_STATE")
	private String preSubState;

	@Column(name="PRE_STATE_ENTRY_TIME")
	private Date preStateEntryTime;

	@Transient
	private String actionComment;
	
	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}

	public Long getLotRrn() {
		return lotRrn;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}

	public String getHoldCode() {
		return holdCode;
	}

	public void setHoldCode(String holdCode) {
		this.holdCode = holdCode;
	}

	public String getHoldReason() {
		return holdReason;
	}

	public void setHoldReason(String holdReason) {
		this.holdReason = holdReason;
	}

	public Long getHoldUserRrn() {
		return holdUserRrn;
	}

	public void setHoldUserRrn(Long holdUserRrn) {
		this.holdUserRrn = holdUserRrn;
	}

	public String getHoldPwd() {
		return holdPwd;
	}

	public void setHoldPwd(String holdPwd) {
		this.holdPwd = holdPwd;
	}

	public void setPreComClass(String preComClass) {
		this.preComClass = preComClass;
	}

	public String getPreComClass() {
		return preComClass;
	}

	public void setPreState(String preState) {
		this.preState = preState;
	}

	public String getPreState() {
		return preState;
	}

	public void setPreSubState(String preSubState) {
		this.preSubState = preSubState;
	}

	public String getPreSubState() {
		return preSubState;
	}

	public void setPreStateEntryTime(Date preStateEntryTime) {
		this.preStateEntryTime = preStateEntryTime;
	}

	public Date getPreStateEntryTime() {
		return preStateEntryTime;
	}

	public void setActionComment(String actionComment) {
		this.actionComment = actionComment;
	}

	public String getActionComment() {
		return actionComment;
	}

}
