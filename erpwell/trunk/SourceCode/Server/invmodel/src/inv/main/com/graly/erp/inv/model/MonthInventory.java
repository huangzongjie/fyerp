package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="REP_MONTH_INVENTORY")
public class MonthInventory extends ADBase {
	private static final long serialVersionUID = 4606343722267060614L;

	@Column(name="REPORT_MONTH")
	private Date reportMonth;

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

	@Column(name="QTY_INITIAL")
	private BigDecimal qtyInitial;

	@Column(name="PRICE_INITIAL")
	private BigDecimal priceInitial;

	@Column(name="AMOUNT_INITIAL")
	private BigDecimal amountInitial;

	@Column(name="QTY_IN")
	private BigDecimal qtyIn;

	@Column(name="PRICE_IN")
	private BigDecimal priceIn;

	@Column(name="AMOUNT_IN")
	private BigDecimal amountIn;

	@Column(name="QTY_OUT")
	private BigDecimal qtyOut;

	@Column(name="PRICE_OUT")
	private BigDecimal priceOut;

	@Column(name="AMOUNT_OUT")
	private BigDecimal amountOut;

	@Column(name="QTY_END")
	private BigDecimal qtyEnd;

	@Column(name="PRICE_END")
	private BigDecimal priceEnd;

	@Column(name="AMOUNT_END")
	private BigDecimal amountEnd;

	@Column(name="QTY_DIFFERENT")
	private BigDecimal qtyDifferent;

	@Column(name="AMOUNT_DIFFERENT")
	private BigDecimal amountDifferent;

	@Column(name="UOM_ID")
	private String uomId;

	public Date getReportMonth() {
		return reportMonth;
	}

	public void setReportMonth(Date reportMonth) {
		this.reportMonth = reportMonth;
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

	public BigDecimal getQtyInitial() {
		return qtyInitial;
	}

	public void setQtyInitial(BigDecimal qtyInitial) {
		this.qtyInitial = qtyInitial;
	}

	public BigDecimal getPriceInitial() {
		return priceInitial;
	}

	public void setPriceInitial(BigDecimal priceInitial) {
		this.priceInitial = priceInitial;
	}

	public BigDecimal getAmountInitial() {
		return amountInitial;
	}

	public void setAmountInitial(BigDecimal amountInitial) {
		this.amountInitial = amountInitial;
	}

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public BigDecimal getPriceIn() {
		return priceIn;
	}

	public void setPriceIn(BigDecimal priceIn) {
		this.priceIn = priceIn;
	}

	public BigDecimal getAmountIn() {
		return amountIn;
	}

	public void setAmountIn(BigDecimal amountIn) {
		this.amountIn = amountIn;
	}

	public BigDecimal getQtyOut() {
		return qtyOut;
	}

	public void setQtyOut(BigDecimal qtyOut) {
		this.qtyOut = qtyOut;
	}

	public BigDecimal getPriceOut() {
		return priceOut;
	}

	public void setPriceOut(BigDecimal priceOut) {
		this.priceOut = priceOut;
	}

	public BigDecimal getAmountOut() {
		return amountOut;
	}

	public void setAmountOut(BigDecimal amountOut) {
		this.amountOut = amountOut;
	}

	public BigDecimal getQtyEnd() {
		return qtyEnd;
	}

	public void setQtyEnd(BigDecimal qtyEnd) {
		this.qtyEnd = qtyEnd;
	}

	public BigDecimal getPriceEnd() {
		return priceEnd;
	}

	public void setPriceEnd(BigDecimal priceEnd) {
		this.priceEnd = priceEnd;
	}

	public BigDecimal getAmountEnd() {
		return amountEnd;
	}

	public void setAmountEnd(BigDecimal amountEnd) {
		this.amountEnd = amountEnd;
	}

	public BigDecimal getQtyDifferent() {
		return qtyDifferent;
	}

	public void setQtyDifferent(BigDecimal qtyDifferent) {
		this.qtyDifferent = qtyDifferent;
	}

	public BigDecimal getAmountDifferent() {
		return amountDifferent;
	}

	public void setAmountDifferent(BigDecimal amountDifferent) {
		this.amountDifferent = amountDifferent;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
}
