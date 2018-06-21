package com.graly.erp.pdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_PDM_MATERIAL_CATEGORY6")
public class VMaterialCategory6 extends ADBase {
private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_CATEGORY5")
	private String materialCategory5;
	
	@Column(name="MATERIAL_CATEGORY6")
	private String materialCategory6;

	public String getMaterialCategory5() {
		return materialCategory5;
	}

	public void setMaterialCategory5(String materialCategory5) {
		this.materialCategory5 = materialCategory5;
	}

	public String getMaterialCategory6() {
		return materialCategory6;
	}

	public void setMaterialCategory6(String materialCategory6) {
		this.materialCategory6 = materialCategory6;
	}
}
