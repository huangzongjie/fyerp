package com.graly.mes.prd.workflow.bytes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="WF_BYTEBLOCK")
public class ByteBlock extends ADBase {

	private static final long serialVersionUID = 1L;
	
	@Column(name="PROCESS_FILE_RRN")
	private Long processFileId;
	
	@Column(name="BYTES")
	private byte[] bytes;
	
	@Column(name="SEQ_NO")
	private Long seqNo;

	public void setProcessFileId(Long processFileId) {
		this.processFileId = processFileId;
	}

	public Long getProcessFileId() {
		return processFileId;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getSeqNo() {
		return seqNo;
	}

}
