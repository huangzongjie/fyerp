package com.graly.erp.wip.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="LARGE_LOT")
public class LargeLot extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name = "LARGE_LOT_RRN", referencedColumnName = "OBJECT_RRN")
	private List<LargeWipLot> largeWipLots;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LOT_TYPE")
	private String lotType;

	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
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

	public List<LargeWipLot> getLargeWipLots() {
		return largeWipLots;
	}

	public void setLargeWipLots(List<LargeWipLot> largeWipLots) {
		this.largeWipLots = largeWipLots;
	}

}