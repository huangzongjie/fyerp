package com.graly.promisone.security.model;

import static javax.persistence.CascadeType.ALL;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.ColumnResult;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADUpdatable;
@Entity
@Table(name="AD_MENU")
@SqlResultSetMapping(name="ADMenuResults", columns={
		@ColumnResult(name="LEVEL"),
		@ColumnResult(name="OBJECT_ID"),
		@ColumnResult(name="PARENT_ID"),
		@ColumnResult(name="NAME"),
		@ColumnResult(name="DESCRIPTION"),
		@ColumnResult(name="ACTION"),
		@ColumnResult(name="AD_EDITOR_ID"),
		@ColumnResult(name="MENU_TYPE"),
		@ColumnResult(name="LABEL"),
		@ColumnResult(name="LABEL_ZH")
	}
)
public class ADMenu extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	public static String MENU_TYPE_MENU = "M";
	public static String MENU_TYPE_FUNCTION = "F";
	public static String MENU_TYPE_FEATURE = "B";
	
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
	
	@Column(name="PARENT_ID")
	private Long parentId;
	
	@Column(name="SEQ_NO")
	private Long seqNo;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="ACTION")
	private String action;
	
	@Column(name="AD_EDITOR_ID")
	private Long editorId;

	@Column(name="MENU_TYPE")
	private String menuType;

	@Column(name="LABEL")
	private String label;
	
	@Column(name="LABEL_ZH")
	private String label_zh;
	
	@ManyToMany(targetEntity = ADUserGroup.class, mappedBy = "authorities")
	private List<ADUserGroup> userGroups;

	
	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getLevel() {
		return level;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getParentId() {
		return parentId;
	}
	
	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getSeqNo() {
		return seqNo;
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

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setEditorId(Long editorId) {
		this.editorId = editorId;
	}

	public Long getEditorId() {
		return editorId;
	}
	
	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public String getMenuType() {
		return menuType;
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
