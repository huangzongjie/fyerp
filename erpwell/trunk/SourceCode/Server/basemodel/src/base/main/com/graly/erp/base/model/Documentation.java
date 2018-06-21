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
	public static final String DOCTYPE_TPR = "TPR"; //�ɹ�����
	public static final String DOCTYPE_MPO = "MPO";
	public static final String DOCTYPE_TPO = "TPO"; //�ɹ�����
	public static final String DOCTYPE_REC = "REC"; //�ջ���
	public static final String DOCTYPE_IQC = "IQC"; //���鵥
	public static final String DOCTYPE_PIN = "PIN"; //�ɹ����
	public static final String DOCTYPE_WIN = "WIN"; //�������
	public static final String DOCTYPE_OIN = "OIN"; //�������
	public static final String DOCTYPE_RIN = "RIN"; //�˻����
	public static final String DOCTYPE_TRF = "TRF"; //������
	public static final String DOCTYPE_SOU = "SOU"; //���۳���
	public static final String DOCTYPE_OOU = "OOU"; //��������
	public static final String DOCTYPE_AOU = "AOU"; //�������
	public static final String DOCTYPE_DOU = "DOU"; //�з����ϳ���
	public static final String DOCTYPE_ADOU = "ADOU"; //Ӫ�˵�������
	public static final String DOCTYPE_ADIN = "ADIN"; //Ӫ�˵������
	public static final String DOCTYPE_PMO = "PMO"; //�ƻ�������
	public static final String DOCTYPE_AMO = "AMO"; //��ʱ������
	public static final String DOCTYPE_BMO = "BMO"; //���������� ���ȼ���
	
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