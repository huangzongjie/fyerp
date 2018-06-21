package com.graly.erp.product.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ����Canatureԭ��crm���ݿ�
 * @author Denny
 *
 */
@Entity
@Table(name="CANA_PRODUCTS")
public class CanaProduct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="SERIAL_NUMBER")//�൱��Material�е�materialId
	private String  serialNumber;
	
	@Column(name="PRICE2_LOW")//�ɱ��� �������ù����еĲ���ɱ������еļ۸���Ǵ�����ֶ�ȡ��ֵ
	private BigDecimal price2Low;
	
	@Column(name="PRICE2")//crm�е�bom��
	private BigDecimal price2;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public BigDecimal getPrice2Low() {
		return price2Low;
	}

	public void setPrice2Low(BigDecimal price2Low) {
		this.price2Low = price2Low;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getPrice2() {
		return price2;
	}

	public void setPrice2(BigDecimal price2) {
		this.price2 = price2;
	}

}
