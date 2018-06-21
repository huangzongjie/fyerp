package com.graly.erp.wip.model;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="REP_MONTH_WORKHOURS_MO")
public class WorkHoursMo extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name = "REPORT_MONTH")
	private Date reportMonth;

	@Column(name = "MO_ID")
	private String moId;

	@Column(name = "QTY_MO_PRODUCT")
	private BigDecimal qtyMoProduct;

	@Column(name = "QTY_MO_IN")
	private BigDecimal qtyMoIn;

	@Column(name = "WORKCENTER_ID")
	private String workcenterId;

	@Column(name = "MATERIAL_RRN")
	private Long materialRrn;

	@Column(name = "MATERIAL_ID")
	private String materialId;

	@Column(name = "MATERIAL_NAME")
	private String materialName;

	@Column(name = "UOM_ID")
	private String uomId;

	@Column(name = "STAND_TIME")
	private BigDecimal standTime;

	@Column(name = "QTY_SUBMO_PRODUCT")
	private BigDecimal qtySubmoProduct;

	@Column(name = "WORK_HOURS")
	private BigDecimal workHours;

	public Date getReportMonth() {
		return reportMonth;
	}

	public void setReportMonth(Date reportMonth) {
		this.reportMonth = reportMonth;
	}

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public BigDecimal getQtyMoProduct() {
		return qtyMoProduct;
	}

	public void setQtyMoProduct(BigDecimal qtyMoProduct) {
		this.qtyMoProduct = qtyMoProduct;
	}

	public BigDecimal getQtyMoIn() {
		return qtyMoIn;
	}

	public void setQtyMoIn(BigDecimal qtyMoIn) {
		this.qtyMoIn = qtyMoIn;
	}

	public String getWorkcenterId() {
		return workcenterId;
	}

	public void setWorkcenterId(String workcenterId) {
		this.workcenterId = workcenterId;
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

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public BigDecimal getStandTime() {
		return standTime;
	}

	public void setStandTime(BigDecimal standTime) {
		this.standTime = standTime;
	}

	public BigDecimal getQtySubmoProduct() {
		return qtySubmoProduct;
	}

	public void setQtySubmoProduct(BigDecimal qtySubmoProduct) {
		this.qtySubmoProduct = qtySubmoProduct;
	}

	public BigDecimal getWorkHours() {
		return workHours;
	}

	public void setWorkHours(BigDecimal workHours) {
		this.workHours = workHours;
	}
}
