package com.graly.erp.spread.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;

/**
 * 物料流转记录(物料交割、物料报废)
 * @author Denny
 *
 */
@Entity
@Table(name="INV_MATERIAL_SPREAD")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="SPREAD_TYPE", discriminatorType=DiscriminatorType.STRING)
public class MaterialSpread extends ADUpdatable {
	private static final long	serialVersionUID	= 1L;
	
	@Column(name="MATERIAL_RRN")
	protected Long materialRrn;
	
	@Transient
	protected String materialId;

	@Transient
	protected String materialName;
	
	@ManyToOne
	@JoinColumn(name="MATERIAL_RRN", insertable=false, updatable=false)
	protected Material material;
	
	@Column(name="WORKCENTER")
	protected String workCenter;
	
	@Column(name="QTY")
	protected BigDecimal qty;

	public String getWorkCenter() {
		return workCenter;
	}

	public void setWorkCenter(String workCenter) {
		this.workCenter = workCenter;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialId() {
		if(material == null){
			materialId = "";
		}else{
			materialId = material.getMaterialId();
		}
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialName() {
		if(material == null){
			materialName = "";
		}else {
			materialName = material.getName();
		}
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

}
