package com.graly.promisone.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.graly.promisone.activeentity.model.ADUpdatable;
import com.graly.promisone.activeentity.model.ADBase;

@Entity
@Table(name="AD_EDITOR")
public class ADEditor extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="EDITOR_ID")
	private String editorId;
	
	@Column(name="PARAM1")
	private String PARAM1;
	
	@Column(name="PARAM2")
	private String PARAM2;
	
	@Column(name="PARAM3")
	private String PARAM3;
	
	@Column(name="PARAM4")
	private String PARAM4;
	
	@Column(name="PARAM5")
	private String PARAM5;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEditorId(String editorId) {
		this.editorId = editorId;
	}

	public String getEditorId() {
		return editorId;
	}

	public void setPARAM1(String pARAM1) {
		PARAM1 = pARAM1;
	}

	public String getPARAM1() {
		return PARAM1;
	}

	public void setPARAM2(String pARAM2) {
		PARAM2 = pARAM2;
	}

	public String getPARAM2() {
		return PARAM2;
	}

	public void setPARAM3(String pARAM3) {
		PARAM3 = pARAM3;
	}

	public String getPARAM3() {
		return PARAM3;
	}

	public void setPARAM4(String pARAM4) {
		PARAM4 = pARAM4;
	}

	public String getPARAM4() {
		return PARAM4;
	}

	public void setPARAM5(String pARAM5) {
		PARAM5 = pARAM5;
	}

	public String getPARAM5() {
		return PARAM5;
	}
}
