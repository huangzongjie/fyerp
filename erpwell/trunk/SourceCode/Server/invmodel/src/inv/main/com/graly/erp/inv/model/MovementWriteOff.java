package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("W")
public class MovementWriteOff extends Movement{

	private static final long serialVersionUID = 1L;
	
	@Column(name="WRITEOFF_TYPE")//'N'--�������� 'W'--����
	private String writeoffType = "N";//Ĭ����N ��������

	public String getWriteoffType() {
		return writeoffType;
	}

	public void setWriteoffType(String writeoffType) {
		this.writeoffType = writeoffType;
	}
	
}
