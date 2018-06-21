package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_RACK_MOVEMENT_LOT")
public class RacKMovementLot extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static final String IO_TYPE_IN = "I";
	public static final String IO_TYPE_OUT = "O";
	
	public static final String MOVEMENT_TYPE_COUNT = "COUNT";//盘点
	public static final String MOVEMENT_TYPE_TRF = "TRF";//调整(调拨)
	
	public static final String STATUS_APPROVED = "APPROVED";
	public static final String STATUS_DRAFTED = "DRAFTED";
	
	@Column(name="WAREHOUSE_RRN")
	private	Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private	String warehouseId;
	
	@Column(name="RACK_RRN")
	private	Long rackRrn;
	
	@ManyToOne
	@JoinColumn(name="RACK_RRN", insertable=false, updatable=false)
	private	WarehouseRack rack;
	
	@Column(name="RACK_ID")
	private	String rackId;
	
	@Column(name="MATERIAL_RRN")
	private	Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private	String materialId;
	
	@Column(name="MATERIAL_NAME")
	private	String materialName;
	
	@Column(name="LOT_RRN")
	private	Long lotRrn;
	
	@Column(name="LOT_ID")
	private	String lotId;
	
	@Column(name="QTY")
	private	BigDecimal qty;
	
	@Column(name="DATE_IN")
	private	Date dateIn;
	
	@Column(name="IO_TYPE", nullable=false)
	private String ioType;//出入库娄型: IN 或者 OUT
	
	@Column(name="MOVEMENT_TYPE")
	private String movementType;//类型
	
	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;
	
	@Column(name="MOVEMENT_LINE_RRN")
	private Long movementLineRrn;//对应出库单行

	@Column(name="LOT_STATUS")
	private String lotStatus;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="IS_LARGE_LOT")
	private String isLargeLot = "N";//是否大批
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@Column(name="IS_IN")
	private String isGenMovementIn = "N";//是否已转成入单
	
	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getRackRrn() {
		return rackRrn;
	}

	public void setRackRrn(Long rackRrn) {
		this.rackRrn = rackRrn;
	}

	public String getRackId() {
		return rackId;
	}

	public void setRackId(String rackId) {
		this.rackId = rackId;
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

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public Date getDateIn() {
		return dateIn;
	}

	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}
	
	public WarehouseRack getRack() {
		return rack;
	}

	public void setRack(WarehouseRack rack) {
		this.rack = rack;
	}

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public String getIoType() {
		return ioType;
	}

	public void setIoType(String ioType) {
		this.ioType = ioType;
	}

	public long getMovementRrn() {
		return movementRrn;
	}

	public void setMovementRrn(long movementRrn) {
		this.movementRrn = movementRrn;
	}

	public long getMovementLineRrn() {
		return movementLineRrn;
	}

	public void setMovementLineRrn(long movementLineRrn) {
		this.movementLineRrn = movementLineRrn;
	}

	public String getLotStatus() {
		return lotStatus;
	}

	public void setLotStatus(String lotStatus) {
		this.lotStatus = lotStatus;
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public boolean getIsLargeLot() {
		return "Y".equals(isLargeLot);
	}

	public void setIsLargeLot(boolean isLargeLot) {
		this.isLargeLot = (isLargeLot?"Y":"N");
	}

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}

	public void setMovementRrn(Long movementRrn) {
		this.movementRrn = movementRrn;
	}

	public void setMovementLineRrn(Long movementLineRrn) {
		this.movementLineRrn = movementLineRrn;
	}
	
	public boolean isGenMovementIn(){
		return "Y".equals(isGenMovementIn);
	}
	
	public void setIsGenMovementIn(boolean isGenMovementIn){
		this.isGenMovementIn = isGenMovementIn?"Y":"N";
	}

	public String getIsGenMovementIn() {
		return isGenMovementIn;
	}

	public void setIsGenMovementIn(String isGenMovementIn) {
		this.isGenMovementIn = isGenMovementIn;
	}
}
