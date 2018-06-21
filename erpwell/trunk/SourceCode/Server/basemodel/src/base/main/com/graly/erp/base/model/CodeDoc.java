package com.graly.erp.base.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="BAS_CODE_DOC")
public class CodeDoc extends ADBase {
	
	private static final long serialVersionUID = 1L;
	public static Map<String, String> docMap = null;
	public static Map<String, String> docCategoryMap = null;
	
	@Column(name="DOC")
	private String doc;
	
	@Column(name = "DOC_CODE")
	private String docCode;

	@Column(name = "DOC_CATEGORY")
	private String docCategory;
	
	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public String getDocCode() {
		return docCode;
	}

	public void setDocCode(String docCode) {
		this.docCode = docCode;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getDocCategory() {
		return docCategory;
	}


}
