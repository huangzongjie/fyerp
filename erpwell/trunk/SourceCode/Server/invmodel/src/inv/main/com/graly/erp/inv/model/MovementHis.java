package com.graly.erp.inv.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;

import javax.persistence.Entity;

import javax.persistence.Table;

import com.graly.erp.base.model.Documentation;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_MOVEMENT_HIS")
public class MovementHis extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String ACTION_TYPE_CREATE = "CREATE";
	public static final String ACTION_TYPE_SAVE = "SAVE";
	public static final String ACTION_TYPE_DELETE = "DELETE";
	public static final String ACTION_TYPE_APPROVE = "APPROVE";
	public static final String ACTION_TYPE_WRITEOFF = "WRITEOFF";
	
	@Column(name="DOC_ID")
	private String docId;
	
	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_STATUS")
	private String docStatus;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="MOVEMENT_TYPE")
	private String movementType;
	
	@Column(name="MOVEMENT_DATE")
	private Date movementDate;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
		
	@Column(name="USER_CREATED")
	private String userCreated;
	
	@Column(name="USER_APPROVED")
	private String userApproved;
	
	@Column(name="DATE_CREATED")
	private Date dateCreated;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="TOTAL_LINES")
	private Long totalLines;

	@Column(name="ACTION_TYPE")
	private String actionType;
	
	@Column(name="USER_ACTION")
	private String userAction;
	
	@Column(name="DATE_ACTION")
	private Date dateAction;
	
	public MovementHis(Movement movement) {
		this.setOrgRrn(movement.getOrgRrn());
		this.setIsActive(true);
		this.setDateCreated(new Date());
		this.setDocId(movement.getDocId());
		this.setDocType(movement.getDocType());
		this.setDocStatus(movement.getDocStatus());
		this.setDescription(movement.getDescription());
		this.setMovementDate(movement.getMovementDate());
		this.setWarehouseRrn(movement.getWarehouseRrn());
		this.setWarehouseId(movement.getWarehouseId());
		this.setUserCreated(movement.getUserCreated());
		this.setUserApproved(movement.getUserApproved());
		this.setDateCreated(movement.getDateCreated());
		this.setDateApproved(movement.getDateApproved());
		this.setTotalLines(movement.getTotalLines());
	}
	
	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocId() {
		return docId;
	}
	
	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getDocStatus() {
		return docStatus;
	}
	
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
	
	public Long getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(Long totalLines) {
		this.totalLines = totalLines;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActionType() {
		return actionType;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setDateAction(Date dateAction) {
		this.dateAction = dateAction;
	}

	public Date getDateAction() {
		return dateAction;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public String getMovementType() {
		return movementType;
	}

}
