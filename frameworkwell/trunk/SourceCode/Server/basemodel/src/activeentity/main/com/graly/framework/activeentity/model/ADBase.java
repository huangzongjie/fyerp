package com.graly.framework.activeentity.model;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
 
@MappedSuperclass
public abstract class ADBase implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 3817281931354683816L;
	public static final String BASE_CONDITION = " isActive = 'Y' AND (orgRrn = ? OR orgRrn = 0) ";
	public static final String SQL_BASE_CONDITION = " IS_ACTIVE = 'Y' AND (ORG_RRN = ? OR ORG_RRN = 0) ";
		
	@Id
	@SequenceGenerator(name = "OBJECT_RRN", sequenceName="OBJECT_RRN", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBJECT_RRN")
	@Column(name="OBJECT_RRN")
	protected Long objectRrn;
	 
	@Column(name="ORG_RRN")
	protected Long orgRrn = 0L;
	
	@Column(name="IS_ACTIVE")
	protected String isActive;
	
	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setOrgRrn(Long orgRrn) {
		if (orgRrn == null){
			this.orgRrn = 0L;
		} else {
			this.orgRrn = orgRrn;
		}
	} 

	public Long getOrgRrn() {
		return orgRrn;
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
		if (this.getObjectRrn() == null) return super.equals(obj);  
		if (!(obj instanceof ADBase)) return false;
		ADBase o = (ADBase) obj;
		return this.getObjectRrn().equals(o.getObjectRrn());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ADBase base = (ADBase) super.clone();
		base.setObjectRrn(null);
		return base;
	}

}
