package com.graly.mes.prd.workflow.bytes;

import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

/**
 * is a persistable array of bytes.  While there is no generic way of storing blobs
 * that is supported by many databases, all databases are able to handle small chunks 
 * of bytes properly.  It is the responsibility of this class to chop the large byte 
 * array into small chunks of 1K (and combine the chunks again in the reverse way).  
 * Hibernate will persist the list of byte-chunks in the database.
 * 
 * ByteArray is used in process variableInstances and in the file module (that stores the 
 * non-parsed process archive files). 
 */
@Entity
@Table(name="WF_BYTEARRAY")
public class ByteArray extends ADBase {

	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	protected String name = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "seqNo ASC")
	@JoinColumn(name = "PROCESS_FILE_RRN", referencedColumnName = "OBJECT_RRN")
	protected List<ByteBlock> byteBlocks = null;

	public ByteArray() {
	}

	public ByteArray(byte[] bytes) {
		this.byteBlocks = ByteBlockChopper.chopItUp(bytes);
	}

	public ByteArray(String name, byte[] bytes) {
		this(bytes);
		this.name = name;
	}

	public ByteArray(ByteArray other) {
		List<ByteBlock> otherByteBlocks = other.getByteBlocks();
		if (otherByteBlocks != null) {
			this.byteBlocks = otherByteBlocks;
		}
		this.name = other.name;
	}

	public byte[] getBytes() {
		return ByteBlockChopper.glueChopsBackTogether(byteBlocks);
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof ByteArray))
			return false;
		ByteArray other = (ByteArray) o;
		return Arrays.equals(
				ByteBlockChopper.glueChopsBackTogether(byteBlocks),
				ByteBlockChopper.glueChopsBackTogether(other.byteBlocks));
	}

	public int hashCode() {
		if (byteBlocks == null)
			return 0;
		return byteBlocks.hashCode();
	}

	public List<ByteBlock> getByteBlocks() {
		return byteBlocks;
	}
}
