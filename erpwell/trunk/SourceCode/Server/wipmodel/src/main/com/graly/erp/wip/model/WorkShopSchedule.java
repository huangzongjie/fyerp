package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="WORK_SHOP_SCHEDULE")
public class WorkShopSchedule extends ADUpdatable{
	
	private static final long serialVersionUID = 1L;
	public static String DOC_STATUS_DRAFTED="DRAFTED";
	public static String DOC_STATUS_START="START";
	public static String DOC_STATUS_COMPLETED="COMPLETED";
	public static String DOC_STATUS_SUSPEND="SUSPEND";
	public static String DOC_STATUS_SHORT="SHORT";
	
		@Column(name="SCHEDULE_DATE")
		private Date scheduleDate;//排产日期
		
		@Column(name="WS_DATE_DELIVERY")
		private String wsDateDelivery;//车间交货日期
		
		@Column(name="DATE_PLAN")
		private Date datePlan;//计划交货期
		
		@Column(name="MO_ID")
		private String moId;
		
		@Column(name="MO_RRN")
		private Long moRrn;
		
		@Column(name="MO_LINE_RRN")
		private Long moLineRrn;
		
		@Column(name="MATERIAL_ID")
		private String materialId;
		
		@Column(name="MATERIAL_NAME")
		private String materialName;
		
		@Column(name="QTY_PRODUCT")
		private BigDecimal qtyProcuct;//生产数量
		
		@Column(name="QTY_RECEIVE")
		private BigDecimal qtyReceive;//统计所有接收的数量
		
		@Column(name="QTY_PLAN_PRODUCT")
		private BigDecimal qtyPlanProcuct;//计划生产数量
		
		@Column(name="CUSTOMER")
		private String customer;//客户
		
		@Column(name="WORK_CENTER")
		private String workCenter;//缠绕车间
		
		@Column(name="WORK_CENTER2")
		private String workCenter2;//控制阀车间
		
		@Column(name="WORK_CENTER3")
		private String workCenter3;//切割车间
		
		@Column(name="WORK_CENTER4")
		private String workCenter4;//纸箱物料
		
		@Column(name="WORK_CENTER5")
		private String workCenter5;//射流器管
		
		@Column(name="WORK_CENTER6")
		private String workCenter6;//吹塑车间
		
//		@Column(name="IS_COMPLETED")
//		private String isCompleted="N";//是否完成
		
//		@Column(name="IS_SHORT_MOLD")
//		private String isShortMold="N";//是否缺料
//		
//		@Column(name="IS_START")
//		private String isStart="N";//是否开始
//		
//		@Column(name="IS_SUSPEND")
//		private String isSuspend="N";//是否暂停
		
		@Column(name="DOC_STATUS")
		private String docStatus="DRAFTED";//是否开始
		
		@Column(name="WORKCENTER_ID")
		private String workcenterId;
		
		@Column(name="WORKCENTER_RRN")
		private Long workcenterRrn;
		
		@Column(name="COMMENTS")
		private String comments;
		
		@Column(name="USER_CREATED")
		private String userCreated;
		
		@Column(name="DELAY_REASON")
		private String delayReason;//变更原因

		@Column(name="DELAY_DEPT")
		private String delayDept;//部门
		
		@Column(name="DELAY_REASON_DETAIL")
		private String delayReasonDetail;//变更原因详细
		
		@Column(name="MATERIAL_RRN")
		private Long materialRrn;//部门

		@Column(name="WC_COLOR1")
		private String wcolor1;//颜色1
		
		@Column(name="WC_COLOR2")
		private String wcolor2;//颜色1
		
		@Column(name="WC_COLOR3")
		private String wcolor3;//颜色1
		
		@Column(name="WC_COLOR4")
		private String wcolor4;//颜色1
		
		@Column(name="WC_COLOR5")
		private String wcolor5;//颜色1
		
		@Column(name="WC_COLOR6")
		private String wcolor6;//颜色1
		
		public Date getScheduleDate() {
			return scheduleDate;
		}

		public void setScheduleDate(Date scheduleDate) {
			this.scheduleDate = scheduleDate;
		}

		public String getWsDateDelivery() {
			return wsDateDelivery;
		}

		public void setWsDateDelivery(String wsDateDelivery) {
			this.wsDateDelivery = wsDateDelivery;
		}

		public Date getDatePlan() {
			return datePlan;
		}

		public void setDatePlan(Date datePlan) {
			this.datePlan = datePlan;
		}

		public String getMoId() {
			return moId;
		}

		public void setMoId(String moId) {
			this.moId = moId;
		}

		public Long getMoRrn() {
			return moRrn;
		}

		public void setMoRrn(Long moRrn) {
			this.moRrn = moRrn;
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

		public BigDecimal getQtyPlanProcuct() {
			return qtyPlanProcuct;
		}

		public void setQtyPlanProcuct(BigDecimal qtyPlanProcuct) {
			this.qtyPlanProcuct = qtyPlanProcuct;
		}

		public String getWorkcenterId() {
			return workcenterId;
		}

		public void setWorkcenterId(String workcenterId) {
			this.workcenterId = workcenterId;
		}

		public Long getWorkcenterRrn() {
			return workcenterRrn;
		}

		public void setWorkcenterRrn(Long workcenterRrn) {
			this.workcenterRrn = workcenterRrn;
		}

		public String getComments() {
			return comments;
		}

		public void setComments(String comments) {
			this.comments = comments;
		}

		public String getCustomer() {
			return customer;
		}

		public void setCustomer(String customer) {
			this.customer = customer;
		}

		public String getWorkCenter() {
			return workCenter;
		}

		public void setWorkCenter(String workCenter) {
			this.workCenter = workCenter;
		}

		public String getWorkCenter2() {
			return workCenter2;
		}

		public void setWorkCenter2(String workCenter2) {
			this.workCenter2 = workCenter2;
		}

		public String getWorkCenter3() {
			return workCenter3;
		}

		public void setWorkCenter3(String workCenter3) {
			this.workCenter3 = workCenter3;
		}

		public String getWorkCenter4() {
			return workCenter4;
		}

		public void setWorkCenter4(String workCenter4) {
			this.workCenter4 = workCenter4;
		}
		
		 
		public String getUserCreated() {
			return userCreated;
		}

		public void setUserCreated(String userCreated) {
			this.userCreated = userCreated;
		}

		public String getWorkCenter5() {
			return workCenter5;
		}

		public void setWorkCenter5(String workCenter5) {
			this.workCenter5 = workCenter5;
		}

		public String getDelayReason() {
			return delayReason;
		}

		public void setDelayReason(String delayReason) {
			this.delayReason = delayReason;
		}

		public String getDelayDept() {
			return delayDept;
		}

		public void setDelayDept(String delayDept) {
			this.delayDept = delayDept;
		}

		public String getDelayReasonDetail() {
			return delayReasonDetail;
		}

		public void setDelayReasonDetail(String delayReasonDetail) {
			this.delayReasonDetail = delayReasonDetail;
		}

		public String getWorkCenter6() {
			return workCenter6;
		}

		public void setWorkCenter6(String workCenter6) {
			this.workCenter6 = workCenter6;
		}

		public String getDocStatus() {
			return docStatus;
		}

		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

		public Long getMaterialRrn() {
			return materialRrn;
		}

		public void setMaterialRrn(Long materialRrn) {
			this.materialRrn = materialRrn;
		}

		public String getWcolor1() {
			return wcolor1;
		}

		public void setWcolor1(String wcolor1) {
			this.wcolor1 = wcolor1;
		}

		public String getWcolor2() {
			return wcolor2;
		}

		public void setWcolor2(String wcolor2) {
			this.wcolor2 = wcolor2;
		}

		public String getWcolor3() {
			return wcolor3;
		}

		public void setWcolor3(String wcolor3) {
			this.wcolor3 = wcolor3;
		}

		public String getWcolor4() {
			return wcolor4;
		}

		public void setWcolor4(String wcolor4) {
			this.wcolor4 = wcolor4;
		}

		public String getWcolor5() {
			return wcolor5;
		}

		public void setWcolor5(String wcolor5) {
			this.wcolor5 = wcolor5;
		}

		public String getWcolor6() {
			return wcolor6;
		}

		public void setWcolor6(String wcolor6) {
			this.wcolor6 = wcolor6;
		}

		public Long getMoLineRrn() {
			return moLineRrn;
		}

		public void setMoLineRrn(Long moLineRrn) {
			this.moLineRrn = moLineRrn;
		}
}
