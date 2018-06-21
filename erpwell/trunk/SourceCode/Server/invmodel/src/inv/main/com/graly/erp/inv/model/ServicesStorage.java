package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="INV_SERVICES_STORAGE")
public class ServicesStorage extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="QTY_ONHAND")
	private BigDecimal qtyOnhand = BigDecimal.ZERO;
	
	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public BigDecimal getQtyOnhand() {
		return qtyOnhand;
	}

	public void setQtyOnhand(BigDecimal qtyOnhand) {
		this.qtyOnhand = qtyOnhand;
	}
}
