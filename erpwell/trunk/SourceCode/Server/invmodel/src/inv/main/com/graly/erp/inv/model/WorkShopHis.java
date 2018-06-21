package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.graly.erp.wiphis.model.InvLotHis;
import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.mes.wip.model.Lot;

@Entity
@Table(name="INVHIS_WORKSHOP")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TRANS_TYPE", discriminatorType = DiscriminatorType.STRING, length = 32)
public class WorkShopHis extends ADUpdatable {
	private static final long serialVersionUID = 1L;

 
	public static String TRANS_RECEIVE = "RECEIVE";
	public static String TRANS_USED = "USED";
	
	@Column(name="TRANS_TYPE", insertable = false, updatable = false)
	private String transType;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="MO_LINE_RRN")
	private Long moLineRrn;
	
	@Column(name="QTY_INITIAL")
	private BigDecimal qtyInitial;
	
	@Column(name="QTY_CURRENT")
	private BigDecimal qtyCurrent;
	
	@Column(name="QTY_TRANSACTION")
	private BigDecimal qtyTransaction;
	
	@Column(name="DATE_PRODUCT")
	private Date dateProduct;

	@Column(name="WORKCENTER_RRN")
	private Long workCenterRrn;
	
	@Column(name="WORKCENTER_ID")
	private String workCenterId;

	@Column(name="POSITION")
	private String position;
	
	public WorkShopHis(){
	}
	
	public WorkShopHis(Lot lot){
		this.setOrgRrn(lot.getOrgRrn());
		this.setIsActive(lot.getIsActive());
		this.setCreated(new Date());
		this.setUpdatedBy(lot.getUpdatedBy());
		this.setLotRrn(lot.getObjectRrn());
		this.setLotId(lot.getLotId());
		this.setDescription(lot.getDescription());
		this.setLotType(lot.getLotType());
		this.setMaterialRrn(lot.getMaterialRrn());
		this.setMaterialId(lot.getMaterialId());
		this.setMaterialName(lot.getMaterialName());
		this.setWarehouseRrn(lot.getWarehouseRrn());
		this.setWarehouseId(lot.getWarehouseId());
		this.setMoRrn(lot.getMoRrn());
		this.setMoId(lot.getMoId());
		this.setMoLineRrn(lot.getMoLineRrn());
		this.setQtyCurrent(lot.getQtyCurrent());
		this.setQtyInitial(lot.getQtyInitial());
		this.setDateProduct(lot.getDateProduct());
		this.setWorkCenterRrn(lot.getWorkCenterRrn());
		this.setWorkCenterId(lot.getWorkCenterId());
		this.setPosition(lot.getPosition());
		this.setQtyTransaction(lot.getQtyTransaction());
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
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

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

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

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

	public BigDecimal getQtyInitial() {
		return qtyInitial;
	}

	public void setQtyInitial(BigDecimal qtyInitial) {
		this.qtyInitial = qtyInitial;
	}

	public BigDecimal getQtyCurrent() {
		return qtyCurrent;
	}

	public void setQtyCurrent(BigDecimal qtyCurrent) {
		this.qtyCurrent = qtyCurrent;
	}

	public BigDecimal getQtyTransaction() {
		return qtyTransaction;
	}

	public void setQtyTransaction(BigDecimal qtyTransaction) {
		this.qtyTransaction = qtyTransaction;
	}

	public Date getDateProduct() {
		return dateProduct;
	}

	public void setDateProduct(Date dateProduct) {
		this.dateProduct = dateProduct;
	}

	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}

	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}

	public String getWorkCenterId() {
		return workCenterId;
	}

	public void setWorkCenterId(String workCenterId) {
		this.workCenterId = workCenterId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
}
