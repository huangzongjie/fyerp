package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="ALARM_TARGET")
public class AlarmTarget extends ADUpdatable {
	
	public static String TARGET_TYPE_IQC="IQC";//检验警报
	
	public static String TARGET_TYPE_WAREHOUSE="WAREHOUSE";//仓库警报
	
	public static String TARGET_TYPE_SERVICE="SERVICE";//服务公司最小库存警报

	private static final long serialVersionUID = 1L;
	@Column(name="USER_GROUP_RRN")
	private Long userGroupRrn;
	
	@Column(name="USER_RRN")
	private Long userRrn;
	
	@Column(name="TARGET_TYPE")
	private String targetType;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="FIELD1")
	private String field1;

	@Column(name="FIELD2")
	private String field2;
	
	@Column(name="FIELD3")
	private String field3;
	
	@Column(name="FIELD4")
	private String field4;
	
	@Column(name="FIELD5")
	private String field5;
	
	public Long getUserGroupRrn() {
		return userGroupRrn;
	}

	public void setUserGroupRrn(Long userGroupRrn) {
		this.userGroupRrn = userGroupRrn;
	}

	public Long getUserRrn() {
		return userRrn;
	}

	public void setUserRrn(Long userRrn) {
		this.userRrn = userRrn;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	
}
