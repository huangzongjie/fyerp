package com.graly.erp.wip.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_WCT_STORAGE")
public class WCTMaterialStorage extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;

	@Column(name="WORKCENTER_RRN")
	private Long workcenterRrn;

	@Column(name="WORKCENTER_NAME")
	private String workcenterName;

	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;

	@Column(name="LOT_RRN")
	private Long lotRrn;

	@Column(name="LOT_ID")
	private String lotId;

	@Column(name="QTY")
	private BigDecimal qty;

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public Long getWorkcenterRrn() {
		return workcenterRrn;
	}

	public void setWorkcenterRrn(Long workcenterRrn) {
		this.workcenterRrn = workcenterRrn;
	}

	public String getWorkcenterName() {
		return workcenterName;
	}

	public void setWorkcenterName(String workcenterName) {
		this.workcenterName = workcenterName;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
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

	public Long getLotRrn() {
		return lotRrn;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

}
