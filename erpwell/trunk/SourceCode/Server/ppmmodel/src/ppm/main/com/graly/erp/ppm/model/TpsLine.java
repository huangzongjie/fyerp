package com.graly.erp.ppm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name = "PPM_TPS_LINE")
public class TpsLine extends ADUpdatable {

	private static final long serialVersionUID = 1L;

	@Column(name = "TPS_ID")
	private String tpsId;
	
	@Column(name = "SALEPLAN_TYPE")
	private String salePlanType;
	
	@Column(name = "DATE_CREATED")
	private Date dateCreated;
	
	@Column(name = "MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name = "MATERIAL_ID")
	private String materialId;
	
	@Column(name = "MATERIAL_NAME")
	private String materialName;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;

	@Column(name = "UOM_ID")
	private String uomId;
	
	@Column(name = "QTY_TPS")
	private BigDecimal qtyTps;
	
	@Column(name = "DATE_DELIVERED")
	private Date dateDelivered;
	
	@Column(name = "IS_GENERATE")
	private String isGenerate = "N";
	
	@Column(name = "COMMENTS")
	private String comments;
	
	@Column(name="CUSTOMER_NAME")//客户名称
	private String customerName;
	
	@Column(name="SALER")//业务员
	private String saler;
	
	@Column(name="ORDER_ID")//订单编号
	private String orderId;
	
	@Column(name="IS_STOCK_UP")//区分是临时计划还是备货计划,备货计划的优先级让位于临时计划
	private String isStockUp = "N";//默认否
	
	@Column(name = "PREPARE_TPS")
	private String prepareTps;//是否为预处理物料的临时计划
	
	@Column(name = "SYS_VALIDATE")
	private String sysValidate = "Y";//系统是否校验过该计划(是否是无问题的临时计划或者预处理临时计划prepareTps),默认通过校验，因为有新建临时计划
	
	@Column(name = "EXCEL_VALIDATE")
	private String excelValidate;//Y代表EXCEL导入需系统校验
	
	@Column(name = "PI_ID")
	private String piId;//pi编号
	
	@Column(name = "INTERNAL_ORDER_ID")
	private String internalOrderId;//内部订单编号
	
	@Column(name="CUSTOMER_MANAGER")//客户经理
	private String customerManager;
	
	public void setTpsId(String tpsId) {
		this.tpsId = tpsId;
	}

	public String getTpsId() {
		return tpsId;
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialId() {
		return materialId;
	}

	public String getMaterialName() {
		return materialName;
	}
	
	public String getUomId() {
		if(uomId != null && !"".equals(uomId.trim()))
			return uomId;
		if(material != null){
			return material.getInventoryUom();
		}
		return "";
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public Date getDateDelivered() {
		return dateDelivered;
	}

	public void setDateDelivered(Date dateDelivered) {
		this.dateDelivered = dateDelivered;
	}

	public Boolean getIsGenerate() {
		return "Y".equals(this.isGenerate);
	}

	public void setState(Boolean isGenerate) {
		this.isGenerate = isGenerate ? "Y" : "N";
	}

	public BigDecimal getQtyTps() {
		return qtyTps;
	}

	public void setQtyTps(BigDecimal qtyTps) {
		this.qtyTps = qtyTps;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public void setIsGenerate(Boolean isGenerate) {
		this.isGenerate = isGenerate ? "Y" : "N";
	}

	public String getSalePlanType() {
		return salePlanType;
	}

	public void setSalePlanType(String salePlanType) {
		this.salePlanType = salePlanType;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
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

	public Boolean getIsStockUp() {
		return "Y".equals(isStockUp);
	}

	public void setIsStockUp(Boolean isStockUp) {
		this.isStockUp = (isStockUp ? "Y" : "N");
	}

	@Transient
	public String getPlanCategory(){
		if(material != null){
			return material.getPlanCategory();
		}
		return "";
	}
	
	@Transient
	public String getMaterialCategory1(){
		if(material != null){
			return material.getMaterialCategory1();
		}
		return "";
	}
	
	@Transient
	public String getMaterialCategory2(){
		if(material != null){
			return material.getMaterialCategory2();
		}
		return "";
	}
	
	@Transient
	public String getMaterialCategory3(){
		if(material != null){
			return material.getMaterialCategory3();
		}
		return "";
	}

	public Boolean getPrepareTps() {
		return "Y".equalsIgnoreCase(this.prepareTps) ? true : false;
	}

	public void setPrepareTps(Boolean prepareTps) {
		this.prepareTps = prepareTps ? "Y" : "N";
	}

	public Boolean getSysValidate() {
		return "Y".equalsIgnoreCase(this.sysValidate) ? true : false;
	}

	public void setSysValidate(Boolean sysValidate) {
		this.sysValidate = sysValidate ? "Y" : "N";
	}

	public Boolean getExcelValidate() {
		return "Y".equalsIgnoreCase(this.excelValidate) ? true : false;
	}

	public void setExcelValidate(Boolean excelValidate) {
		this.excelValidate = excelValidate ? "Y" : "N";
	}

	public String getPiId() {
		return piId;
	}

	public void setPiId(String piId) {
		this.piId = piId;
	}

	public String getInternalOrderId() {
		return internalOrderId;
	}

	public void setInternalOrderId(String internalOrderId) {
		this.internalOrderId = internalOrderId;
	}

	public String getCustomerManager() {
		return customerManager;
	}

	public void setCustomerManager(String customerManager) {
		this.customerManager = customerManager;
	}
	
}
