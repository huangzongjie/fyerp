package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name = "INV_WAREHOUSE_RACK")
public class WarehouseRack extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="SERIAL_NUMBER")
	private Long serialNumber;

	@Column(name = "RACK_ID")
	private String rackId;// »õ¼Ü±àºÅ

	@Column(name = "WAREHOUSE_RRN")
	private Long warehouseRrn;// ²Ö¿â
	
	@Column(name = "WAREHOUSE_ID")
	private String warehouseId;// ²Ö¿â±àºÅ

	@Column(name = "AREA_ID")
	private String areaId;// ¿âÇø

	@Column(name = "IS_FULL")
	private String isFull;// ÊÇ·ñÒÑÂú

	@Column(name = "SIZEE")
	private BigDecimal size;// ¹æ¸ñ
	
	@Column(name = "COMMENTS")
	private String comments;
	
	@Column(name = "RESERVE_COLUMN1")
	private String reserveColumn1;
	
	@Column(name = "RESERVE_COLUMN2")
	private String reserveColumn2;
	
	@Column(name = "RESERVE_COLUMN3")
	private String reserveColumn3;
	
	@Column(name = "RESERVE_COLUMN4")
	private Date reserveColumn4;
	
	@Column(name = "RESERVE_COLUMN5")
	private Date reserveColumn5;
	
	@Column(name = "X")//ÅÅ
	private Long x;

	@Column(name = "y")//ÁÐ
	private Long y;
	
	public String getRackId() {
		return rackId;
	}

	public void setRackId(String rackId) {
		this.rackId = rackId;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}
	
	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getIsFull() {
		return isFull;
	}

	public boolean IsFull() {
		return isFull == "Y" ? true : false;
	}

	public void setIsFull(boolean isFull) {
		if (isFull) {
			this.isFull = "Y";
		} else {
			this.isFull = "N";
		}
	}

	public BigDecimal getSize() {
		return size;
	}

	public void setSize(BigDecimal size) {
		this.size = size;
	}

	public Long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Long serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getReserveColumn1() {
		return reserveColumn1;
	}

	public void setReserveColumn1(String reserveColumn1) {
		this.reserveColumn1 = reserveColumn1;
	}

	public String getReserveColumn2() {
		return reserveColumn2;
	}

	public void setReserveColumn2(String reserveColumn2) {
		this.reserveColumn2 = reserveColumn2;
	}

	public String getReserveColumn3() {
		return reserveColumn3;
	}

	public void setReserveColumn3(String reserveColumn3) {
		this.reserveColumn3 = reserveColumn3;
	}

	public Date getReserveColumn4() {
		return reserveColumn4;
	}

	public void setReserveColumn4(Date reserveColumn4) {
		this.reserveColumn4 = reserveColumn4;
	}

	public Date getReserveColumn5() {
		return reserveColumn5;
	}

	public void setReserveColumn5(Date reserveColumn5) {
		this.reserveColumn5 = reserveColumn5;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getX() {
		return x;
	}

	public void setX(Long x) {
		this.x = x;
	}

	public Long getY() {
		return y;
	}

	public void setY(Long y) {
		this.y = y;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}
}
