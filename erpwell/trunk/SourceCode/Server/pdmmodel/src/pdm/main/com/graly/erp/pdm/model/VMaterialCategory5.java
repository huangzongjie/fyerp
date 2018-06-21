package com.graly.erp.pdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_PDM_MATERIAL_CATEGORY5")
public class VMaterialCategory5 extends ADBase {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_CATEGORY5")
	private String materialCategory5;

	public String getMaterialCategory5() {
		return materialCategory5;
	}

	public void setMaterialCategory5(String materialCategory5) {
		this.materialCategory5 = materialCategory5;
	}
}
