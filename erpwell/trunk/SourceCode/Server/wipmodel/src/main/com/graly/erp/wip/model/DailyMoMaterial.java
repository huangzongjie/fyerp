package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="DAILY_MO_MATERIAL")
public class DailyMoMaterial extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="MO_LINE_RRN")
	private Long moLineRrn;

	@Column(name="MO_RRN")
	private Long moRrn;

	@Column(name="WORKCENTER_RRN")
	private Long workcenterRrn;
	
	@ManyToOne
	@JoinColumn(name="MATERIAL_RRN",referencedColumnName="OBJECT_RRN",insertable=false,updatable=false)
	private Material material;

	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="QTY")
	private BigDecimal qty;

	@Column(name="QTY_RECEIVE")
	private BigDecimal qtyReceive;

	@Column(name="STAND_TIME")
	private BigDecimal standTime;

	@Column(name="TOTAL_TIME")
	private BigDecimal totalTime;

	@Column(name="WORK_STATUS")
	private String workStatus;

	@Column(name="DATE_PLAN_START")
	private Date datePlanStart;

	@Column(name="DATE_PLAN_END")
	private Date datePlanEnd;

	@Column(name="AVAILABLE_DAY")
	private Long availableDay;

	@Column(name="DAY_NO")
	private Long dayNo;

	@Column(name="CURR_DATE")
	private Date currDate;

	@Column(name="MANPOWER")
	private Long manpower;

	@Column(name="CURR_DAY_POWER")
	private BigDecimal currDayPower;

	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}

	public Long getWorkcenterRrn() {
		return workcenterRrn;
	}

	public void setWorkcenterRrn(Long workcenterRrn) {
		this.workcenterRrn = workcenterRrn;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public BigDecimal getQtyReceive() {
		return qtyReceive;
	}

	public void setQtyReceive(BigDecimal qtyReceive) {
		this.qtyReceive = qtyReceive;
	}

	public BigDecimal getStandTime() {
		return standTime;
	}

	public void setStandTime(BigDecimal standTime) {
		this.standTime = standTime;
	}

	public BigDecimal getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(BigDecimal totalTime) {
		this.totalTime = totalTime;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public Date getDatePlanStart() {
		return datePlanStart;
	}

	public void setDatePlanStart(Date datePlanStart) {
		this.datePlanStart = datePlanStart;
	}

	public Date getDatePlanEnd() {
		return datePlanEnd;
	}

	public void setDatePlanEnd(Date datePlanEnd) {
		this.datePlanEnd = datePlanEnd;
	}

	public Long getAvailableDay() {
		return availableDay;
	}

	public void setAvailableDay(Long availableDay) {
		this.availableDay = availableDay;
	}

	public Long getDayNo() {
		return dayNo;
	}

	public void setDayNo(Long dayNo) {
		this.dayNo = dayNo;
	}

	public Date getCurrDate() {
		return currDate;
	}

	public void setCurrDate(Date currDate) {
		this.currDate = currDate;
	}

	public Long getManpower() {
		return manpower;
	}

	public void setManpower(Long manpower) {
		this.manpower = manpower;
	}

	public BigDecimal getCurrDayPower() {
		return currDayPower;
	}

	public void setCurrDayPower(BigDecimal currDayPower) {
		this.currDayPower = currDayPower;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
}
