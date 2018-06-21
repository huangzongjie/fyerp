package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_LOTCONSUME_SUM_BY_MO")
public class VLotConsumeSumByMO extends ADBase {
	private static final long serialVersionUID = 1L;
	
	@Column(name="DATE_IN")
	private Date dateIn;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;

	@Column(name="SUM_QTY_CONSUME")
	private BigDecimal sumQtyConsume;

	@Column(name="SUM_SUMNUM")
	private BigDecimal sumSumNum;

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public BigDecimal getSumQtyConsume() {
		return sumQtyConsume;
	}

	public void setSumQtyConsume(BigDecimal sumQtyConsume) {
		this.sumQtyConsume = sumQtyConsume;
	}

	public BigDecimal getSumSumNum() {
		return sumSumNum;
	}

	public void setSumSumNum(BigDecimal sumSumNum) {
		this.sumSumNum = sumSumNum;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public Date getDateIn() {
		return dateIn;
	}

	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
