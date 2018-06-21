package com.graly.erp.product.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CANA_TRANSFER")
public class CanaTransfer implements Serializable {
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="SELF_FIELD79")
	private String selfField79;//
	
	@Column(name="SELF_FIELD81")
	private String selfField81;//置为"已导入"

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSelfField79() {
		return selfField79;
	}

	public void setSelfField79(String selfField79) {
		this.selfField79 = selfField79;
	}

	public String getSelfField81() {
		return selfField81;
	}

	public void setSelfField81(String selfField81) {
		this.selfField81 = selfField81;
	}
}
