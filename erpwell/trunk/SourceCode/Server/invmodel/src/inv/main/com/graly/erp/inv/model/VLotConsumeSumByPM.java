package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_LOTCONSUME_SUM_BY_PM")
public class VLotConsumeSumByPM extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="DATE_IN")
	private Date dateIn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="IN_SUM")
	private BigDecimal inSum;
	
	@Column(name="SUMNUM")
	private BigDecimal sumNum;

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

	public BigDecimal getInSum() {
		return inSum;
	}

	public void setInSum(BigDecimal inSum) {
		this.inSum = inSum;
	}

	public BigDecimal getSumNum() {
		return sumNum;
	}

	public void setSumNum(BigDecimal sumNum) {
		this.sumNum = sumNum;
	}

	public Date getDateIn() {
		return dateIn;
	}

	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}

}
