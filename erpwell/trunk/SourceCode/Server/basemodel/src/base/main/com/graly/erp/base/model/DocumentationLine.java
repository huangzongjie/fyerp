package com.graly.erp.base.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;

@javax.persistence.MappedSuperclass
public class DocumentationLine extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static final String LINESTATUS_DRAFTED = "DRAFTED";
	public static final String LINESTATUS_COMPLETED = "COMPLETED";
	public static final String LINESTATUS_APPROVED = "APPROVED";
	public static final String LINESTATUS_INVALID = "INVALID";
	public static final String LINESTATUS_REJECTED = "REJECTED";
	public static final String LINESTATUS_CLOSED = "CLOSED";
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;

	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY")
	private BigDecimal qty = new BigDecimal("0.0");
	
	@Column(name="DATE_START")
	private Date dateStart;
	
	@Transient
	private  Date timeStart;
	
	@Column(name="DATE_END")
	private Date dateEnd;
	
	@Transient
	private Date timeEnd;
	
	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice = new BigDecimal("0.0");
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal = new BigDecimal("0.0");
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;


	public Long getLineNo() {
		return lineNo;
	}

	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	
	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public Date getDateStart() {
		return dateStart;
	}
	
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}
	
	public Date getDateEnd() {
		return dateEnd;
	}
	
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String getMaterialName() {
		if (this.getMaterial() != null) {
			return this.getMaterial().getName();
		}
		return "";
	}
	
	public String getMaterialId() {
		if (this.getMaterial() != null) {
			return this.getMaterial().getMaterialId();
		}
		return "";
	}

	public String getLotType() {
		if (this.getMaterial() != null) {
			return this.getMaterial().getLotType();
		}
		return "";
	}

	public Date getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}

	public Date getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}
	
	public BigDecimal getStandTime() {
		if(material != null) {
			return material.getStandTime();
		}
		return null;
	}

}
