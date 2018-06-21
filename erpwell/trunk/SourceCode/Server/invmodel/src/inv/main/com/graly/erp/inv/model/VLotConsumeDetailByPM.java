package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_LOTCONSUME_DETAIL_BY_PM")
public class VLotConsumeDetailByPM extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="DATE_IN")
	private Date dateIn;
	
	@Column(name="PROMATERIAL")
	private String promaterial;

	@Column(name="NAME")
	private String name;

	@Column(name="MATERIAL")
	private String material;
	
	@Column(name="CHILDNAME")
	private String childName;

	@Column(name="SUM_IN_SUM")
	private BigDecimal sumInSum;

	@Column(name="SUM_ALLCONSUME")
	private BigDecimal sumAllconsume;

	@Column(name="SUM_SUMNUM")
	private BigDecimal sumSumnum;

	public String getPromaterial() {
		return promaterial;
	}

	public void setPromaterial(String promaterial) {
		this.promaterial = promaterial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public BigDecimal getSumInSum() {
		return sumInSum;
	}

	public void setSumInSum(BigDecimal sumInSum) {
		this.sumInSum = sumInSum;
	}

	public BigDecimal getSumAllconsume() {
		return sumAllconsume;
	}

	public void setSumAllconsume(BigDecimal sumAllconsume) {
		this.sumAllconsume = sumAllconsume;
	}

	public BigDecimal getSumSumnum() {
		return sumSumnum;
	}

	public void setSumSumnum(BigDecimal sumSumnum) {
		this.sumSumnum = sumSumnum;
	}

	public Date getDateIn() {
		return dateIn;
	}

	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}
}
