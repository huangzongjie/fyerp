package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;
//常规物料需求警报
@Entity
@Table(name="PDM_MATERIAL_CONVENTIONAL")
public class MaterialConventional extends ADUpdatable {
	 
	private static final long serialVersionUID = 1L;

	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	@Column(name="MATERIAL_Name")
	private String materialName;
	
	@Column(name="NEXT_QTY")//下周需求
	private BigDecimal nextQty;
	
	@Column(name="NEXT_QTY2")//下周需求
	private BigDecimal nextQty2;
	
	@Column(name="NEXT_QTY3")//下周需求
	private BigDecimal nextQty3;
	
	@Column(name="NEXT_QTY4")//下周需求
	private BigDecimal nextQty4;
	
	@Column(name="NEXT_QTY5")//下周需求
	private BigDecimal nextQty5;
	
	@Column(name="NEXT_QTY6")//下周需求
	private BigDecimal nextQty6;
	
	@Column(name="NEXT_QTY7")//下周需求
	private BigDecimal nextQty7;
	
	@Column(name="NEXT_QTY8")//下周需求
	private BigDecimal nextQty8;
	
	@Column(name="NEXT_QTY9")//下周需求
	private BigDecimal nextQty9;
	
	@Column(name="NEXT_QTY10")//下周需求
	private BigDecimal nextQty10;
	
	@Column(name="NEXT_QTY11")//下周需求
	private BigDecimal nextQty11;
	
	@Transient
	private BigDecimal nextQtyTotal;
	
	@Column(name="COMMENTS")
	private String comments;
	
	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public BigDecimal getNextQty() {
		return nextQty;
	}

	public void setNextQty(BigDecimal nextQty) {
		this.nextQty = nextQty;
	}

	public BigDecimal getNextQty2() {
		return nextQty2;
	}

	public void setNextQty2(BigDecimal nextQty2) {
		this.nextQty2 = nextQty2;
	}

	public BigDecimal getNextQty3() {
		return nextQty3;
	}

	public void setNextQty3(BigDecimal nextQty3) {
		this.nextQty3 = nextQty3;
	}

	public BigDecimal getNextQty4() {
		return nextQty4;
	}

	public void setNextQty4(BigDecimal nextQty4) {
		this.nextQty4 = nextQty4;
	}

	public BigDecimal getNextQty5() {
		return nextQty5;
	}

	public void setNextQty5(BigDecimal nextQty5) {
		this.nextQty5 = nextQty5;
	}

	public BigDecimal getNextQty6() {
		return nextQty6;
	}

	public void setNextQty6(BigDecimal nextQty6) {
		this.nextQty6 = nextQty6;
	}

	public BigDecimal getNextQty7() {
		return nextQty7;
	}

	public void setNextQty7(BigDecimal nextQty7) {
		this.nextQty7 = nextQty7;
	}

	public BigDecimal getNextQty8() {
		return nextQty8;
	}

	public void setNextQty8(BigDecimal nextQty8) {
		this.nextQty8 = nextQty8;
	}

	public BigDecimal getNextQty9() {
		return nextQty9;
	}

	public void setNextQty9(BigDecimal nextQty9) {
		this.nextQty9 = nextQty9;
	}

	public BigDecimal getNextQty10() {
		return nextQty10;
	}

	public void setNextQty10(BigDecimal nextQty10) {
		this.nextQty10 = nextQty10;
	}

	public BigDecimal getNextQty11() {
		return nextQty11;
	}

	public void setNextQty11(BigDecimal nextQty11) {
		this.nextQty11 = nextQty11;
	}
	
	@Transient//可分配数
	public BigDecimal getNextQtyTotal() {
		
		BigDecimal nextQty = this.nextQty!=null?this.nextQty:BigDecimal.ZERO;
		BigDecimal nextQty2 = this.nextQty2!=null?this.nextQty2:BigDecimal.ZERO;
		BigDecimal nextQty3 = this.nextQty3!=null?this.nextQty3:BigDecimal.ZERO;
		BigDecimal nextQty4 = this.nextQty4!=null?this.nextQty4:BigDecimal.ZERO;
		BigDecimal nextQty5 = this.nextQty5!=null?this.nextQty5:BigDecimal.ZERO;
		BigDecimal nextQty6 = this.nextQty6!=null?this.nextQty6:BigDecimal.ZERO;
		BigDecimal nextQty7 = this.nextQty7!=null?this.nextQty7:BigDecimal.ZERO;
		BigDecimal nextQty8 = this.nextQty8!=null?this.nextQty8:BigDecimal.ZERO;
		BigDecimal nextQty9 = this.nextQty9!=null?this.nextQty9:BigDecimal.ZERO;
		BigDecimal nextQty10 = this.nextQty10!=null?this.nextQty10:BigDecimal.ZERO;
		BigDecimal nextQty11 = this.nextQty11!=null?this.nextQty11:BigDecimal.ZERO;
		nextQtyTotal= nextQty.add(nextQty2).add(nextQty3).add(nextQty4).add(nextQty5).add(nextQty6).add(nextQty7).add(nextQty8)
		.add(nextQty9).add(nextQty10).add(nextQty11);
		return nextQtyTotal;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}