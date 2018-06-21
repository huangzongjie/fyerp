package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_STORAGE_MATERIAL")
public class VStorageMaterial extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="QTY_MIN")
	private BigDecimal qtyMin;
	
	@Column(name="QTY_MAX")
	private BigDecimal qtyMax;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Column(name="QTY_ONHAND")
	private BigDecimal qtyOnhand;
	
	@Column(name="QTY_DIFF")
	private BigDecimal qtyDiff;
	
	@Column(name="QTY_WRITE_OFF")
	private BigDecimal qtyWriteOff;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="MATERIAL_TYPE")
	private String materialType;
	
	@Column(name="ABC_CATEGORY")
	private String abcCategory;
	
	@Column(name="MATERIAL_CATEGORY1")
	private String materialCategory1;
	
	@Column(name="MATERIAL_CATEGORY2")
	private String materialCategory2;
	
	@Column(name="MATERIAL_CATEGORY3")
	private String materialCategory3;
	
	@Column(name="MATERIAL_CATEGORY4")
	private String materialCategory4;
	
	@Column(name="INVENTORY_UOM")
	private String inventoryUom;
	
	@Column(name="PURCHASE_UOM")
	private String purchaseUom;
	
	@Column(name="SPECIFICATION")
	private String specification;
	
	@Column(name="MODEL")
	private String model;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="KEEPER_ID")
	private String keeperId;
	
	@Transient
	private BigDecimal qtyWmsOnhand;
	
//	@ManyToOne
//	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
//	private Material material;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getQtyOnhand() {
		return qtyOnhand;
	}

	public void setQtyOnhand(BigDecimal qtyOnhand) {
		this.qtyOnhand = qtyOnhand;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public String getMaterialCategory4() {
		return materialCategory4;
	}

	public void setMaterialCategory4(String materialCategory4) {
		this.materialCategory4 = materialCategory4;
	}

	public String getInventoryUom() {
		return inventoryUom;
	}

	public void setInventoryUom(String inventoryUom) {
		this.inventoryUom = inventoryUom;
	}

	public String getPurchaseUom() {
		return purchaseUom;
	}

	public void setPurchaseUom(String purchaseUom) {
		this.purchaseUom = purchaseUom;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public BigDecimal getQtyMax() {
		return qtyMax;
	}

	public void setQtyMax(BigDecimal qtyMax) {
		this.qtyMax = qtyMax;
	}

	public String getAbcCategory() {
		return abcCategory;
	}

	public void setAbcCategory(String abcCategory) {
		this.abcCategory = abcCategory;
	}

	public void setQtyWriteOff(BigDecimal qtyWriteOff) {
		this.qtyWriteOff = qtyWriteOff;
	}

	public BigDecimal getQtyWriteOff() {
		return qtyWriteOff;
	}

	public String getKeeperId() {
		return keeperId;
	}

	public void setKeeperId(String keeperId) {
		this.keeperId = keeperId;
	}

	public BigDecimal getQtyDiff() {
		return qtyDiff;
	}

	public void setQtyDiff(BigDecimal qtyDiff) {
		this.qtyDiff = qtyDiff;
	}

	public BigDecimal getQtyWmsOnhand() {
		return qtyWmsOnhand;
	}

	public void setQtyWmsOnhand(BigDecimal qtyWmsOnhand) {
		this.qtyWmsOnhand = qtyWmsOnhand;
	}
}
