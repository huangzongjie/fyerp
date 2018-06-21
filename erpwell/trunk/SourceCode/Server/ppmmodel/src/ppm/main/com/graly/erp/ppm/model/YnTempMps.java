package com.graly.erp.ppm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="YN_TEMP_MPS")//原能临时计划运算实体
public class YnTempMps extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name = "QTY_PRL")
	private BigDecimal qtyPrl;
	
	@Column(name = "QTY_PPL")
	private BigDecimal qtyPpl;
	
	@Column(name = "QTY_ONHAND")
	private BigDecimal qtyOnhand;
	
	@Column(name = "QTY_MIN")
	private BigDecimal qtyMin;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;
	
	@Column(name="VENDOR_ID")
	private String vendorId;

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getQtyPrl() {
		return qtyPrl;
	}

	public void setQtyPrl(BigDecimal qtyPrl) {
		this.qtyPrl = qtyPrl;
	}

	public BigDecimal getQtyPpl() {
		return qtyPpl;
	}

	public void setQtyPpl(BigDecimal qtyPpl) {
		this.qtyPpl = qtyPpl;
	}

	public BigDecimal getQtyOnhand() {
		return qtyOnhand;
	}

	public void setQtyOnhand(BigDecimal qtyOnhand) {
		this.qtyOnhand = qtyOnhand;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
}
