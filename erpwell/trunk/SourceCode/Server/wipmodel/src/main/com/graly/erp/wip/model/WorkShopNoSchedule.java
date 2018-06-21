package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="V_WORK_SHOP_NOSCHEDULE")
public class WorkShopNoSchedule extends ADUpdatable{
	
	private static final long serialVersionUID = 1L;

	  
		@Column(name="MO_ID")
		private String moId;
		
		@Column(name="MO_LINE_RRN")
		private Long moLineRrn;
		
		@Column(name="MATERIAL_ID")
		private String materialId;
		
		@Column(name="MATERIAL_NAME")
		private String materialName;
		
		@Column(name="QTY_NOPLAN")
		private BigDecimal qtyNoPlan;//计划生产数量
		
		@Column(name="QTY_PRODUCT")
		private BigDecimal qtyProcuct;//生产数量
		
		@Column(name="QTY_RECEIVE")
		private BigDecimal qtyReceive;//统计所有接收的数量
		
		@Column(name="CUSTOMER")
		private String customer;//客户
		
		@Column(name="WORKCENTER_ID")
		private String workcenterId;
		
		@Column(name="DATE_DELIVERY")
		private Date dateDelivery;//交货期
		
		public String getMoId() {
			return moId;
		}

		public void setMoId(String moId) {
			this.moId = moId;
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

		public BigDecimal getQtyNoPlan() {
			return qtyNoPlan;
		}

		public void setQtyNoPlan(BigDecimal qtyNoPlan) {
			this.qtyNoPlan = qtyNoPlan;
		}

		public BigDecimal getQtyProcuct() {
			return qtyProcuct;
		}

		public void setQtyProcuct(BigDecimal qtyProcuct) {
			this.qtyProcuct = qtyProcuct;
		}

		public BigDecimal getQtyReceive() {
			return qtyReceive;
		}

		public void setQtyReceive(BigDecimal qtyReceive) {
			this.qtyReceive = qtyReceive;
		}

		public String getCustomer() {
			return customer;
		}

		public void setCustomer(String customer) {
			this.customer = customer;
		}

		public String getWorkcenterId() {
			return workcenterId;
		}

		public void setWorkcenterId(String workcenterId) {
			this.workcenterId = workcenterId;
		}

		public Long getMoLineRrn() {
			return moLineRrn;
		}

		public void setMoLineRrn(Long moLineRrn) {
			this.moLineRrn = moLineRrn;
		}

		public Date getDateDelivery() {
			return dateDelivery;
		}

		public void setDateDelivery(Date dateDelivery) {
			this.dateDelivery = dateDelivery;
		}
}
