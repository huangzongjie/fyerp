package com.graly.promisone.activeentity.model;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
 
@MappedSuperclass
public abstract class ADBase implements Serializable {
	
	private static final long serialVersionUID = 3817281931354683816L;
	public static final String BASE_CONDITION = " isActive = 'Y' AND (orgId = ? OR orgId = 0) "; 
		
	@Id
	@SequenceGenerator(name = "OBJECT_SEQ", sequenceName="Id", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBJECT_SEQ")
	@Column(name="OBJECT_ID")
	protected Long objectId;
	 
	@Column(name="AD_ORG_ID")
	protected Long orgId = 0L;
	
	@Column(name="IS_ACTIVE")
	protected String isActive;
	

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setOrgId(Long orgId) {
		if (orgId == null){
			this.orgId = 0L;
		} else {
			this.orgId = orgId;
		}
	} 

	public Long getOrgId() {
		return orgId;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive ? "Y" : "N";
	}
	
	public Boolean getIsActive(){
		return "Y".equalsIgnoreCase(this.isActive) ? true : false; 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ADBase)) return false;
		ADBase o = (ADBase) obj;
		if (this.objectId == null) return false;
		return this.objectId.equals(o.objectId);
	}
}
