package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="INV_WORKSHOP_STORAGE")
public class WorkShopStorage extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
//	@Column(name="MATERIAL_ID")
//	private String materialId;
//	
//	@Column(name="MATERIAL_NAME")
//	private String materialName;
	
//	@Column(name="UOM_ID")
//	private String uomId;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
//	@Column(name="WAREHOUSE_ID")
//	private String warehouseId;
	
	@Column(name="QTY_ONHAND")
	private BigDecimal qtyOnhand = BigDecimal.ZERO;
	
	@Column(name="QTY_DIFF")
	private BigDecimal qtyDiff = BigDecimal.ZERO;//������,����ά��ϵͳ�п����ʵ�ʿ��Ĳ�����,ϵͳ���+������=ʵ�ʿ��

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}


	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public BigDecimal getQtyOnhand() {
		return qtyOnhand;
	}

	public void setQtyOnhand(BigDecimal qtyOnhand) {
		this.qtyOnhand = qtyOnhand;
	}

	public BigDecimal getQtyDiff() {
		return qtyDiff;
	}

	public void setQtyDiff(BigDecimal qtyDiff) {
		this.qtyDiff = qtyDiff;
	}
}
