package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.model.Material;

@Entity
@Table(name="WIP_MO")
public class ManufactureOrder extends Documentation {
	private static final long serialVersionUID = 1L;
	public static String MOTYPE_P = "P";
	public static String MOTYPE_A = "A";
	public static String MOTYPE_B = "B";
	
	@Column(name="MO_TYPE")
	private String moType;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;

	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY_PRODUCT")
	private BigDecimal qtyProduct = new BigDecimal("0.0");

	@Column(name="QTY_RECEIVE")
	private BigDecimal qtyReceive = new BigDecimal("0.0");

	@Column(name="QTY_IN")
	private BigDecimal qtyIn= new BigDecimal("0.0");
	
	@Column(name="DATE_START")
	private Date dateStart;
	
	@Column(name="DATE_END")
	private Date dateEnd;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="DATE_DELIVERY")
	private Date dateDelivery;
	
	@Column(name="USER_CREATED")
	private String userCreated;
	
	@Column(name="USER_APPROVED")
	private String userApproved;

	@Column(name="MPS_RRN")
	private Long mpsRrn;
	
	@Column(name="MPS_ID")
	private String mpsId;
	
	@Column(name="MPS_LINE_RRN")
	private Long mpsLineRrn;
	
	@Column(name="TPS_RRN")
	private Long tpsRrn;

	@Column(name="WORKCENTER_RRN")
	private Long workCenterRrn;
	
	@Column(name="WORKCENTER_ID")
	private String workCenterId;
	
	@Column(name="DATE_PLAN_SATRT")
	private Date datePlanStart;
	
	@Column(name="DATE_PLAN_END")
	private Date datePlanEnd;
	
	@Column(name="STAND_TIME")
	private BigDecimal standTime;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name = "SALEPLAN_TYPE")
	private String salePlanType;//销售类型
	
	@Column(name="CUSTOMER_NAME")//客户名称
	private String customerName;
	
	@Column(name="SALER")//业务员
	private String saler;
	
	@Column(name="ORDER_ID")//订单编号
	private String orderId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="HAS_PREPARE_MO_LINE")
	private String hasPrepareMoLine;//有取消了预处理的MO需要生成工作令
	
	@Column(name="IS_PREPARE_MO")
	private String isPrepareMo;//是否是预处理工作令
	
	@Column(name="HAS_FIRST_COUNT_BOM")
	private String hasFirstCountBOM;//已经第一次统计预处理工作令的在制数，在途数等等
	
	@Column(name="COUNT_PREPARE_BOM")
	private String countPrepareBom;//统计多少待处理物料
	
	@Column(name="PLAN_NOTICE_QTY")
	private Long planNoticeQty = 0L;//计划通知条数,提醒车间，童庆飞需求
	
	@Column(name = "PI_ID")
	private String piId;//pi编号
	
	@Column(name="DATE_ACTUAL")
	private Date dateActual;//曹惠峰实际计划时间
	
	@Column(name="REASON_ACTUAL")
	private String reasonActual;//曹惠峰实际原因
	
	@Column(name="CUSTOMER_MANAGER")//客户经理
	private String customerManager;
	
	public Long getTpsRrn() {
		return tpsRrn;
	}

	public void setTpsRrn(Long tpsRrn) {
		this.tpsRrn = tpsRrn;
	}

	public void setMoType(String moType) {
		this.moType = moType;
	}

	public String getMoType() {
		return moType;
	}
	
	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public String getMaterialName() {
		return materialName;
	}
	
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	
	public String getMaterialId() {
		if(material != null) {
			return material.getMaterialId();
		}
		return "";
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public BigDecimal getQtyProduct() {
		return qtyProduct;
	}

	public void setQtyProduct(BigDecimal qtyProduct) {
		this.qtyProduct = qtyProduct;
	}

	public BigDecimal getQtyReceive() {
		return qtyReceive;
	}

	public void setQtyReceive(BigDecimal qtyReceive) {
		this.qtyReceive = qtyReceive;
	}

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public String getUserApproved() {
		return userApproved;
	}

	public void setUserApproved(String userApproved) {
		this.userApproved = userApproved;
	}

	public void setMpsRrn(Long mpsRrn) {
		this.mpsRrn = mpsRrn;
	}

	public Long getMpsRrn() {
		return mpsRrn;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public String getMpsId() {
		return mpsId;
	}

	public void setMpsLineRrn(Long mpsLineRrn) {
		this.mpsLineRrn = mpsLineRrn;
	}

	public Long getMpsLineRrn() {
		return mpsLineRrn;
	}

	public void setWorkCenterId(String workCenterId) {
		this.workCenterId = workCenterId;
	}

	public String getWorkCenterId() {
		return workCenterId;
	}

	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}

	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}

	public Date getDateDelivery() {
		return dateDelivery;
	}

	public void setDateDelivery(Date dateDelivery) {
		this.dateDelivery = dateDelivery;
	}

	public Date getDatePlanStart() {
		return datePlanStart;
	}

	public void setDatePlanStart(Date datePlanStart) {
		this.datePlanStart = datePlanStart;
	}

	public Date getDatePlanEnd() {
		return datePlanEnd;
	}

	public void setDatePlanEnd(Date datePlanEnd) {
		this.datePlanEnd = datePlanEnd;
	}

	public BigDecimal getStandTime() {
		return standTime;
	}

	public void setStandTime(BigDecimal standTime) {
		this.standTime = standTime;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getSalePlanType() {
		return salePlanType;
	}

	public void setSalePlanType(String salePlanType) {
		this.salePlanType = salePlanType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSaler() {
		return saler;
	}

	public void setSaler(String saler) {
		this.saler = saler;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Boolean getHasPrepareMoLine() {
		return "Y".equalsIgnoreCase(this.hasPrepareMoLine) ? true : false; 
	}

	public void setHasPrepareMoLine(Boolean hasPrepareMoLine) {
		this.hasPrepareMoLine = hasPrepareMoLine ? "Y" : "N";
	}

	public Boolean getIsPrepareMo() {
		return "Y".equalsIgnoreCase(this.isPrepareMo) ? true : false; 
	}

	public void setIsPrepareMo(Boolean isPrepareMo) {
		this.isPrepareMo = isPrepareMo ? "Y" : "N";
	}
	
	public Boolean getHasFirstCountBOM() {
		return "Y".equalsIgnoreCase(this.hasFirstCountBOM) ? true : false; 
	}

	public void setHasFirstCountBOM(Boolean hasFirstCountBOM) {
		this.hasFirstCountBOM = hasFirstCountBOM ? "Y" : "N";
	}

	public String getCountPrepareBom() {
		return countPrepareBom;
	}

	public void setCountPrepareBom(String countPrepareBom) {
		this.countPrepareBom = countPrepareBom;
	}

	public Long getPlanNoticeQty() {
		return planNoticeQty==null?0L:planNoticeQty;
	}

	public void setPlanNoticeQty(Long planNoticeQty) {
		this.planNoticeQty = planNoticeQty;
	}

	public String getPiId() {
		return piId;
	}

	public void setPiId(String piId) {
		this.piId = piId;
	}

	public Date getDateActual() {
		return dateActual;
	}

	public void setDateActual(Date dateActual) {
		this.dateActual = dateActual;
	}

	public String getReasonActual() {
		return reasonActual;
	}

	public void setReasonActual(String reasonActual) {
		this.reasonActual = reasonActual;
	}

	public String getCustomerManager() {
		return customerManager;
	}

	public void setCustomerManager(String customerManager) {
		this.customerManager = customerManager;
	}
}
