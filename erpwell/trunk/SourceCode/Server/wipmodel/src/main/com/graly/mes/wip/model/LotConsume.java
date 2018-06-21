package com.graly.mes.wip.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_LOT_CONSUME")
public class LotConsume extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="IN_RRN")
	private Long inRrn;
	
	@Column(name="IN_ID")
	private String inId;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="QTY_PRODUCT")
	private BigDecimal qtyProduct;
	
	@Column(name="QTY_CONSUME")
	private BigDecimal qtyConsume;
	
	@Column(name="UNIT_CONSUME")
	private BigDecimal unitConsume;
	
	@Column(name="IS_WIN")
	private String isWin = "N";
	
	@Column(name="DATE_IN")
	private Date dateIn;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="IS_MANUAL")
	private String isManual = "N";
	
	@Transient
	private BigDecimal bomPrice;
	
	@Column(name="WRITEOFF_TYPE")//'N'--正常核销 'W'--冲销
	private String writeoffType = "N";//默认是N 正常核销
	
	@Transient
	private String consumeDate;//核销日期，多个核销日期用“，”分隔
	
	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}
	
	public Long getMoRrn() {
		return moRrn;
	}
	
	public void setMoId(String moId) {
		this.moId = moId;
	}
	
	public String getMoId() {
		return moId;
	}
	
	public void setInRrn(Long inRrn) {
		this.inRrn = inRrn;
	}
	
	public Long getInRrn() {
		return inRrn;
	}
	
	public void setInId(String inId) {
		this.inId = inId;
	}
	
	public String getInId() {
		return inId;
	}
	
	public Long getLotRrn() {
		return lotRrn;
	}
	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}
	
	public String getLotId() {
		return lotId;
	}
	public void setLotId(String lotId) {
		this.lotId = lotId;
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
	
	public String getMaterialName() {
		return materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	
	public BigDecimal getQtyConsume() {
		return qtyConsume;
	}
	public void setQtyConsume(BigDecimal qtyConsume) {
		this.qtyConsume = qtyConsume;
	}
	
	public BigDecimal getQtyProduct() {
		return qtyProduct;
	}
	public void setQtyProduct(BigDecimal qtyProduct) {
		this.qtyProduct = qtyProduct;
	}
	
	public BigDecimal getUnitConsume() {
		return unitConsume;
	}
	public void setUnitConsume(BigDecimal unitConsume) {
		this.unitConsume = unitConsume;
	}
	
	public Boolean getIsWin(){
		return "Y".equalsIgnoreCase(this.isWin) ? true : false; 
	}

	public void setIsWin(Boolean isWin) {
		this.isWin = isWin ? "Y" : "N";
	}
	
	public BigDecimal getBomPrice() {
		return bomPrice;
	}
	
	public void setBomPrice(BigDecimal bomPrice) {
		this.bomPrice = bomPrice;
	}

	
	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}
	
	public Date getDateIn() {
		return dateIn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseId() {
		return warehouseId;
	}
	
	public Boolean getIsManual(){
		return "Y".equalsIgnoreCase(this.isManual) ? true : false; 
	}

	public void setIsManual(Boolean isManual) {
		this.isManual = isManual ? "Y" : "N";
	}

	public String getWriteoffType() {
		return writeoffType;
	}

	public void setWriteoffType(String writeoffType) {
		this.writeoffType = writeoffType;
	}

	public String getConsumeDate() {
		return consumeDate;
	}

	public void setConsumeDate(String consumeDate) {
		this.consumeDate = consumeDate;
	}
}
