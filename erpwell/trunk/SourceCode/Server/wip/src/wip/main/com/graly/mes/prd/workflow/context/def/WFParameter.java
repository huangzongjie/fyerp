package com.graly.mes.prd.workflow.context.def;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

/**
 * specifies access to a variable. Variable access is used in 3 situations: 1)
 * process 2) process-state 3) script 4) task controllers
 */

@Entity
@Table(name="WF_PARAMETER")
public class WFParameter extends ADBase {

	private static final long serialVersionUID = 1L;

	@Column(name="VARIABLE_NAME")
	protected String variableName = null;
	
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

	// constructors
	// /////////////////////////////////////////////////////////////

	public WFParameter() {
	}

	public WFParameter(String variableName, String access,
			String mappedName, String defaultValue) {
		this.variableName = variableName;
		if (access != null)
			access = access.toLowerCase();
		this.access = access;
		this.mappedName = mappedName;
		this.defaultValue = defaultValue;
	}

	// getters and setters
	// //////////////////////////////////////////////////////

	/**
	 * the mapped name. The mappedName defaults to the variableName in case no
	 * mapped name is specified.
	 */
	public String getMappedName() {
		if (mappedName == null) {
			return variableName;
		}
		return mappedName;
	}

	/**
	 * specifies a comma separated list of access literals {read, write,
	 * required}.
	 */
	public Access getAccess() {
		return new Access(access);
	}

	public String getVariableName() {
		return variableName;
	}

	public boolean isReadable() {
		return getAccess().isReadable();
	}

	public boolean isWritable() {
		return getAccess().isWritable();
	}

	public boolean isRequired() {
		return getAccess().isRequired();
	}

	public boolean isLock() {
		return getAccess().isLock();
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
