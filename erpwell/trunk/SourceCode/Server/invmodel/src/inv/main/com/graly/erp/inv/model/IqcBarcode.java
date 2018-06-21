package com.graly.erp.inv.model;

import javax.persistence.Column;

import com.graly.framework.activeentity.model.ADUpdatable;

public class IqcBarcode extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="IQC_LINE_RRN")
	private Long iqcLineRrn;
	
	@Column(name="LINE_NO")
	private Long lineNO;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="BARCODE")
	private String barcode;

	public Long getIqcLineRrn() {
		return iqcLineRrn;
	}

	public void setIqcLineRrn(Long iqcLineRrn) {
		this.iqcLineRrn = iqcLineRrn;
	}

	public Long getLineNO() {
		return lineNO;
	}

	public void setLineNO(Long lineNO) {
		this.lineNO = lineNO;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
}
