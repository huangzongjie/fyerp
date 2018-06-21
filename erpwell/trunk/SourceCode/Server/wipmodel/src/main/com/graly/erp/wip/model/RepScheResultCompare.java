package com.graly.erp.wip.model;


import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;
//������������ǰһ������2��Ա�  �� 4�ż���4��5��  3�ż����4.5��
@Entity
@Table(name="V_REP_PO_RESULT_COMPARE")
public class RepScheResultCompare extends ADBase{
	private static final long serialVersionUID = 1L;
	
 
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	 
	@Column(name="ONE_QTY")
	private BigDecimal oneQty;//�ѷ�����
	
	@Column(name="TWO_QTY")
	private BigDecimal twoQty;//�ѷ�����
	
	@Column(name="THREE_QTY")
	private BigDecimal threeQty;//�ѷ�����
	
	@Column(name="FOUR_QTY")
	private BigDecimal fourQty;//�ѷ�����

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

	public BigDecimal getOneQty() {
		return oneQty;
	}

	public void setOneQty(BigDecimal oneQty) {
		this.oneQty = oneQty;
	}

	public BigDecimal getTwoQty() {
		return twoQty;
	}

	public void setTwoQty(BigDecimal twoQty) {
		this.twoQty = twoQty;
	}

	public BigDecimal getThreeQty() {
		return threeQty;
	}

	public void setThreeQty(BigDecimal threeQty) {
		this.threeQty = threeQty;
	}

	public BigDecimal getFourQty() {
		return fourQty;
	}

	public void setFourQty(BigDecimal fourQty) {
		this.fourQty = fourQty;
	}
	
}
