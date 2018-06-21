package com.graly.erp.base.model;

import javax.persistence.Column;

import com.graly.framework.activeentity.model.ADUpdatable;

@javax.persistence.MappedSuperclass
public class Documentation extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String ACTION_COMPLETE = "CO";
	public static final String ACTION_APPROVE = "AP";
	public static final String ACTION_REJECT = "RJ";
	public static final String ACTION_CLOSE = "CL";
	
	public static final String STATUS_DRAFTED = "DRAFTED";
	public static final String STATUS_COMPLETED = "COMPLETED";
	public static final String STATUS_APPROVED = "APPROVED";
	public static final String STATUS_INVALID = "INVALID";
	public static final String STATUS_REJECTED = "REJECTED";
	public static final String STATUS_CLOSED = "CLOSED";
	public static final String STATUS_MERGED = "MERGED";
//	public static final String STATUS_MERGED = "MERGED";
	public static final String STATUS_PREPARE = "PREPARE";
	
	public static final String DOCTYPE_MPR = "MPR"; 
	public static final String DOCTYPE_TPR = "TPR"; //采购申请
	public static final String DOCTYPE_MPO = "MPO";
	public static final String DOCTYPE_TPO = "TPO"; //采购订单
	public static final String DOCTYPE_REC = "REC"; //收货单
	public static final String DOCTYPE_IQC = "IQC"; //检验单
	public static final String DOCTYPE_PIN = "PIN"; //采购入库
	public static final String DOCTYPE_WIN = "WIN"; //生产入库
	public static final String DOCTYPE_OIN = "OIN"; //其它入库
	public static final String DOCTYPE_RIN = "RIN"; //退货入库
	public static final String DOCTYPE_TRF = "TRF"; //调拨单
	public static final String DOCTYPE_SOU = "SOU"; //销售出库
	public static final String DOCTYPE_OOU = "OOU"; //其它出库
	public static final String DOCTYPE_AOU = "AOU"; //财务调整
	public static final String DOCTYPE_DOU = "DOU"; //研发用料出库
	public static final String DOCTYPE_ADOU = "ADOU"; //营运调整出库
	public static final String DOCTYPE_ADIN = "ADIN"; //营运调整入库
	public static final String DOCTYPE_PMO = "PMO"; //计划工作令
	public static final String DOCTYPE_AMO = "AMO"; //临时工作令
	public static final String DOCTYPE_BMO = "BMO"; //备货工作令 优先级低
	
	public static final String DOCTYPE_MWO = "MWO";
	
	@Column(name="DOC_ID")
	private String docId;
	
	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_STATUS")
	private String docStatus = STATUS_DRAFTED;

	@Column(name="IS_APPROVED")
	private String isApproved;

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocId() {
		return docId;
	}
	
	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getDocStatus() {
		return docStatus;
	}
	
	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved ? "Y" : "N";
	}
	
	public Boolean getIsApproved(){
		return "Y".equalsIgnoreCase(this.isApproved) ? true : false; 
	}
	
	public boolean approve() {
		this.setIsApproved(true);
		this.setDocStatus(STATUS_APPROVED);
		return true;
	}
	
	public boolean reject() {
		this.setIsApproved(false);
		this.setDocStatus(STATUS_REJECTED);
		return true;
	}
}