package com.graly.erp.pdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="PDM_MATERIAL_UNSELECTED")
public class MaterialUnSelected extends ADBase {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_UNSELECTED_RRN")
	private Long unSelectedRrn;

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public void setUnSelectedRrn(Long unSelectedRrn) {
		this.unSelectedRrn = unSelectedRrn;
	}

	public Long getUnSelectedRrn() {
		return unSelectedRrn;
	}

}
