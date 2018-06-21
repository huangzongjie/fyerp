package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_REP_MONTH_INVENTORY")
public class VMonthInventory extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;

	@Column(name="INIT_SUM")
	private BigDecimal initSum;

	@Column(name="INIT_NUM")
	private BigDecimal initNum;

	@Column(name="INIT_PRICE")
	private BigDecimal initPrice;

	@Column(name="IN_SUM")
	private BigDecimal inSum;

	@Column(name="IN_NUM")
	private BigDecimal inNum;

	@Column(name="IN_PRICE")
	private BigDecimal inPrice;

	@Column(name="OUT_SUM")
	private BigDecimal outSum;

	@Column(name="OUT_NUM")
	private BigDecimal outNum;

	@Column(name="OUT_PRICE")
	private BigDecimal outPrice;

	@Column(name="FINAL_SUM")
	private BigDecimal finalSum;

	@Column(name="FINAL_NUM")
	private BigDecimal finalNum;

	@Column(name="FINAL_PRICE")
	private BigDecimal finalPrice;

	@Column(name="OPDATE")
	private String opdate;

	@Column(name="MONTHNUM")
	private String monthnum;

	@Column(name="OPUSER")
	private String opuser;

	@Column(name="WAREHOUSE")
	private String warehouse;
	
	@Column(name="INV_UNIT")
	private String invUnit;
	
	@Column(name="STORAGE_ADM")
	private String storageAdm;

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public BigDecimal getInitSum() {
		return initSum;
	}

	public void setInitSum(BigDecimal initSum) {
		this.initSum = initSum;
	}

	public BigDecimal getInitNum() {
		return initNum;
	}

	public void setInitNum(BigDecimal initNum) {
		this.initNum = initNum;
	}

	public BigDecimal getInitPrice() {
		return initPrice;
	}

	public void setInitPrice(BigDecimal initPrice) {
		this.initPrice = initPrice;
	}

	public BigDecimal getInSum() {
		return inSum;
	}

	public void setInSum(BigDecimal inSum) {
		this.inSum = inSum;
	}

	public BigDecimal getInNum() {
		return inNum;
	}

	public void setInNum(BigDecimal inNum) {
		this.inNum = inNum;
	}

	public BigDecimal getInPrice() {
		return inPrice;
	}

	public void setInPrice(BigDecimal inPrice) {
		this.inPrice = inPrice;
	}

	public BigDecimal getOutSum() {
		return outSum;
	}

	public void setOutSum(BigDecimal outSum) {
		this.outSum = outSum;
	}

	public BigDecimal getOutNum() {
		return outNum;
	}

	public void setOutNum(BigDecimal outNum) {
		this.outNum = outNum;
	}

	public BigDecimal getOutPrice() {
		return outPrice;
	}

	public void setOutPrice(BigDecimal outPrice) {
		this.outPrice = outPrice;
	}

	public BigDecimal getFinalSum() {
		return finalSum;
	}

	public void setFinalSum(BigDecimal finalSum) {
		this.finalSum = finalSum;
	}

	public BigDecimal getFinalNum() {
		return finalNum;
	}

	public void setFinalNum(BigDecimal finalNum) {
		this.finalNum = finalNum;
	}

	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

	public String getOpdate() {
		return opdate;
	}

	public void setOpdate(String opdate) {
		this.opdate = opdate;
	}

	public String getMonthnum() {
		return monthnum;
	}

	public void setMonthnum(String monthnum) {
		this.monthnum = monthnum;
	}

	public String getOpuser() {
		return opuser;
	}

	public void setOpuser(String opuser) {
		this.opuser = opuser;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInvUnit() {
		return invUnit;
	}

	public void setInvUnit(String invUnit) {
		this.invUnit = invUnit;
	}

	public String getStorageAdm() {
		return storageAdm;
	}

	public void setStorageAdm(String storageAdm) {
		this.storageAdm = storageAdm;
	}
}
