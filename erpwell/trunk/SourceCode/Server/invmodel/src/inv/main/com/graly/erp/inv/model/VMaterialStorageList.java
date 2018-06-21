package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
public class VMaterialStorageList extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="MATERIAL_CATEGORY2")
	private String materialCategory2;
	
	@Column(name="QTY_MIN")
	private BigDecimal qtyMin;
	
	@Column(name="PACKAGE_SPEC")//包装规格
	private String packageSpec;
	
	@Column(name="M_QTY_ONHAND")
	private BigDecimal mqtyOnhand = BigDecimal.ZERO;//制造车间良品库存
	
	@Column(name="E_QTY_ONHAND")
	private BigDecimal eqtyOnhand = BigDecimal.ZERO;//环保良品库存
	
	@Column(name="QTY_DIFF")
	private BigDecimal qtyDiff = BigDecimal.ZERO;

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

	public String getMaterialCategory2() {
		return materialCategory2;
	}

	public void setMaterialCategory2(String materialCategory2) {
		this.materialCategory2 = materialCategory2;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public String getPackageSpec() {
		return packageSpec;
	}

	public void setPackageSpec(String packageSpec) {
		this.packageSpec = packageSpec;
	}

	public BigDecimal getMqtyOnhand() {
		return mqtyOnhand;
	}

	public void setMqtyOnhand(BigDecimal mqtyOnhand) {
		this.mqtyOnhand = mqtyOnhand;
	}

	public BigDecimal getEqtyOnhand() {
		return eqtyOnhand;
	}

	public void setEqtyOnhand(BigDecimal eqtyOnhand) {
		this.eqtyOnhand = eqtyOnhand;
	}

	public BigDecimal getQtyDiff() {
		return qtyDiff;
	}

	public void setQtyDiff(BigDecimal qtyDiff) {
		this.qtyDiff = qtyDiff;
	}
}
