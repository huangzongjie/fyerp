package com.graly.mes.prd.workflow.graph.def;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.log4j.Logger;

import com.graly.framework.activeentity.model.ADBase;

@MappedSuperclass
public abstract class GraphElement extends ADBase implements Serializable {
	
	static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(GraphElement.class);

	@Column(name = "NAME")
	protected String name;

	@Column(name = "DESCRIPTION")
	protected String description;

	public GraphElement() {
	}

	public GraphElement(String name) {
		setName(name);
	}

	public String toString() {
		String className = getClass().getName();
		className = className.substring(className.lastIndexOf('.') + 1);
		if (name != null) {
			className = className + "(" + name + ")";
		} else {
			className = className + "("
					+ Integer.toHexString(System.identityHashCode(this)) + ")";
		}
		return className;
	}

//	public boolean equals(Object o) {
//		return EqualsUtil.equals(this, o);
//	}
	
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
