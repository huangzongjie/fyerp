package com.graly.erp.inv.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.erp.base.model.Storage;
import com.graly.framework.activeentity.model.ADUpdatable;

/**
 * ’À¡‰ø‚¥Ê±Ì
 * */

public class MaterialAging implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long objectRrn;
	
	private String materialId;

	private String materialName;
	
	private BigDecimal qtyIn;
	
	private BigDecimal qtyOnhand;
	
	private BigDecimal qtyAging;

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
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

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public BigDecimal getQtyOnhand() {
		return qtyOnhand;
	}

	public void setQtyOnhand(BigDecimal qtyOnhand) {
		this.qtyOnhand = qtyOnhand;
	}

	public BigDecimal getQtyAging() {
		return qtyAging;
	}

	public void setQtyAging(BigDecimal qtyAging) {
		this.qtyAging = qtyAging;
	}
	
}
