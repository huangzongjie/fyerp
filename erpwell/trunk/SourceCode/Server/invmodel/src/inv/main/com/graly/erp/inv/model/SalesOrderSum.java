package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="SAL_SO")
public class SalesOrderSum extends ADBase {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="QTY_SO")
	private BigDecimal qtySo;

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setQtySo(BigDecimal qtySo) {
		this.qtySo = qtySo;
	}

	public BigDecimal getQtySo() {
		return qtySo;
	}
	
}
