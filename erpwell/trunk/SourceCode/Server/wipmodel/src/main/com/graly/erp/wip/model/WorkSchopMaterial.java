package com.graly.erp.wip.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADUpdatable;
//车间仓库第一阶段管控物料
@Entity
@Table(name="WORK_SHOP_MATERIAL")
public class WorkSchopMaterial extends ADUpdatable  {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}
}
