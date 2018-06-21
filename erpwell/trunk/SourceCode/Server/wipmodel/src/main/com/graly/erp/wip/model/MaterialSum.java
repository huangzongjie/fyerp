package com.graly.erp.wip.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="PAS_MATERIAL_SUM")
public class MaterialSum extends ADBase {

	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="QTY_MIN")
	private BigDecimal qtyMin = BigDecimal.ZERO;
	
	@Column(name="QTY_MAX")
	private BigDecimal qtyMax = BigDecimal.ZERO;
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation = BigDecimal.ZERO;
	
	@Column(name="QTY_ONHAND")
	private BigDecimal qtyOnHand = BigDecimal.ZERO;
	
	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit = BigDecimal.ZERO;
	
	@Column(name="QTY_MO")
	private BigDecimal qtyMo = BigDecimal.ZERO;

	@Column(name="QTY_MO_IN")
	private BigDecimal qtyMoIn = BigDecimal.ZERO;

	@Column(name="QTY_MO_WIP")
	private BigDecimal qtyMoWip = BigDecimal.ZERO;

	@Column(name="QTY_MOLINE")
	private BigDecimal qtyMoLine = BigDecimal.ZERO;
	
	@Column(name="QTY_MOLINE_RECEIVE")
	private BigDecimal qtyMoLineReceive = BigDecimal.ZERO;
	
	@Column(name="QTY_MOLINE_WIP")
	private BigDecimal qtyMoLineWip = BigDecimal.ZERO;
	
	@Column(name="QTY_SO")
	private BigDecimal qtySo = BigDecimal.ZERO;
	
	@Column(name="QTY_MIN_PRODUCT")
	private BigDecimal qtyMinProduct = BigDecimal.ZERO;
	
	@Column(name="IS_PURCHASE")
	private String isPurchase;
	
	@Column(name="IS_PRODUCT")
	private String isProduct;

	@Column(name="IS_JIT")
	private String isJit;
	
	@Column(name="STAND_TIME")
	private BigDecimal standTime;
	
	@Column(name="IQC_LEAD_TIME")
	private Long iqcLeadTime;
	
	@Transient
	private Material material;
	
	@Column(name="QTY_WRITE_OFF")
	private BigDecimal qtyWriteOff = BigDecimal.ZERO;
	
	@Column(name="QTY_DIFF")
	private BigDecimal qtyDiff = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal meter;
	
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
	
	public String getMaterialName() {
		return materialName;
	}
	
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	
	public BigDecimal getQtyMin() {
		return qtyMin;
	}
	
	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}
	
	public BigDecimal getQtyMax() {
		return qtyMax;
	}
	
	public void setQtyMax(BigDecimal qtyMax) {
		this.qtyMax = qtyMax;
	}
	
	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}
	
	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}
	
	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}
	
	public void setQtyOnHand(BigDecimal qtyOnHand) {
		this.qtyOnHand = qtyOnHand;
	}
	
	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}
	
	public void setQtyMo(BigDecimal qtyMo) {
		this.qtyMo = qtyMo;
	}

	public BigDecimal getQtyMo() {
		return qtyMo;
	}

	public void setQtyMoIn(BigDecimal qtyMoIn) {
		this.qtyMoIn = qtyMoIn;
	}

	public BigDecimal getQtyMoIn() {
		return qtyMoIn;
	}

	public void setQtyMoWip(BigDecimal qtyMoWip) {
		this.qtyMoWip = qtyMoWip;
	}

	public BigDecimal getQtyMoWip() {
		return qtyMoWip;
	}
	
	public void setQtyMoLine(BigDecimal qtyMoLine) {
		this.qtyMoLine = qtyMoLine;
	}

	public BigDecimal getQtyMoLine() {
		return qtyMoLine;
	}
	
	public BigDecimal getQtyMoLineReceive() {
		return qtyMoLineReceive;
	}
	
	public void setQtyMoLineReceive(BigDecimal qtyMoLineReceive) {
		this.qtyMoLineReceive = qtyMoLineReceive;
	}

	public void setQtyMoLineWip(BigDecimal qtyMoLineWip) {
		this.qtyMoLineWip = qtyMoLineWip;
	}

	public BigDecimal getQtyMoLineWip() {
		return qtyMoLineWip;
	}
	
	public void setQtySo(BigDecimal qtySo) {
		this.qtySo = qtySo;
	}

	public BigDecimal getQtySo() {
		return qtySo;
	}
	
	public void setQtyMinProduct(BigDecimal qtyMinProduct) {
		this.qtyMinProduct = qtyMinProduct;
	}

	public BigDecimal getQtyMinProduct() {
		return qtyMinProduct;
	}

	public Boolean getIsPurchase(){
		return "Y".equalsIgnoreCase(this.isPurchase) ? true : false; 
	}

	public void setIsPurchase(Boolean isPurchase) {
		this.isPurchase = isPurchase ? "Y" : "N";
	}
	
	public Boolean getIsProduct(){
		return "Y".equalsIgnoreCase(this.isProduct) ? true : false; 
	}

	public void setIsProduct(Boolean isProduct) {
		this.isProduct = isProduct ? "Y" : "N";
	}
	
	public Boolean getIsJit(){
		return "Y".equalsIgnoreCase(this.isJit) ? true : false; 
	}

	public void setIsJit(Boolean isJit) {
		this.isJit = isJit ? "Y" : "N";
	}
	
	public void setStandTime(BigDecimal standTime) {
		this.standTime = standTime;
	}

	public BigDecimal getStandTime() {
		return standTime;
	}

	public void setIqcLeadTime(Long iqcLeadTime) {
		this.iqcLeadTime = iqcLeadTime;
	}

	public Long getIqcLeadTime() {
		return iqcLeadTime;
	}

	@Transient//ø…∑÷≈‰ ˝
	public BigDecimal getQtyAssignable() {
		if(getQtyOnHand() != null && getQtyAllocation() != null) {
			return getQtyOnHand().subtract(getQtyAllocation());			
		}
		return null;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}

	public BigDecimal getQtyWriteOff() {
		return qtyWriteOff;
	}

	public void setQtyWriteOff(BigDecimal qtyWriteOff) {
		this.qtyWriteOff = qtyWriteOff;
	}

	public BigDecimal getQtyDiff() {
		return qtyDiff;
	}

	public void setQtyDiff(BigDecimal qtyDiff) {
		this.qtyDiff = qtyDiff;
	}

	public BigDecimal getMeter() {
		return meter;
	}

	public void setMeter(BigDecimal meter) {
		this.meter = meter;
	}
}
