package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_MOVEMENT_WORKSHOP")
@DiscriminatorColumn(name="MOVEMENT_TYPE", discriminatorType = DiscriminatorType.STRING, length = 1)
public class MovementWorkShop extends ADUpdatable  {
	
	private static final long serialVersionUID = 1L;
	public static final String STATUS_DRAFTED = "DRAFTED";
	public static final String STATUS_COMPLETED = "COMPLETED";
	public static final String STATUS_APPROVED = "APPROVED";
	public static final String STATUS_CLOSED = "CLOSED";
	public static final String DOCTYPE_MAN = "MAN"; //制造领料
	public static final String DOCTYPE_REC = "REC"; //接收用料
	public static final String DOCTYPE_DEL = "DEL"; //车间配送
	public static final String DOCTYPE_HUI = "HUI"; //车间回料、用中文代替，区分系统中的名字R
	public static final String DOCTYPE_UNQ = "UNQ";
	public static final String DOCTYPE_SER = "SER"; //车间配送
	public static final String DOCTYPE_VIR = "VIR"; //虚拟库
	
	@Column(name="DOC_ID")
	private String docId;
	
	@Column(name = "DOC_TYPE")
	private String docType;//单据类型:生产领料 或者 车间调拨

	@Column(name = "DOC_STATUS")
	private String docStatus = STATUS_DRAFTED;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="TARGET_WAREHOUSE_RRN")
	private Long targetWarehouseRrn;
	
	@Column(name="TARGET_WAREHOUSE_ID")
	private String targetWarehouseId;
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="USER_CREATED")
	private String userCreated;//创建人
	
	@Column(name="USER_APPROVED")
	private String userApproved;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="TOTAL_LINES")
	private Long totalLines;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@OrderBy(value = "lineNo ASC")
	@JoinColumn(name = "MOVEMENT_RRN", referencedColumnName = "OBJECT_RRN" ,insertable = false, updatable = false)
	private List<MovementWorkShopLine> movementWorkShopLines;
	
	@Column(name="DBA_MARK")
	private String dbaMark;//DBA备注
	
	@Column(name="TRS_TYPE")
	private String trsType;
	
//	@Column(name = "PI_ID")
//	private String piId;//pi编号
//	
//	@Column(name = "INTERNAL_ORDER_ID")
//	private String internalOrderId;//内部订单编号
	
	@Transient
	private BigDecimal qtyTemp;
	
	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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


	public String getUserApproved() {
		return userApproved;
	}

	public void setUserApproved(String userApproved) {
		this.userApproved = userApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public Date getDateApproved() {
		return dateApproved;
	}
	
	public Long getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(Long totalLines) {
		this.totalLines = totalLines;
	}

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

	public BigDecimal getQtyTemp() {
		return qtyTemp;
	}

	public void setQtyTemp(BigDecimal qtyTemp) {
		this.qtyTemp = qtyTemp;
	}

	public String getDbaMark() {
		return dbaMark;
	}

	public void setDbaMark(String dbaMark) {
		this.dbaMark = dbaMark;
	}

	public List<MovementWorkShopLine> getMovementWorkShopLines() {
		return movementWorkShopLines;
	}

	public void setMovementWorkShopLines(
			List<MovementWorkShopLine> movementWorkShopLines) {
		this.movementWorkShopLines = movementWorkShopLines;
	}

	public Long getTargetWarehouseRrn() {
		return targetWarehouseRrn;
	}

	public void setTargetWarehouseRrn(Long targetWarehouseRrn) {
		this.targetWarehouseRrn = targetWarehouseRrn;
	}

	public String getTargetWarehouseId() {
		return targetWarehouseId;
	}

	public void setTargetWarehouseId(String targetWarehouseId) {
		this.targetWarehouseId = targetWarehouseId;
	}

	public String getTrsType() {
		return trsType;
	}

	public void setTrsType(String trsType) {
		this.trsType = trsType;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}
}

