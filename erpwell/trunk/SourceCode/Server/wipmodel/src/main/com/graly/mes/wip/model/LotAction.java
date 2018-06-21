package com.graly.mes.wip.model;

import java.io.Serializable;

public class LotAction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String actionCode;
	private String actionReason;
	private String actionComment;
	
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
	public String getActionCode() {
		return actionCode;
	}
	public void setActionReason(String actionReason) {
		this.actionReason = actionReason;
	}
	public String getActionReason() {
		return actionReason;
	}
	public void setActionComment(String actionComment) {
		this.actionComment = actionComment;
	}
	public String getActionComment() {
		return actionComment;
	}
	
}
