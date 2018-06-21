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
 * 连接Canature原有crm数据库
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
	
	@Column(name="SERIAL_NUMBER")//相当于Material中的materialId
	private String  serialNumber;
	
	@Column(name="PRICE2_LOW")//成本价 物料设置功能中的财务成本报表中的价格就是从这个字段取的值
	private BigDecimal price2Low;
	
	@Column(name="PRICE2")//crm中的bom价
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
