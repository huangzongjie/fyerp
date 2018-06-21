package com.graly.erp.ppm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="V_MPSLINE_DELIVERY")
public class VMpsLineDelivery extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MPS_ID")
	private String mpsId;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="TOTAL_LINE")
	private Long totalLine = 0L;
	  
	 
	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Long getTotalLine() {
		return totalLine;
	}

	public void setTotalLine(Long totalLine) {
		this.totalLine = totalLine;
	}
}
