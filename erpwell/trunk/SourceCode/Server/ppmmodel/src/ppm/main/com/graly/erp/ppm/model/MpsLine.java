package com.graly.erp.ppm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name = "PPM_MPS_LINE")
public class MpsLine extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name = "MPS_ID")
	private String mpsId;

	@Column(name = "QTY_SALEPLAN")
	private BigDecimal qtySalePlan;
	
	@Column(name = "QTY_AVALIABLE")
	private BigDecimal qtyAvailable;
	
	@Column(name = "QTY_MPS")
	private BigDecimal qtyMps;
	
	@Column(name = "MATERIAL_RRN")
	private Long materialRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;
	
	@Column(name = "UOM_ID")
	private String uomId;

	@Column(name = "DATE_DELIVERED")
	private Date dateDelivered;
	
	@Column(name = "IS_GENERATE")
	private String isGenerate;
	
	@Column(name = "PRIORITY")
	private Long priority;
	
	@Column(name="QTY_HANDON")
	private BigDecimal qtyHandOn;
	
	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit;
	
	@Column(name = "QTY_LADING")
	private BigDecimal qtyLading;
	
	@Column(name="QTY_MIN")
	private BigDecimal qtyMin;

	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation;
	
	@Column(name="ORDER_ID")//订单编号
	private String orderId;
	
	@Column(name="TEMP_QTY")
	private BigDecimal temporaryQty = BigDecimal.ZERO;
	
	@Column(name="INTERNAL_ORDER_QTY")
	private BigDecimal internalOrderQty = BigDecimal.ZERO;
	
	@Column(name = "LAST_QTY_MPS")
	private BigDecimal lastQtyMps = BigDecimal.ZERO ;//上一月该物料主计划生产数量
	
	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public void setQtySalePlan(BigDecimal qtySalePlan) {
		this.qtySalePlan = qtySalePlan;
	}

	public BigDecimal getQtySalePlan() {
		return qtySalePlan;
	}

	public void setQtyAvailable(BigDecimal qtyAvailable) {
		this.qtyAvailable = qtyAvailable;
	}

	public BigDecimal getQtyAvailable() {
		return qtyAvailable;
	}
	
	public BigDecimal getQtyMps() {
		return qtyMps;
	}

	public void setQtyMps(BigDecimal qtyMps) {
		this.qtyMps = qtyMps;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}
	
	public String getMaterialId() {
		if (material != null) {
			return material.getMaterialId();
		}
		return "";
	}

	public String getMaterialName() {
		if (material != null) {
			return material.getName();
		}
		return "";
	}
	
	public String getUomId() {
		if(material != null){
			return material.getInventoryUom();
		}
		return "";
	}

	public BigDecimal getRefernectPrice() {
		if (material != null) {
			return material.getRefernectPrice();
		}
		return null;
	}
	
	public BigDecimal getStandTime() {
		if (material != null) {
			return material.getStandTime();
		}
		return null;
	}
	
	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	
	public Date getDateDelivered() {
		return dateDelivered;
	}

	public void setDateDelivered(Date dateDelivered) {
		this.dateDelivered = dateDelivered;
	}

	public Boolean getIsGenerate() {
		return "Y".equals(this.isGenerate);
	}

	public void setIsGenerate(Boolean isGenerate) {
		this.isGenerate = isGenerate ? "Y" : "N";
	}
	
	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public void setQtyHandOn(BigDecimal qtyHandOn) {
		this.qtyHandOn = qtyHandOn;
	}

	public BigDecimal getQtyHandOn() {
		return qtyHandOn;
	}

	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}

	public void setQtyLading(BigDecimal qtyLading) {
		this.qtyLading = qtyLading;
	}

	public BigDecimal getQtyLading() {
		return qtyLading;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}

	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getTemporaryQty() {
		return temporaryQty==null?BigDecimal.ZERO:temporaryQty;
	}

	public void setTemporaryQty(BigDecimal temporaryQty) {
		this.temporaryQty = temporaryQty;
	}

	public BigDecimal getInternalOrderQty() {
		return internalOrderQty==null?BigDecimal.ZERO:internalOrderQty;
	}

	public void setInternalOrderQty(BigDecimal internalOrderQty) {
		this.internalOrderQty = internalOrderQty;
	}

	public BigDecimal getLastQtyMps() {
		return lastQtyMps;
	}

	public void setLastQtyMps(BigDecimal lastQtyMps) {
		this.lastQtyMps = lastQtyMps;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	
}
