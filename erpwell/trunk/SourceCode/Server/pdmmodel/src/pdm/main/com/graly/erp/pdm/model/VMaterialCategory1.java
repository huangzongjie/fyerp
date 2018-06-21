package com.graly.erp.pdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_PDM_MATERIAL_CATEGORY1")
public class VMaterialCategory1 extends ADBase {

	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_CATEGORY1")
	private String materialCategory1;

	public String getMaterialCategory1() {
		return materialCategory1;
	}

	public void setMaterialCategory1(String materialCategory1) {
		this.materialCategory1 = materialCategory1;
	}

}
