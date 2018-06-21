package com.graly.promisone.activeentity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="V_USER_REF_LIST_NAME")
public class VUserRefListName extends ADBase {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="REFERENCE_NAME")
	private String referenceName;

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getReferenceName() {
		return referenceName;
	}
	
	

}
