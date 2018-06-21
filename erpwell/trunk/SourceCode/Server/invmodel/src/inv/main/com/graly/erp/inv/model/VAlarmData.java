package com.graly.erp.inv.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;
//质检提醒报警视图，目的：防止每次添加一个信息必须修改手持和系统
@Entity
@Table(name="V_ALARM_DATA")
public class VAlarmData extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	public static String STATUS_OPEN = "OPEN";
	public static String STATUS_CLOSE = "CLOSE";

	@Column(name="ALARM_TYPE")
	private String alarmType;//警报类型
	
	@Column(name="USER_RRN")
	private Long userRrn;//给予警报的用户
	
	@Column(name="OWNER")
	private String owner;//警报拥有者
	
	@Column(name="START_TIME")
	private Date startTime;//警报开始时间

	@Column(name="CLOSE_TIME")
	private Date closeTime;//警报关闭时间
	
	@Column(name="CLOSER")
	private Long closer;//关闭人
	
	@Column(name="STATUS")
	private String status;//状态
	
	@Column(name="COMMENTS")
	private String comments;//备注
	
	@Column(name="FIELD1")
	private String field1;//收货单号
	
	@Column(name="FIELD2")
	private String field2;//采购订单号
	
	@Column(name="FIELD3")
	private String field3;//检验单号
	
	@Column(name="FIELD4")
	private String field4;//检验周期
	
	@Column(name="FIELD5")
	private String field5;//物料编号
	
	@Column(name="FIELD6")
	private String field6;//物料名称
	
	@Column(name="FIELD7")
	private String field7;//是否允许采购入库：y是,N否,空值，代表没处理
	
	@Column(name="FIELD8")
	private String field8;//收货数量
	
	@Column(name="FIELD9")
	private String field9;//采购订单备注
	
	@Column(name="FIELD10")
	private String field10;
	
	@Column(name="FIELD11")
	private Long field11;//收货单RRN
	
	@Column(name="FIELD12")
	private Long field12;//采购入库单RRN
	
	@Column(name="FIELD13")
	private Date field13;//制单时间(收货单)
	
	@Column(name="FIELD14")
	private Long field14;//检验单RRn
	
	@Column(name="FIELD15")
	private Long field15;
	
	@Column(name="FIELD16")
	private Date field16;
	
	@Column(name="FIELD17")
	private Long field17;//同意/不同意人
	
	@Column(name="FIELD18")
	private String field18;//同意/不同意历史
	
	@Column(name="FIELD19")
	private String field19;//同意/不同意人历史
	
	@Column(name="FIELD20")
	private String field20;//是否调拨:Y/N
	
	@Column(name="FIELD21")
	private String field21;//质检备注
	
	@Column(name="FIELD22")
	private String field22;//供应商ID
	
	@Column(name="FIELD23")
	private String field23;//检验数
	
	@Column(name="FIELD24")//合格数
	private String field24;

	@Column(name="FIELD25")//紧急程度
	private String field25;
	
	@Column(name="FIELD26")//供应商名称
	private String field26;
	
	@Column(name="FIELD27")//订单数
	private String field27;
	
	@Column(name="FIELD28")//预留字段
	private String field28;
	
	@Column(name="FIELD29")//预留字段
	private String field29;
	
	@Column(name="FIELD30")//预留字段
	private String field30;
	
	@Column(name="FIELD31")//行rrn
	private Long field31;
	
	@Transient
	private int reservedField1;
	
	@Transient
	private String reservedField2;
	
	@Transient
	private String reservedField3;
	
	@Transient
	private String reservedField4;
	
	@Column(name="IQC_DEPT")//质检部门
	private String iqcDept;
	
	@Column(name="WMS_WAREHOUSE")//质检部门
	private String wmsWarehouse;
	
	public VAlarmData() {
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public Long getCloser() {
		return closer;
	}

	public void setCloser(Long closer) {
		this.closer = closer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		this.field7 = field7;
	}

	public String getField8() {
		return field8;
	}

	public void setField8(String field8) {
		this.field8 = field8;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		this.field9 = field9;
	}

	public String getField10() {
		return field10;
	}

	public void setField10(String field10) {
		this.field10 = field10;
	}

	public Long getField11() {
		return field11;
	}

	public void setField11(Long field11) {
		this.field11 = field11;
	}

	public Long getField12() {
		return field12;
	}

	public void setField12(Long field12) {
		this.field12 = field12;
	}

	public Date getField13() {
		return field13;
	}

	public void setField13(Date field13) {
		this.field13 = field13;
	}

	public Long getUserRrn() {
		return userRrn;
	}

	public void setUserRrn(Long userRrn) {
		this.userRrn = userRrn;
	}

	public Long getField14() {
		return field14;
	}

	public void setField14(Long field14) {
		this.field14 = field14;
	}

	public Long getField15() {
		return field15;
	}

	public void setField15(Long field15) {
		this.field15 = field15;
	}

	public Date getField16() {
		return field16;
	}

	public void setField16(Date field16) {
		this.field16 = field16;
	}

	public Long getField17() {
		return field17;
	}

	public void setField17(Long field17) {
		this.field17 = field17;
	}

	public String getField18() {
		return field18;
	}

	public void setField18(String field18) {
		this.field18 = field18;
	}

	public String getField19() {
		return field19;
	}

	public void setField19(String field19) {
		this.field19 = field19;
	}

	public String getField20() {
		return field20;
	}

	public void setField20(String field20) {
		this.field20 = field20;
	}

	public String getField21() {
		return field21;
	}

	public void setField21(String field21) {
		this.field21 = field21;
	}

	public String getField22() {
		return field22;
	}

	public void setField22(String field22) {
		this.field22 = field22;
	}

	public String getField23() {
		return field23;
	}

	public void setField23(String field23) {
		this.field23 = field23;
	}

	public String getField24() {
		return field24;
	}

	public void setField24(String field24) {
		this.field24 = field24;
	}

	public String getField25() {
		return field25;
	}

	public void setField25(String field25) {
		this.field25 = field25;
	}

	public String getField26() {
		return field26;
	}

	public void setField26(String field26) {
		this.field26 = field26;
	}

	public String getField27() {
		return field27;
	}

	public void setField27(String field27) {
		this.field27 = field27;
	}

	public String getField28() {
		return field28;
	}

	public void setField28(String field28) {
		this.field28 = field28;
	}

	public String getField29() {
		return field29;
	}

	public void setField29(String field29) {
		this.field29 = field29;
	}

	public String getField30() {
		return field30;
	}

	public void setField30(String field30) {
		this.field30 = field30;
	}

	public int getReservedField1() {
		return reservedField1;
	}

	public void setReservedField1(int reservedField1) {
		this.reservedField1 = reservedField1;
	}

	public String getReservedField2() {
		return reservedField2;
	}

	public void setReservedField2(String reservedField2) {
		this.reservedField2 = reservedField2;
	}

	public String getReservedField3() {
		return reservedField3;
	}

	public void setReservedField3(String reservedField3) {
		this.reservedField3 = reservedField3;
	}

	public String getReservedField4() {
		return reservedField4;
	}

	public void setReservedField4(String reservedField4) {
		this.reservedField4 = reservedField4;
	}

	public Long getField31() {
		return field31;
	}

	public void setField31(Long field31) {
		this.field31 = field31;
	}

	public String getIqcDept() {
		return iqcDept;
	}

	public void setIqcDept(String iqcDept) {
		this.iqcDept = iqcDept;
	}

	public String getWmsWarehouse() {
		return wmsWarehouse;
	}

	public void setWmsWarehouse(String wmsWarehouse) {
		this.wmsWarehouse = wmsWarehouse;
	}
}