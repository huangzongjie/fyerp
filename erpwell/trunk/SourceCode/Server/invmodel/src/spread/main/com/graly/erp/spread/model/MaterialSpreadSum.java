package com.graly.erp.spread.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_MATERIAL_SPREAD_SUM")
public class MaterialSpreadSum extends ADBase {
	private static final long	serialVersionUID	= 1L;
	
	@Column(name="CREATED")
	private Date created;
	
	@Column(name="MATERIAL_RRN")
	private long materialRrn;
	
	@ManyToOne
	@JoinColumn(name="MATERIAL_RRN", insertable=false, updatable=false)
	private Material material;
	
	@Transient
	private String materialId;
	
	@Transient
	private String materialName;
	
	@Column(name="WORKCENTER")
	private String workCenter;
	
	@Column(name="QTY_INITIAL")
	private BigDecimal qtyInitial;//期初值
	
	@Column(name="QTY_PRODUCE")//车间生产的数量
	private BigDecimal qtyProduce;
	
	@Column(name="QTY_MOVE_OUT")//交割出去的数量
	private BigDecimal qtyMoveOut;
	
	@Column(name="QTY_MOVE_IN")//从其他车间交割来的数量
	private BigDecimal qtyMoveIn;
	
	@Column(name="QTY_SCRAP")//报废的数量
	private BigDecimal qtyScrap;
	
	@Column(name="QTY_USED")//生产使用数量
	private BigDecimal qtyUsed;
	
	@Transient
	private BigDecimal qtyNow;//现存数 =期初值+生产数+交割入-交割出-生产使用

	public long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getWorkCenter() {
		return workCenter;
	}

	public void setWorkCenter(String workCenter) {
		this.workCenter = workCenter;
	}

	public BigDecimal getQtyMoveOut() {
		return qtyMoveOut;
	}

	public void setQtyMoveOut(BigDecimal qtyMoveOut) {
		this.qtyMoveOut = qtyMoveOut;
	}

	public BigDecimal getQtyMoveIn() {
		return qtyMoveIn;
	}

	public void setQtyMoveIn(BigDecimal qtyMoveIn) {
		this.qtyMoveIn = qtyMoveIn;
	}

	public BigDecimal getQtyScrap() {
		return qtyScrap;
	}

	public void setQtyScrap(BigDecimal qtyScrap) {
		this.qtyScrap = qtyScrap;
	}

	public BigDecimal getQtyProduce() {
		return qtyProduce;
	}

	public void setQtyProduce(BigDecimal qtyProduce) {
		this.qtyProduce = qtyProduce;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getMaterialId() {
		if(material != null){
			materialId = material.getMaterialId();
		}else{
			materialId = "";
		}
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialName() {
		if(material != null){
			materialName = material.getName();
		}else{
			materialName = "";
		}
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public BigDecimal getQtyUsed() {
		return qtyUsed;
	}

	public void setQtyUsed(BigDecimal qtyUsed) {
		this.qtyUsed = qtyUsed;
	}

	public BigDecimal getQtyNow() {
		if(qtyInitial == null){
			qtyInitial = BigDecimal.ZERO;
		}
		if(qtyProduce == null){
			qtyProduce = BigDecimal.ZERO;
		}
		if(qtyMoveIn == null){
			qtyMoveIn = BigDecimal.ZERO;
		}
		if(qtyMoveOut == null){
			qtyMoveOut = BigDecimal.ZERO;
		}
		if(qtyUsed == null){
			qtyUsed = BigDecimal.ZERO;
		}
		qtyNow = qtyInitial.add(qtyProduce).add(qtyMoveIn).subtract(qtyMoveOut).subtract(qtyUsed);
		return qtyNow;
	}

	public void setQtyNow(BigDecimal qtyNow) {
		this.qtyNow = qtyNow;
	}

	public BigDecimal getQtyInitial() {
		return ( qtyInitial == null ? BigDecimal.ZERO : qtyInitial );
	}

	public void setQtyInitial(BigDecimal qtyInitial) {
		this.qtyInitial = ( qtyInitial == null ? BigDecimal.ZERO : qtyInitial );
	}
}
