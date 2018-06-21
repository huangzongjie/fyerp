package com.graly.framework.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="AD_AUTHORITY")
public class ADAuthority extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	public static String AUTHORITY_TYPE_MENU = "M";
	public static String AUTHORITY_TYPE_FUNCTION = "F";
	public static String AUTHORITY_TYPE_FEATURE = "B";
	
	public static String ACTION_TYPE_EDITOR = "E";
	public static String ACTION_TYPE_DIALOG = "D";
	
	public static String KEY_PRD_PART_ACTIVE = "Prd.Part.Active";
	public static String KEY_PRD_PART_FROZEN = "Prd.Part.Frozen";
	public static String KEY_PRD_PROCESS_ACTIVE = "Prd.Process.Active";
	public static String KEY_PRD_PROCESS_FROZEN = "Prd.Process.Frozen";
	public static String KEY_PRD_PROCEDURE_ACTIVE = "Prd.Procedure.Active";
	public static String KEY_PRD_PROCEDURE_FROZEN = "Prd.Procedure.Frozen";
	public static String KEY_PRD_STEP_ACTIVE = "Prd.Step.Active";
	public static String KEY_PRD_STEP_FROZEN = "Prd.Step.Frozen";
	public static String KEY_WIP_SCHEDULE = "Wip.Schedule";
	
	@Transient
	private Long level;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="AUTHORITY_TYPE")
	private String authorityType;
	
	@Column(name="ACTION")
	private String action;

	@Column(name="EDITOR_RRN")
	private Long editorRrn;
	
	@Column(name="PARENT_RRN")
	private Long parentRrn;
	
	@Column(name="SEQ_NO")
	private Long seqNo;

	@Column(name="LABEL")
	private String label;
	
	@Column(name="LABEL_ZH")
	private String label_zh;
	
	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getLevel() {
		return level;
	}
	
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
	
	public String getAuthorityType() {
		return authorityType;
	}
	
	public void setAuthorityType(String authorityType) {
		this.authorityType = authorityType;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setEditorRrn(Long editorRrn) {
		this.editorRrn = editorRrn;
	}

	public Long getEditorRrn() {
		return editorRrn;
	}
	
	public void setParentRrn(Long parentRrn) {
		this.parentRrn = parentRrn;
	}

	public Long getParentRrn() {
		return parentRrn;
	}
	
	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel_zh(String label_zh) {
		this.label_zh = label_zh;
	}

	public String getLabel_zh() {
		return label_zh;
	}
}
