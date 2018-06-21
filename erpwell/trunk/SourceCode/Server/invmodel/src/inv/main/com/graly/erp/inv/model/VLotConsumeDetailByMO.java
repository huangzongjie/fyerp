package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_LOTCONSUME_DETAIL_BY_MO")
public class VLotConsumeDetailByMO extends ADBase {
	/**
	 * 按工作令统计物料消耗明细
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="DATE_IN")
	private Date dateIn;
	
	@Column(name = "MO_ID")
	private String moId;

	@Column(name = "PM_MATERIAL_ID")
	private String pmMaterialId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "QTY_IN")
	private BigDecimal qtyIn;
	
	@Column(name = "QTY_MOVEMENT")
	private BigDecimal qtyMovement;

	@Column(name = "MATERIAL_ID")
	private String materialId;
	
	@Column(name = "CHILDNAME")
	private String childName;

	@Column(name = "QTY_CONSUME")
	private BigDecimal qtyConsume;

	@Column(name = "CONSUME_PRICE")
	private BigDecimal consumePrice;

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public String getPmMaterialId() {
		return pmMaterialId;
	}

	public void setPmMaterialId(String pmMaterialId) {
		this.pmMaterialId = pmMaterialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public BigDecimal getQtyConsume() {
		return qtyConsume;
	}

	public void setQtyConsume(BigDecimal qtyConsume) {
		this.qtyConsume = qtyConsume;
	}

	public BigDecimal getConsumePrice() {
		return consumePrice;
	}

	public void setConsumePrice(BigDecimal consumePrice) {
		this.consumePrice = consumePrice;
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

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}
}
