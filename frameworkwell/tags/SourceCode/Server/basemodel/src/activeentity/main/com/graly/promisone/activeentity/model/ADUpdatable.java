package com.graly.promisone.activeentity.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.persistence.MappedSuperclass;
 
@MappedSuperclass
public abstract class ADUpdatable extends ADBase {
	
	@Column(name="CREATED")
	protected Date created;
	
	@Column(name="CREATED_BY")
	protected Long createdBy;
	
	@Version
	@Column(name="UPDATED")
	protected Date updated;
	
	@Column(name="UPDATED_BY")
	protected Long updatedBy;
	
	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getCreatedBy() {
		return createdBy;
	}
	
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}
}
