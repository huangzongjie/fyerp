package com.graly.erp.pdm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;
 
@Entity
@Table(name="T_MATERIAL")
public class WmsMaterial extends ADBase {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_CODE")
	private String materialCode;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="SPEC")
	private String spec;
	
	@Column(name="UNIT")
	private String unit;
	
	@Column(name="UPPER_LIMIT")
	private BigDecimal upperLimit;
	
	@Column(name="LOWER_LIMIT")
	private BigDecimal lowerLimit;
	
	@Column(name="SAFE_STOCK")
	private BigDecimal safeStock;
	
	@Column(name="ERP_WRITE")
	private BigDecimal erpWrite;
	
	@Column(name="ERP_WRITE_TIME")
	private Date erpWriteTime;
	
	@Column(name="WMS_READ")
	private BigDecimal wmsRead;
	
	@Column(name="WMS_READ_TIME")
	private Date wmsReadTime;
	
	@Column(name="MATERIAL_CATEGORY1")
	private String materialCategory1;
	
	@Column(name="MATERIAL_CATEGORY2")
	private String materialCategory2;
	
	@Column(name="MATERIAL_CATEGORY3")
	private String materialCategory3;
	
	@Column(name="MATERIAL_CATEGORY4")
	private String materialCategory4;
	
	@Column(name="MATERIAL_CATEGORY5")
	private String materialCategory5;
	
	@Column(name="MATERIAL_CATEGORY6")
	private String materialCategory6;

	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(BigDecimal upperLimit) {
		this.upperLimit = upperLimit;
	}

	public BigDecimal getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(BigDecimal lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public BigDecimal getSafeStock() {
		return safeStock;
	}

	public void setSafeStock(BigDecimal safeStock) {
		this.safeStock = safeStock;
	}

	public BigDecimal getErpWrite() {
		return erpWrite;
	}

	public void setErpWrite(BigDecimal erpWrite) {
		this.erpWrite = erpWrite;
	}

	public String getMaterialCategory1() {
		return materialCategory1;
	}

	public void setMaterialCategory1(String materialCategory1) {
		this.materialCategory1 = materialCategory1;
	}

	public String getMaterialCategory2() {
		return materialCategory2;
	}

	public void setMaterialCategory2(String materialCategory2) {
		this.materialCategory2 = materialCategory2;
	}

	public String getMaterialCategory3() {
		return materialCategory3;
	}

	public void setMaterialCategory3(String materialCategory3) {
		this.materialCategory3 = materialCategory3;
	}

	public String getMaterialCategory4() {
		return materialCategory4;
	}

	public void setMaterialCategory4(String materialCategory4) {
		this.materialCategory4 = materialCategory4;
	}

	public String getMaterialCategory5() {
		return materialCategory5;
	}

	public void setMaterialCategory5(String materialCategory5) {
		this.materialCategory5 = materialCategory5;
	}

	public String getMaterialCategory6() {
		return materialCategory6;
	}

	public void setMaterialCategory6(String materialCategory6) {
		this.materialCategory6 = materialCategory6;
	}

	public Date getErpWriteTime() {
		return erpWriteTime;
	}

	public void setErpWriteTime(Date erpWriteTime) {
		this.erpWriteTime = erpWriteTime;
	}

	public BigDecimal getWmsRead() {
		return wmsRead;
	}

	public void setWmsRead(BigDecimal wmsRead) {
		this.wmsRead = wmsRead;
	}

	public Date getWmsReadTime() {
		return wmsReadTime;
	}

	public void setWmsReadTime(Date wmsReadTime) {
		this.wmsReadTime = wmsReadTime;
	}
}	


	