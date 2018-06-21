package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.base.model.Storage;

@Entity
@Table(name="WIP_MO_LINE")
public class ManufactureOrderLine extends DocumentationLine {
	private static final long serialVersionUID = 1L;
	
	public static final String WORK_STATUS_RUNNING = "RUNNING";
	public static final String WORK_STATUS_SUSPENED = "SUSPENED";
	public static final String WORK_STATUS_CLOSED = "CLOSED";
	public static final String WORK_STATUS_COMPLETED = "COMPLETED";
	public static final String WORK_STATUS_MERGED = "MERGED";
	
	
	@Column(name="MASTER_MO_RRN")
	private Long masterMoRrn;
	
	@Column(name="MASTER_MO_ID")
	private String masterMoId;
	
	@Column(name="PARENT_MO_RRN")
	private Long parentMoRrn;

	@Column(name="QTY_RECEIVE")
	private BigDecimal qtyReceive = BigDecimal.ZERO;
	
	@Column(name="QTY_NEED")
	private BigDecimal qtyNeed;
	
	@Column(name="WORKCENTER_RRN")
	private Long workCenterRrn;
	
	@Column(name="PATH")
	private String path;
	
	@Column(name="PATH_LEVEL")
	private Long pathLevel;
	
	@Column(name="WORK_STATUS")
	private String workStatus;
	
	@Column(name="DATE_START_ACTUAL")
	private Date dateStartActual;
	
	@Column(name="DATE_END_ACTUAL")
	private Date dateEndActual;
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation;
	
	@Column(name="QTY_ONHAND")
	private BigDecimal qtyOnHand;
	
	@Transient
	private Long moBomRrn;
	
	@Transient
	private BigDecimal qtyCurrentAllocation;
	
	@Transient
	private BigDecimal qtyCurrentOnHand;
	
	@Column(name = "SALEPLAN_TYPE")
	private String salePlanType;//销售类型
	
	@Column(name="CUSTOMER_NAME")//客户名称
	private String customerName;
	
	@Column(name="SALER")//业务员
	private String saler;
	
	@Column(name="ORDER_ID")//订单编号
	private String orderId;
	
	@Column(name="MATERIAL_NAME")//物料名称
	private String materialName;
	
	@Column(name="MERGE_BY")
	private Long mergeBy;
	
	@Column(name="MERGE_NEW_RRN")//合并后生成的新子mo的objectRrn
	private Long mergeNewRrn;
	
	@Column(name="DATE_MERGE")//合并时间
	private Date dateMerge;
	
	@Column(name="DATE_UNMERGE")//合并时间
	private Date dateUnMerge;
	
	@Column(name="UNMERGE_BY")
	private Long unMergeBy;
	
	@Column(name="LINE_UID", insertable=false, updatable=false)
	private Long lineUid;
	
    @OneToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="LINE_UID", insertable=true, updatable=false)
    private UidSequence uid = new UidSequence();
	
	@Column(name="PLAN_NOTICE_QTY")
	private Long planNoticeQty = 0L;//计划通知条数,提醒车间，童庆飞需求
	
	@Transient
	private BigDecimal qtyReserved1;//暂时用于立体库处理半仓品入库处理
	
	@Transient
	private BigDecimal qtyReserved2;//暂时用于立体库处理半仓品入库处理

	@Transient
	private String workCenterId;//暂时用于立体库处理半仓品入库处理
	
	@Transient
	private String receiptId;//暂时用于立体库处理半仓品入库处理
	
	@Column(name="PARENT_MATERIAL_NAME")//范总父物料名称
	private String parentMaterialName;
	
	@Column(name="PARENT_MATERIAL_ID")//父物料ID
	private String parentMaterialId;
	
	public Long getUid() {
		if(uid != null){
			return uid.getUid();
		}
		return null;
	}

	public void setUid(UidSequence uid) {
		this.uid = uid;
	}

	public Long getMasterMoRrn() {
		return masterMoRrn;
	}

	public void setMasterMoRrn(Long masterMoRrn) {
		this.masterMoRrn = masterMoRrn;
	}

	public String getMasterMoId() {
		return masterMoId;
	}

	public void setMasterMoId(String masterMoId) {
		this.masterMoId = masterMoId;
	}

	public Long getParentMoRrn() {
		return parentMoRrn;
	}

	public void setParentMoRrn(Long parentMoRrn) {
		this.parentMoRrn = parentMoRrn;
	}

	public BigDecimal getQtyReceive() {
		return qtyReceive;
	}

	public void setQtyReceive(BigDecimal qtyReceive) {
		this.qtyReceive = qtyReceive;
	}

	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}

	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPathLevel(Long pathLevel) {
		this.pathLevel = pathLevel;
	}

	public Long getPathLevel() {
		return pathLevel;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public BigDecimal getQtyNeed() {
		return qtyNeed;
	}

	public void setQtyNeed(BigDecimal qtyNeed) {
		this.qtyNeed = qtyNeed;
	}

	public void setMoBomRrn(Long moBomRrn) {
		this.moBomRrn = moBomRrn;
	}

	public Long getMoBomRrn() {
		return moBomRrn;
	}
	
	public Date getDateStartActual() {
		return dateStartActual;
	}

	public void setDateStartActual(Date dateStartActual) {
		this.dateStartActual = dateStartActual;
	}

	public Date getDateEndActual() {
		return dateEndActual;
	}

	public void setDateEndActual(Date dateEndActual) {
		this.dateEndActual = dateEndActual;
	}

	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}

	public void setQtyCurrentAllocation(BigDecimal qtyCurrentAllocation) {
		this.qtyCurrentAllocation = qtyCurrentAllocation;
	}

	public BigDecimal getQtyCurrentAllocation() {
		return qtyCurrentAllocation;
	}

	public void setQtyCurrentOnHand(BigDecimal qtyCurrentOnHand) {
		this.qtyCurrentOnHand = qtyCurrentOnHand;
	}

	public BigDecimal getQtyCurrentOnHand() {
		return qtyCurrentOnHand;
	}
	
	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}

	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}

	public void setQtyOnHand(BigDecimal qtyOnHand) {
		this.qtyOnHand = qtyOnHand;
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

//	public String getMaterialName() {
//		return materialName;
//	}

	public String getMaterialName() {
		if (this.getMaterial() != null) {
			return this.getMaterial().getName();
		}
		return "";
	}
	
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Long getMergeNewRrn() {
		return mergeNewRrn;
	}

	public void setMergeNewRrn(Long mergeNewRrn) {
		this.mergeNewRrn = mergeNewRrn;
	}

	public Date getDateMerge() {
		return dateMerge;
	}

	public void setDateMerge(Date dateMerge) {
		this.dateMerge = dateMerge;
	}

	public Date getDateUnMerge() {
		return dateUnMerge;
	}

	public void setDateUnMerge(Date dateUnMerge) {
		this.dateUnMerge = dateUnMerge;
	}

	public Long getMergeBy() {
		return mergeBy;
	}

	public void setMergeBy(Long mergeBy) {
		this.mergeBy = mergeBy;
	}

	public Long getUnMergeBy() {
		return unMergeBy;
	}

	public void setUnMergeBy(Long unMergeBy) {
		this.unMergeBy = unMergeBy;
	}

	public Long getLineUid() {
		return lineUid;
	}

	public void setLineUid(Long lineUid) {
		this.lineUid = lineUid;
	}

	public Long getPlanNoticeQty() {
		return planNoticeQty==null?0L:planNoticeQty;
	}

	public void setPlanNoticeQty(Long planNoticeQty) {
		this.planNoticeQty = planNoticeQty;
	}

	public BigDecimal getQtyReserved1() {
		return qtyReserved1;
	}

	public void setQtyReserved1(BigDecimal qtyReserved1) {
		this.qtyReserved1 = qtyReserved1;
	}

	public BigDecimal getQtyReserved2() {
		return qtyReserved2;
	}

	public void setQtyReserved2(BigDecimal qtyReserved2) {
		this.qtyReserved2 = qtyReserved2;
	}

	public String getWorkCenterId() {
		return workCenterId;
	}

	public void setWorkCenterId(String workCenterId) {
		this.workCenterId = workCenterId;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getParentMaterialName() {
		return parentMaterialName;
	}

	public void setParentMaterialName(String parentMaterialName) {
		this.parentMaterialName = parentMaterialName;
	}

	public String getParentMaterialId() {
		return parentMaterialId;
	}

	public void setParentMaterialId(String parentMaterialId) {
		this.parentMaterialId = parentMaterialId;
	}
}
