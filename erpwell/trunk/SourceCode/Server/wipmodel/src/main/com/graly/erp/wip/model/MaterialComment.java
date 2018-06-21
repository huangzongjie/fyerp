package com.graly.erp.wip.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
//3+3外购件物料备注
@Entity
@Table(name="PDM_MATERIAL_COMMENT")
public class MaterialComment extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="COMMENTS")
	private String comments;
	
	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
