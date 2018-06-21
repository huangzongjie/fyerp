package com.graly.mes.prd.workflow.bytes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.graly.mes.prd.workflow.JbpmConfiguration;

/**
 * is used by {@link com.graly.mes.prd.workflow.bytes.ByteArray} to chop a
 * byte arrays into a list of chunks and glue them back together.
 */
public abstract class ByteBlockChopper {

	public static List<ByteBlock> chopItUp(byte[] byteArray) {
		int blockSize = JbpmConfiguration.Configs.getInt("jbpm.byte.block.size");
		List<ByteBlock> bytes = null;
		if ((byteArray != null) && (byteArray.length > 0)) {
			bytes = new ArrayList<ByteBlock>();
			int index = 0;
			long seqNo = 0;
			while ((byteArray.length - index) > blockSize) {
				byte[] byteBlock = new byte[blockSize];
				System.arraycopy(byteArray, index, byteBlock, 0, blockSize);
				ByteBlock block = new ByteBlock();
				block.setSeqNo(seqNo);
				block.setBytes(byteBlock);
				bytes.add(block);
				index += blockSize;
				seqNo++;
			}
			byte[] byteBlock = new byte[byteArray.length - index];
			System.arraycopy(byteArray, index, byteBlock, 0, byteArray.length - index);
			ByteBlock block = new ByteBlock();
			block.setSeqNo(seqNo);
			block.setBytes(byteBlock);
			bytes.add(block);
		}
		return bytes;
	}

	public static byte[] glueChopsBackTogether(List<ByteBlock> bytes) {
		byte[] value = null;

		if (bytes != null) {
			Iterator<ByteBlock> iter = bytes.iterator();
			while (iter.hasNext()) {
				ByteBlock byteBlock = (ByteBlock) iter.next();
				if (value == null) {
					value = byteBlock.getBytes();
				} else {
					byte[] oldValue = value;
					value = new byte[value.length + byteBlock.getBytes().length];
					System.arraycopy(oldValue, 0, value, 0, oldValue.length);
					System.arraycopy(byteBlock, 0, value, oldValue.length, byteBlock.getBytes().length);
				}
			}
		}

		return value;
	}
}
