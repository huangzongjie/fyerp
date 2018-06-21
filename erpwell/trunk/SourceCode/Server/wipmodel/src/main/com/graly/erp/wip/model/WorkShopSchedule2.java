package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="WORK_SHOP_SCHEDULE2")
public class WorkShopSchedule2 extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	public static String DOC_STATUS_DRAFTED="DRAFTED";
	public static String DOC_STATUS_START="START";
	public static String DOC_STATUS_COMPLETED="COMPLETED";
	public static String DOC_STATUS_SUSPEND="SUSPEND";
	public static String DOC_STATUS_SHORT="SHORT";
	
		@Column(name="SCHEDULE_DATE")
		private Date scheduleDate;//排产日期
		
		@Column(name="DOC_STATUS")
		private String docStatus="DRAFTED";//是否开始
		
		@Column(name="WORKCENTER_ID")
		private String workcenterId;
		
		@Column(name="WORKCENTER_RRN")
		private Long workcenterRrn;
		
		@Column(name="MATERIAL_ID1")
		private String materialId1;
		@Column(name="MATERIAL_NAME1")
		private String materialName1;
		@Column(name="MATERIAL_QTY1")
		private BigDecimal materialQty1;
		@Column(name="MATERIAL_ID2")
		private String materialId2;
		@Column(name="MATERIAL_NAME2")
		private String materialName2;
		@Column(name="MATERIAL_QTY2")
		private BigDecimal materialQty2;
		@Column(name="MATERIAL_ID3")
		private String materialId3;
		@Column(name="MATERIAL_NAME3")
		private String materialName3;
		@Column(name="MATERIAL_QTY3")
		private BigDecimal materialQty3;
		@Column(name="MATERIAL_ID4")
		private String materialId4;
		@Column(name="MATERIAL_NAME4")
		private String materialName4;
		@Column(name="MATERIAL_QTY4")
		private BigDecimal materialQty4;
		@Column(name="MATERIAL_ID5")
		private String materialId5;
		@Column(name="MATERIAL_NAME5")
		private String materialName5;
		@Column(name="MATERIAL_QTY5")
		private BigDecimal materialQty5;
		@Column(name="MATERIAL_ID6")
		private String materialId6;
		@Column(name="MATERIAL_NAME6")
		private String materialName6;
		@Column(name="MATERIAL_QTY6")
		private BigDecimal materialQty6;
		@Column(name="MATERIAL_ID7")
		private String materialId7;
		@Column(name="MATERIAL_NAME7")
		private String materialName7;
		@Column(name="MATERIAL_QTY7")
		private BigDecimal materialQty7;
		@Column(name="MATERIAL_ID8")
		private String materialId8;
		@Column(name="MATERIAL_NAME8")
		private String materialName8;
		@Column(name="MATERIAL_QTY8")
		private BigDecimal materialQty8;
		@Column(name="MATERIAL_ID9")
		private String materialId9;
		@Column(name="MATERIAL_NAME9")
		private String materialName9;
		@Column(name="MATERIAL_QTY9")
		private BigDecimal materialQty9;
		@Column(name="MATERIAL_ID10")
		private String materialId10;
		@Column(name="MATERIAL_NAME10")
		private String materialName10;
		@Column(name="MATERIAL_QTY10")
		private BigDecimal materialQty10;
		@Column(name="MATERIAL_ID11")
		private String materialId11;
		@Column(name="MATERIAL_NAME11")
		private String materialName11;
		@Column(name="MATERIAL_QTY11")
		private BigDecimal materialQty11;
		@Column(name="MATERIAL_ID12")
		private String materialId12;
		@Column(name="MATERIAL_NAME12")
		private String materialName12;
		@Column(name="MATERIAL_QTY12")
		private BigDecimal materialQty12;
		@Column(name="MATERIAL_ID13")
		private String materialId13;
		@Column(name="MATERIAL_NAME13")
		private String materialName13;
		@Column(name="MATERIAL_QTY13")
		private BigDecimal materialQty13;
		@Column(name="MATERIAL_ID14")
		private String materialId14;
		@Column(name="MATERIAL_NAME14")
		private String materialName14;
		@Column(name="MATERIAL_QTY14")
		private BigDecimal materialQty14;
		@Column(name="MATERIAL_ID15")
		private String materialId15;
		@Column(name="MATERIAL_NAME15")
		private String materialName15;
		@Column(name="MATERIAL_QTY15")
		private BigDecimal materialQty15;
		@Column(name="MATERIAL_ID16")
		private String materialId16;
		@Column(name="MATERIAL_NAME16")
		private String materialName16;
		@Column(name="MATERIAL_QTY16")
		private BigDecimal materialQty16;
		@Column(name="MATERIAL_ID17")
		private String materialId17;
		@Column(name="MATERIAL_NAME17")
		private String materialName17;
		@Column(name="MATERIAL_QTY17")
		private BigDecimal materialQty17;
		@Column(name="MATERIAL_ID18")
		private String materialId18;
		@Column(name="MATERIAL_NAME18")
		private String materialName18;
		@Column(name="MATERIAL_QTY18")
		private BigDecimal materialQty18;
		@Column(name="MATERIAL_ID19")
		private String materialId19;
		@Column(name="MATERIAL_NAME19")
		private String materialName19;
		@Column(name="MATERIAL_QTY19")
		private BigDecimal materialQty19;
		@Column(name="MATERIAL_ID20")
		private String materialId20;
		@Column(name="MATERIAL_NAME20")
		private String materialName20;
		@Column(name="MATERIAL_QTY20")
		private BigDecimal materialQty20;
		@Column(name="MATERIAL_ID21")
		private String materialId21;
		@Column(name="MATERIAL_NAME21")
		private String materialName21;
		@Column(name="MATERIAL_QTY21")
		private BigDecimal materialQty21;
		@Column(name="MATERIAL_ID22")
		private String materialId22;
		@Column(name="MATERIAL_NAME22")
		private String materialName22;
		@Column(name="MATERIAL_QTY22")
		private BigDecimal materialQty22;
		public Date getScheduleDate() {
			return scheduleDate;
		}
		public void setScheduleDate(Date scheduleDate) {
			this.scheduleDate = scheduleDate;
		}
		public String getDocStatus() {
			return docStatus;
		}
		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
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
		public String getMaterialId1() {
			return materialId1;
		}
		public void setMaterialId1(String materialId1) {
			this.materialId1 = materialId1;
		}
		public String getMaterialName1() {
			return materialName1;
		}
		public void setMaterialName1(String materialName1) {
			this.materialName1 = materialName1;
		}
		public BigDecimal getMaterialQty1() {
			return materialQty1;
		}
		public void setMaterialQty1(BigDecimal materialQty1) {
			this.materialQty1 = materialQty1;
		}
		public String getMaterialId2() {
			return materialId2;
		}
		public void setMaterialId2(String materialId2) {
			this.materialId2 = materialId2;
		}
		public String getMaterialName2() {
			return materialName2;
		}
		public void setMaterialName2(String materialName2) {
			this.materialName2 = materialName2;
		}
		public BigDecimal getMaterialQty2() {
			return materialQty2;
		}
		public void setMaterialQty2(BigDecimal materialQty2) {
			this.materialQty2 = materialQty2;
		}
		public String getMaterialId3() {
			return materialId3;
		}
		public void setMaterialId3(String materialId3) {
			this.materialId3 = materialId3;
		}
		public String getMaterialName3() {
			return materialName3;
		}
		public void setMaterialName3(String materialName3) {
			this.materialName3 = materialName3;
		}
		public BigDecimal getMaterialQty3() {
			return materialQty3;
		}
		public void setMaterialQty3(BigDecimal materialQty3) {
			this.materialQty3 = materialQty3;
		}
		public String getMaterialId4() {
			return materialId4;
		}
		public void setMaterialId4(String materialId4) {
			this.materialId4 = materialId4;
		}
		public String getMaterialName4() {
			return materialName4;
		}
		public void setMaterialName4(String materialName4) {
			this.materialName4 = materialName4;
		}
		public BigDecimal getMaterialQty4() {
			return materialQty4;
		}
		public void setMaterialQty4(BigDecimal materialQty4) {
			this.materialQty4 = materialQty4;
		}
		public String getMaterialId5() {
			return materialId5;
		}
		public void setMaterialId5(String materialId5) {
			this.materialId5 = materialId5;
		}
		public String getMaterialName5() {
			return materialName5;
		}
		public void setMaterialName5(String materialName5) {
			this.materialName5 = materialName5;
		}
		public BigDecimal getMaterialQty5() {
			return materialQty5;
		}
		public void setMaterialQty5(BigDecimal materialQty5) {
			this.materialQty5 = materialQty5;
		}
		public String getMaterialId6() {
			return materialId6;
		}
		public void setMaterialId6(String materialId6) {
			this.materialId6 = materialId6;
		}
		public String getMaterialName6() {
			return materialName6;
		}
		public void setMaterialName6(String materialName6) {
			this.materialName6 = materialName6;
		}
		public BigDecimal getMaterialQty6() {
			return materialQty6;
		}
		public void setMaterialQty6(BigDecimal materialQty6) {
			this.materialQty6 = materialQty6;
		}
		public String getMaterialId7() {
			return materialId7;
		}
		public void setMaterialId7(String materialId7) {
			this.materialId7 = materialId7;
		}
		public String getMaterialName7() {
			return materialName7;
		}
		public void setMaterialName7(String materialName7) {
			this.materialName7 = materialName7;
		}
		public BigDecimal getMaterialQty7() {
			return materialQty7;
		}
		public void setMaterialQty7(BigDecimal materialQty7) {
			this.materialQty7 = materialQty7;
		}
		public String getMaterialId8() {
			return materialId8;
		}
		public void setMaterialId8(String materialId8) {
			this.materialId8 = materialId8;
		}
		public String getMaterialName8() {
			return materialName8;
		}
		public void setMaterialName8(String materialName8) {
			this.materialName8 = materialName8;
		}
		public BigDecimal getMaterialQty8() {
			return materialQty8;
		}
		public void setMaterialQty8(BigDecimal materialQty8) {
			this.materialQty8 = materialQty8;
		}
		public String getMaterialId9() {
			return materialId9;
		}
		public void setMaterialId9(String materialId9) {
			this.materialId9 = materialId9;
		}
		public String getMaterialName9() {
			return materialName9;
		}
		public void setMaterialName9(String materialName9) {
			this.materialName9 = materialName9;
		}
		public BigDecimal getMaterialQty9() {
			return materialQty9;
		}
		public void setMaterialQty9(BigDecimal materialQty9) {
			this.materialQty9 = materialQty9;
		}
		public String getMaterialId10() {
			return materialId10;
		}
		public void setMaterialId10(String materialId10) {
			this.materialId10 = materialId10;
		}
		public String getMaterialName10() {
			return materialName10;
		}
		public void setMaterialName10(String materialName10) {
			this.materialName10 = materialName10;
		}
		public BigDecimal getMaterialQty10() {
			return materialQty10;
		}
		public void setMaterialQty10(BigDecimal materialQty10) {
			this.materialQty10 = materialQty10;
		}
		public String getMaterialId11() {
			return materialId11;
		}
		public void setMaterialId11(String materialId11) {
			this.materialId11 = materialId11;
		}
		public String getMaterialName11() {
			return materialName11;
		}
		public void setMaterialName11(String materialName11) {
			this.materialName11 = materialName11;
		}
		public BigDecimal getMaterialQty11() {
			return materialQty11;
		}
		public void setMaterialQty11(BigDecimal materialQty11) {
			this.materialQty11 = materialQty11;
		}
		public String getMaterialId12() {
			return materialId12;
		}
		public void setMaterialId12(String materialId12) {
			this.materialId12 = materialId12;
		}
		public String getMaterialName12() {
			return materialName12;
		}
		public void setMaterialName12(String materialName12) {
			this.materialName12 = materialName12;
		}
		public BigDecimal getMaterialQty12() {
			return materialQty12;
		}
		public void setMaterialQty12(BigDecimal materialQty12) {
			this.materialQty12 = materialQty12;
		}
		public String getMaterialId13() {
			return materialId13;
		}
		public void setMaterialId13(String materialId13) {
			this.materialId13 = materialId13;
		}
		public String getMaterialName13() {
			return materialName13;
		}
		public void setMaterialName13(String materialName13) {
			this.materialName13 = materialName13;
		}
		public BigDecimal getMaterialQty13() {
			return materialQty13;
		}
		public void setMaterialQty13(BigDecimal materialQty13) {
			this.materialQty13 = materialQty13;
		}
		public String getMaterialId14() {
			return materialId14;
		}
		public void setMaterialId14(String materialId14) {
			this.materialId14 = materialId14;
		}
		public String getMaterialName14() {
			return materialName14;
		}
		public void setMaterialName14(String materialName14) {
			this.materialName14 = materialName14;
		}
		public BigDecimal getMaterialQty14() {
			return materialQty14;
		}
		public void setMaterialQty14(BigDecimal materialQty14) {
			this.materialQty14 = materialQty14;
		}
		public String getMaterialId15() {
			return materialId15;
		}
		public void setMaterialId15(String materialId15) {
			this.materialId15 = materialId15;
		}
		public String getMaterialName15() {
			return materialName15;
		}
		public void setMaterialName15(String materialName15) {
			this.materialName15 = materialName15;
		}
		public BigDecimal getMaterialQty15() {
			return materialQty15;
		}
		public void setMaterialQty15(BigDecimal materialQty15) {
			this.materialQty15 = materialQty15;
		}
		public String getMaterialId16() {
			return materialId16;
		}
		public void setMaterialId16(String materialId16) {
			this.materialId16 = materialId16;
		}
		public String getMaterialName16() {
			return materialName16;
		}
		public void setMaterialName16(String materialName16) {
			this.materialName16 = materialName16;
		}
		public BigDecimal getMaterialQty16() {
			return materialQty16;
		}
		public void setMaterialQty16(BigDecimal materialQty16) {
			this.materialQty16 = materialQty16;
		}
		public String getMaterialId17() {
			return materialId17;
		}
		public void setMaterialId17(String materialId17) {
			this.materialId17 = materialId17;
		}
		public String getMaterialName17() {
			return materialName17;
		}
		public void setMaterialName17(String materialName17) {
			this.materialName17 = materialName17;
		}
		public BigDecimal getMaterialQty17() {
			return materialQty17;
		}
		public void setMaterialQty17(BigDecimal materialQty17) {
			this.materialQty17 = materialQty17;
		}
		public String getMaterialId18() {
			return materialId18;
		}
		public void setMaterialId18(String materialId18) {
			this.materialId18 = materialId18;
		}
		public String getMaterialName18() {
			return materialName18;
		}
		public void setMaterialName18(String materialName18) {
			this.materialName18 = materialName18;
		}
		public BigDecimal getMaterialQty18() {
			return materialQty18;
		}
		public void setMaterialQty18(BigDecimal materialQty18) {
			this.materialQty18 = materialQty18;
		}
		public String getMaterialId19() {
			return materialId19;
		}
		public void setMaterialId19(String materialId19) {
			this.materialId19 = materialId19;
		}
		public String getMaterialName19() {
			return materialName19;
		}
		public void setMaterialName19(String materialName19) {
			this.materialName19 = materialName19;
		}
		public BigDecimal getMaterialQty19() {
			return materialQty19;
		}
		public void setMaterialQty19(BigDecimal materialQty19) {
			this.materialQty19 = materialQty19;
		}
		public String getMaterialId20() {
			return materialId20;
		}
		public void setMaterialId20(String materialId20) {
			this.materialId20 = materialId20;
		}
		public String getMaterialName20() {
			return materialName20;
		}
		public void setMaterialName20(String materialName20) {
			this.materialName20 = materialName20;
		}
		public BigDecimal getMaterialQty20() {
			return materialQty20;
		}
		public void setMaterialQty20(BigDecimal materialQty20) {
			this.materialQty20 = materialQty20;
		}
		public String getMaterialId21() {
			return materialId21;
		}
		public void setMaterialId21(String materialId21) {
			this.materialId21 = materialId21;
		}
		public String getMaterialName21() {
			return materialName21;
		}
		public void setMaterialName21(String materialName21) {
			this.materialName21 = materialName21;
		}
		public BigDecimal getMaterialQty21() {
			return materialQty21;
		}
		public void setMaterialQty21(BigDecimal materialQty21) {
			this.materialQty21 = materialQty21;
		}
		public String getMaterialId22() {
			return materialId22;
		}
		public void setMaterialId22(String materialId22) {
			this.materialId22 = materialId22;
		}
		public String getMaterialName22() {
			return materialName22;
		}
		public void setMaterialName22(String materialName22) {
			this.materialName22 = materialName22;
		}
		public BigDecimal getMaterialQty22() {
			return materialQty22;
		}
		public void setMaterialQty22(BigDecimal materialQty22) {
			this.materialQty22 = materialQty22;
		}
}
