package com.graly.mes.prd.workflow.context.def;

import java.io.Serializable;

public class Access implements Serializable {

	private static final long serialVersionUID = 1L;

	String access = "read,write";

	public Access() {
	}

	public Access(String access) {
		if (access != null) {
			if ("".equals(access)) {
				this.access = " ";
			} else {
				this.access = access;
			}
		}
	}

	public boolean isReadable() {
		return hasAccess("read");
	}

	public boolean isWritable() {
		return hasAccess("write");
	}

	public boolean isRequired() {
		return hasAccess("required");
	}

	public boolean isLock() {
		return hasAccess("lock");
	}

	/**
	 * verifies if the given accessLiteral is included in the access text.
	 */
	public boolean hasAccess(String accessLiteral) {
		if (access == null)
			return false;
		return (access.indexOf(accessLiteral.toLowerCase()) != -1);
	}

	public String toString() {
		return access;
	}

	public boolean equals(Object object) {
		if (object instanceof Access) {
			Access other = (Access) object;
			return (isReadable() == other.isReadable())
					&& (isWritable() == other.isWritable())
					&& (isRequired() == other.isRequired())
					&& (isLock() == other.isLock());
		} else {
			return false;
		}
	}
}
