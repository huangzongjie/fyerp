package com.graly.framework.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="AD_EDITOR")
public class ADEditor extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="EDITOR_ID")
	private String editorId;
	
	@Column(name="PARAM1")
	private String pARAM1;
	
	@Column(name="PARAM2")
	private String pARAM2;
	
	@Column(name="PARAM3")
	private String pARAM3;
	
	@Column(name="PARAM4")
	private String pARAM4;
	
	@Column(name="PARAM5")
	private String pARAM5;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setEditorId(String editorId) {
		this.editorId = editorId;
	}

	public String getEditorId() {
		return editorId;
	}

	public void setPARAM1(String pARAM1) {
		this.pARAM1 = pARAM1;
	}

	public String getPARAM1() {
		return pARAM1;
	}

	public void setPARAM2(String pARAM2) {
		this.pARAM2 = pARAM2;
	}

	public String getPARAM2() {
		return pARAM2;
	}

	public void setPARAM3(String pARAM3) {
		this.pARAM3 = pARAM3;
	}

	public String getPARAM3() {
		return pARAM3;
	}

	public void setPARAM4(String pARAM4) {
		this.pARAM4 = pARAM4;
	}

	public String getPARAM4() {
		return pARAM4;
	}

	public void setPARAM5(String pARAM5) {
		this.pARAM5 = pARAM5;
	}

	public String getPARAM5() {
		return pARAM5;
	}
}
