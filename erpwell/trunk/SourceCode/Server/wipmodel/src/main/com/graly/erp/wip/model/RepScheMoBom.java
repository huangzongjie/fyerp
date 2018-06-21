package com.graly.erp.wip.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="REP_SCHE_MO_MATERIAL_BOM")
@SequenceGenerator(name="SEQ_REP_SCHE_RRN", sequenceName="REP_SCHE_RRN", allocationSize=1)
public class RepScheMoBom implements Serializable {//报表--外购件控制排程表
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_REP_SCHE_RRN")
	@Column(name="OBJECT_RRN")
	private Long objectRrn;
	
	@Column(name="ROOT_RRN")
	private Long rootRrn;
	
	@Column(name="MATERIAL_PARENT_RRN")
	private Long materialParentRrn;
	
	@Column(name="MATERIAL_PARENT_VERSION")
	private Long materialParentVersion;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long materialChildRrn;
	
	@Column(name="QTY_UNIT")
	private BigDecimal qtyUnit;
	
	@Column(name="QTY_UNIT_TOTAL")
	private String qtyUnitTotal;

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}

	public Long getRootRrn() {
		return rootRrn;
	}

	public void setRootRrn(Long rootRrn) {
		this.rootRrn = rootRrn;
	}

	public Long getMaterialParentRrn() {
		return materialParentRrn;
	}

	public void setMaterialParentRrn(Long materialParentRrn) {
		this.materialParentRrn = materialParentRrn;
	}

	public Long getMaterialParentVersion() {
		return materialParentVersion;
	}

	public void setMaterialParentVersion(Long materialParentVersion) {
		this.materialParentVersion = materialParentVersion;
	}

	public Long getMaterialChildRrn() {
		return materialChildRrn;
	}

	public void setMaterialChildRrn(Long materialChildRrn) {
		this.materialChildRrn = materialChildRrn;
	}

	public BigDecimal getQtyUnit() {
		return qtyUnit;
	}

	public void setQtyUnit(BigDecimal qtyUnit) {
		this.qtyUnit = qtyUnit;
	}

	public String getQtyUnitTotal() {
		return qtyUnitTotal;
	}

	public void setQtyUnitTotal(String qtyUnitTotal) {
		this.qtyUnitTotal = qtyUnitTotal;
	}
}
