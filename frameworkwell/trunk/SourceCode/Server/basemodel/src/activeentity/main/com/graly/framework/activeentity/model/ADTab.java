package com.graly.framework.activeentity.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="AD_TAB")
public class ADTab extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="TABLE_RRN")
	private Long tableRrn;
	
	@Column(name="SEQ_NO")
	private Long seqNo; 
	
	@Column(name="GRID_Y")
	private Long gridY; 
	
	@Column(name="LABEL")
	private String label;
	
	@Column(name="LABEL_ZH")
	private String label_zh;
	
	@OneToMany(mappedBy = "tab", fetch=FetchType.LAZY, 
			targetEntity = com.graly.framework.activeentity.model.ADField.class)
	@OrderBy(value = "seqNo ASC")
	private List<ADField> fields;
	
	@ManyToOne
	@JoinColumn(name = "TABLE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private ADTable table;
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setTableRrn(Long tableRrn) {
		this.tableRrn = tableRrn;
	}

	public Long getTableRrn() {
		return tableRrn;
	}
	
	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public void setGridY(Long gridY) {
		this.gridY = gridY;
	}

	public Long getGridY() {
		return gridY;
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
	
	public void setFields(List<ADField> fields) {
		this.fields = fields;
	}

	public List<ADField> getFields() {
		return fields;
	}

	public void setTable(ADTable table) {
		this.table = table;
	}

	public ADTable getTable() {
		return table;
	}

	public String getTableName() {
		return table != null ? table.getModelName() : "";
	}
	
}
