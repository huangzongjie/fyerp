package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="WORK_SHOP_SCHEDULE_HIS")
public class WorkShopScheduleHis extends ADUpdatable{
	
	private static final long serialVersionUID = 1L;
	public static String TRANS_TYPE_SAVE = "SAVE";
	public static String TRANS_TYPE_DELETE = "DELETE";
	
		@Column(name="SCHEDULE_RRN")
		private Long scheduleRrn;//�Ų�����
	
		@Column(name="SCHEDULE_DATE")
		private Date scheduleDate;//�Ų�����
		
		@Column(name="WS_DATE_DELIVERY")
		private String wsDateDelivery;//���佻������
		
		@Column(name="DATE_PLAN")
		private Date datePlan;//�ƻ�������
		
		@Column(name="MO_ID")
		private String moId;
		
		@Column(name="MATERIAL_ID")
		private String materialId;

		@Column(name="QTY_PRODUCT")
		private BigDecimal qtyProcuct;//��������
		
		@Column(name="QTY_RECEIVE")
		private BigDecimal qtyReceive;//ͳ�����н��յ�����
		
		@Column(name="QTY_PLAN_PRODUCT")
		private BigDecimal qtyPlanProcuct;//�ƻ���������
		
		@Column(name="CUSTOMER")
		private String customer;//�ͻ�
		
		@Column(name="WORK_CENTER")
		private String workCenter;//���Ƴ���
		
		@Column(name="WORK_CENTER2")
		private String workCenter2;//���Ʒ�����
		
		@Column(name="WORK_CENTER3")
		private String workCenter3;//�и��
		
		@Column(name="WORK_CENTER4")
		private String workCenter4;//ֽ������
		
		@Column(name="WORK_CENTER5")
		private String workCenter5;//��������
		
		@Column(name="WORK_CENTER6")
		private String workCenter6;//���ܳ���
		
		@Column(name="DOC_STATUS")
		private String docStatus="DRAFTED";//�Ƿ�ʼ
		
		@Column(name="WORKCENTER_ID")
		private String workcenterId;
		
		@Column(name="COMMENTS")
		private String comments;
		
		@Column(name="USER_CREATED")
		private String userCreated;
		
		@Column(name="DELAY_REASON")
		private String delayReason;//���ԭ��

		@Column(name="DELAY_DEPT")
		private String delayDept;//����
		
		@Column(name="DELAY_REASON_DETAIL")
		private String delayReasonDetail;//���ԭ����ϸ
		
		@Column(name="TRANS_TYPE")
		private String transType;
		
		public WorkShopScheduleHis(WorkShopSchedule schedule ){
			if(schedule!=null){
				this.setScheduleRrn(schedule.getObjectRrn());
				this.setScheduleDate(schedule.getScheduleDate());
				this.setWsDateDelivery(schedule.getWsDateDelivery());
				this.setDatePlan(schedule.getDatePlan());
				this.setMoId(schedule.getMoId());
				this.setMaterialId(schedule.getMaterialId());
				this.setQtyProcuct(schedule.getQtyProcuct());
				this.setQtyReceive(schedule.getQtyReceive());
				this.setQtyPlanProcuct(schedule.getQtyPlanProcuct());
				this.setCustomer(schedule.getCustomer());
				this.setComments(schedule.getComments());
				this.setWorkCenter(schedule.getWorkCenter());
				this.setWorkCenter2(schedule.getWorkCenter2());
				this.setWorkCenter3(schedule.getWorkCenter3());
				this.setWorkCenter4(schedule.getWorkCenter4());
				this.setWorkCenter6(schedule.getWorkCenter6());
				this.setDocStatus(schedule.getDocStatus());
				this.setWorkcenterId(schedule.getWorkcenterId());
				this.setComments(schedule.getComments());
				this.setUserCreated(schedule.getUserCreated());
				this.setDelayReason(schedule.getDelayReason());
				this.setDelayDept(schedule.getDelayDept());
				this.setDelayReasonDetail(schedule.getDelayReasonDetail());
			}
		}
		
		public Long getScheduleRrn() {
			return scheduleRrn;
		}

		public void setScheduleRrn(Long scheduleRrn) {
			this.scheduleRrn = scheduleRrn;
		}

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

		public String getMaterialId() {
			return materialId;
		}

		public void setMaterialId(String materialId) {
			this.materialId = materialId;
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

		public String getTransType() {
			return transType;
		}

		public void setTransType(String transType) {
			this.transType = transType;
		}
}
