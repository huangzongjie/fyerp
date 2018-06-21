package com.graly.mes.prd.model;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="PRD_OPERATION")
public class Operation extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="SEQ_NO")
	private Long seqNo;
	
	@Column(name="DESCRIPTION")
	private String desciption;
	
	public Long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}
	
	public void replaceOperationParam(Map<String, Object> paramMap) {
		if (paramMap == null || paramMap.size() == 0) {
			return;
		}
		Iterator<String> iter = paramMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String)iter.next();
			Pattern pattern = Pattern.compile("#\\{" + key + "\\}");
			Matcher matcher = pattern.matcher(desciption);
			Object object = paramMap.get(key);
			if (object != null) {
				desciption = matcher.replaceAll(object.toString());
			}
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public boolean equals(Object obj) {
		if(this == null || obj == null) return false;
		if(!(obj instanceof Operation)) return false;
		
		Operation operation = (Operation)obj;
		if(this == operation) {
			return true;
		}
		if(this.getObjectRrn() != null && operation.getObjectRrn() != null) {
			if(this.getObjectRrn().equals(operation.getObjectRrn())) {
				return true;
			}
		}
		return false;
	}
}
