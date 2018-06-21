package com.graly.erp.wip.model;

import java.math.BigDecimal;
/**
 * 物料出入库汇总
 */
public class MaterialMoveSum extends MaterialSum {
	
	private static final long serialVersionUID = 1L;
	
	private String materialCategory1;
	
	private String materialCategory2;
	
	private String materialCategory3;
	
	private String materialCategory4;
	
	private Long warehouseRrn;
	
	private String warehouseId;

	private BigDecimal qtyIn;
	
	private BigDecimal qtyOut;
	
	private BigDecimal qtyTotal;

	public String getMaterialCategory1() {
		return materialCategory1;
	}

	public void setMaterialCategory1(String materialCategory1) {
		this.materialCategory1 = materialCategory1;
	}

	public String getMaterialCategory2() {
		return materialCategory2;
	}

	public void setMaterialCategory2(String materialCategory2) {
		this.materialCategory2 = materialCategory2;
	}

	public String getMaterialCategory3() {
		return materialCategory3;
	}

	public void setMaterialCategory3(String materialCategory3) {
		this.materialCategory3 = materialCategory3;
	}

	public String getMaterialCategory4() {
		return materialCategory4;
	}

	public void setMaterialCategory4(String materialCategory4) {
		this.materialCategory4 = materialCategory4;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public BigDecimal getQtyOut() {
		return qtyOut;
	}

	public void setQtyOut(BigDecimal qtyOut) {
		this.qtyOut = qtyOut;
	}

	public BigDecimal getQtyTotal() {
		return qtyTotal;
	}

	public void setQtyTotal(BigDecimal qtyTotal) {
		this.qtyTotal = qtyTotal;
	}
}
