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
@Table(name = "PPM_TPS_LINE_PREPARE")
public class TpsLinePrepare extends ADUpdatable {

	private static final long serialVersionUID = 1L;

	public static final String TPSSTATUS_DRAFTED = "DRAFTED";
	public static final String TPSSTATUS_COMPLETED = "COMPLETED";
	public static final String TPSSTATUS_APPROVED = "APPROVED";
	
	
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
	
	@Column(name="CUSTOMER_NAME")//�ͻ�����
	private String customerName;
	
	@Column(name="SALER")//ҵ��Ա
	private String saler;
	
	@Column(name="ORDER_ID")//�������
	private String orderId;
	
	@Column(name="IS_STOCK_UP")//��������ʱ�ƻ����Ǳ����ƻ�,�����ƻ������ȼ���λ����ʱ�ƻ�
	private String isStockUp = "N";//Ĭ�Ϸ�
	
	@Column(name="TPS_STATUS")//TPS״̬
	private String tpsStatus;//
	
	@Column(name = "PREPARE_TPS")
	private String prepareTps = "N";//�Ƿ�ΪԤ�������ϵ���ʱ�ƻ�,Ĭ��û��

	@Column(name = "SYS_VALIDATE")
	private String sysValidate = "Y";//ϵͳ�Ƿ�У����üƻ�(�Ƿ������������ʱ�ƻ�����Ԥ������ʱ�ƻ�prepareTps)
	
	@Column(name = "EXCEL_VALIDATE")
	private String excelValidate;//Y����EXCEL������ϵͳУ��

	@Column(name = "PI_ID")
	private String piId;//pi���
	
	@Column(name = "INTERNAL_ORDER_ID")
	private String internalOrderId;//�ڲ��������
	
	@Column(name="CUSTOMER_MANAGER")//�ͻ�����
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

	public String getTpsStatus() {
		return tpsStatus;
	}

	public void setTpsStatus(String tpsStatus) {
		this.tpsStatus = tpsStatus;
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

	public String getInternalOrderId() {
		return internalOrderId;
	}

	public void setInternalOrderId(String internalOrderId) {
		this.internalOrderId = internalOrderId;
	}

	public String getPiId() {
		return piId;
	}

	public void setPiId(String piId) {
		this.piId = piId;
	}

	public String getCustomerManager() {
		return customerManager;
	}

	public void setCustomerManager(String customerManager) {
		this.customerManager = customerManager;
	}
}
