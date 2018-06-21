package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("O")
public class MovementOut extends Movement{	
	private static final long serialVersionUID = 1L;
	
	public static String OUT_TYPE_IN_ADJUST = "������";
	public static String OUT_TYPE_OUT_ADJUST = "�������";
	public static String OUT_TYPE_SALE_ADJUST = "���ۺ��";
	public static String OUT_TYPE_RD_ADJUST = "�з�����";//R&D
	
	public enum OutType {
		SOU, //���۳���
		OOU,//��������
		AOU,//��������
		ADOU,//Ӫ�˵�����ֻ�ı��棬���ı����
		DOU//�з����ϳ���
	};
	
	@Column(name="CUSTOMER_NAME")
	private String customerName;
	
	@Column(name="SELLER")
	private String seller;
	
	@Column(name="OUT_TYPE")
	private String outType;

	@Column(name="KIND")
	private String kind;
	
	@Column(name="DELIVER_ADDRESS")
	private String deliverAddress;//�ͻ���ַ
	
	@Column(name="LINK_MAN")
	private String linkMan;//�ͻ���ַ
	
	@Column(name="BT_LOT_ALARM")
	private String btLotAlarm;//��̩���ξ���,��˺�©������
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getSeller() {
		return seller;
	}

	public String getOutType() {
		return outType;
	}

	public void setOutType(String outType) {
		this.outType = outType;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getKind() {
		return kind;
	}
	
	public String getDeliverAddress() {
		return deliverAddress;
	}

	public void setDeliverAddress(String deliverAddress) {
		this.deliverAddress = deliverAddress;
	}

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public Boolean getBtLotAlarm() {
		return "Y".equalsIgnoreCase(this.btLotAlarm) ? true : false;
	}

	public void setBtLotAlarm(Boolean btLotAlarm) {
		this.btLotAlarm = btLotAlarm ? "Y" : "N";
	}
}
