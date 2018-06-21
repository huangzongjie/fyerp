package com.graly.mes.wip.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_LOT_PARAMETER")
public class LotParameter extends ADBase{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="VARIABLE_NAME")
	private String variableName = null;
	
	@Column(name="ACCESS_TYPE")
	protected String access = null;
	
	@Column(name="MAPPED_NAME")
	protected String mappedName = null;
	
	@Column(name="DATA_TYPE")
	private String type = null;
	
	@Column(name="DEFAULT_VALUE")
	private String defaultValue = null;

	@Column(name="SEQ_NO")
	private Integer seqNo;

	public LotParameter() {
	}

	public LotParameter(String variableName, String access,
			String mappedName, String defaultValue) {
		this.setVariableName(variableName);
		if (access != null)
			access = access.toLowerCase();
		this.access = access;
		this.mappedName = mappedName;
		this.defaultValue = defaultValue;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getVariableName() {
		return variableName;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public Integer getSeqNo() {
		return seqNo;
	}

}
