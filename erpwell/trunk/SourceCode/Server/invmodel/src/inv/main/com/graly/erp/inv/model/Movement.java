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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.wip.model.ManufactureOrder;

@Entity
@Table(name="INV_MOVEMENT")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="MOVEMENT_TYPE", discriminatorType = DiscriminatorType.STRING, length = 1)
public class Movement extends Documentation {
	
	private static final long serialVersionUID = 1L;
	
	public enum LotAction {
		ADD, 
		REMOVE
	};
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="MOVEMENT_DATE")
	private Date movementDate;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="RECEIPT_RRN")
	private Long receiptRrn;
	
	@Column(name="RECEIPT_ID")
	private String receiptId;
	
	@Column(name="IQC_RRN")
	private Long iqcRrn;
	
	@Column(name="IQC_ID")
	private String iqcId;
	
	@Column(name="PO_RRN")
	private Long poRrn;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="SO_RRN")
	private Long soRrn;
	
	@Column(name="SO_ID")
	private String soId;
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@ManyToOne
	@JoinColumn(name = "MO_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private ManufactureOrder mo;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="USER_CREATED")
	private String userCreated;
	
	@Column(name="USER_APPROVED")
	private String userApproved;
	
	@Column(name="DATE_CREATED")
	private Date dateCreated;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="USER_IQC")
	private String userIqc;
	
	@Column(name="IS_PRINTED")
	private String isPrinted;
	
	@Column(name="TOTAL_LINES")
	private Long totalLines;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@OrderBy(value = "lineNo ASC")
	@JoinColumn(name = "MOVEMENT_RRN", referencedColumnName = "OBJECT_RRN" ,insertable = false, updatable = false)
	private List<MovementLine> movementLines;
	
	@Column(name="PRINT_TIME")
	private Long printTime;//´òÓ¡´ÎÊý
	
	@Column(name="DBA_MARK")
	private String dbaMark;//DBA±¸×¢
	
	@Column(name = "PI_ID")
	private String piId;//pi±àºÅ
	
	@Column(name = "INTERNAL_ORDER_ID")
	private String internalOrderId;//ÄÚ²¿¶©µ¥±àºÅ
	
	@Transient
	private BigDecimal qtyTemp;
	
	@Column(name = "WMS_WAREHOUSE")
	private String wmsWarehouse;//WMS¿â´æ
	
	@Column(name = "IS_SERVICES_OUT")
	private String isServicesOut;//pi±àºÅ
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
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

	public Long getReceiptRrn() {
		return receiptRrn;
	}

	public void setReceiptRrn(Long receiptRrn) {
		this.receiptRrn = receiptRrn;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public Long getIqcRrn() {
		return iqcRrn;
	}

	public void setIqcRrn(Long iqcRrn) {
		this.iqcRrn = iqcRrn;
	}

	public String getIqcId() {
		return iqcId;
	}

	public void setIqcId(String iqcId) {
		this.iqcId = iqcId;
	}

	public Long getPoRrn() {
		return poRrn;
	}

	public void setPoRrn(Long poRrn) {
		this.poRrn = poRrn;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public Long getSoRrn() {
		return soRrn;
	}

	public void setSoRrn(Long soRrn) {
		this.soRrn = soRrn;
	}

	public String getSoId() {
		return soId;
	}

	public void setSoId(String soId) {
		this.soId = soId;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public String getUserApproved() {
		return userApproved;
	}

	public void setUserApproved(String userApproved) {
		this.userApproved = userApproved;
	}


	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public Date getDateApproved() {
		return dateApproved;
	}
	
	public String getUserIqc() {
		return userIqc;
	}

	public void setUserIqc(String userIqc) {
		this.userIqc = userIqc;
	}

	public String getIsPrinted() {
		return isPrinted;
	}

	public void setIsPrinted(String isPrinted) {
		this.isPrinted = isPrinted;
	}

	public Long getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(Long totalLines) {
		this.totalLines = totalLines;
	}

	public List<MovementLine> getMovementLines() {
		return movementLines;
	}

	public void setMovementLines(List<MovementLine> movementLines) {
		this.movementLines = movementLines;
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

	public Long getPrintTime() {
		return printTime;
	}

	public void setPrintTime(Long printTime) {
		this.printTime = printTime;
	}

	public ManufactureOrder getMo() {
		return mo;
	}

	public void setMo(ManufactureOrder mo) {
		this.mo = mo;
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

	public String getPiId() {
		return piId;
	}

	public void setPiId(String piId) {
		this.piId = piId;
	}

	public String getInternalOrderId() {
		return internalOrderId;
	}

	public void setInternalOrderId(String internalOrderId) {
		this.internalOrderId = internalOrderId;
	}

	public String getWmsWarehouse() {
		return wmsWarehouse;
	}

	public void setWmsWarehouse(String wmsWarehouse) {
		this.wmsWarehouse = wmsWarehouse;
	}
	public Boolean getIsServicesOut(){
		return "Y".equalsIgnoreCase(this.isServicesOut) ? true : false; 
	}

	public void setIsServicesOut(Boolean isServicesOut) {
		this.isServicesOut = isServicesOut ? "Y" : "N";
	}
}
