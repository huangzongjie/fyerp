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
@Table(name = "PPM_SALEPLAN_LINE")
public class SalePlanLine extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "MPS_ID")
	private String mpsId;

	@Column(name = "SALEPLAN_TYPE")
	private String salePlanType;

	@Column(name = "CUSTOMER")
	private String customer;
	
	@Column(name = "MATERIAL_RRN")
	private Long materialRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;

	@Column(name = "UOM_ID")
	private String uomId;
	
	@Column(name = "QTY_SALEPLAN")
	private BigDecimal qtySalePlan;
	
	@Column(name = "DATE_DELIVERED")
	private Date dateDelivered;
	
	@Column(name = "IS_GENERATE")
	private String isGenerate;
	
	@Column(name = "QTY_LADING")
	private BigDecimal qtyLading;
	
	@Column(name="ORDER_ID")//¶©µ¥±àºÅ
	private String orderId;
	
	@Column(name = "COMMENTS")
	private String comments;
	
	@Column(name="MATERIAL_CATEGORY1")
	private String materialCategory1;
	
	@Column(name="MATERIAL_CATEGORY2")
	private String materialCategory2;
	
	@Column(name="MATERIAL_CATEGORY3")
	private String materialCategory3;
	
	
	public BigDecimal getQtyLading() {
		return qtyLading;
	}

	public void setQtyLading(BigDecimal qtyLading) {
		this.qtyLading = qtyLading;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public String getSalePlanType() {
		return salePlanType;
	}

	public void setSalePlanType(String salePlanType) {
		this.salePlanType = salePlanType;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
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

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	
	public BigDecimal getQtySalePlan() {
		return qtySalePlan;
	}

	public void setQtySalePlan(BigDecimal qtySalePlan) {
		this.qtySalePlan = qtySalePlan;
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

	public void setState(Boolean isGenerate) {
		this.isGenerate = isGenerate ? "Y" : "N";
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

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
}
