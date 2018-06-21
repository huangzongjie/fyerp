package com.graly.mes.prd.workflow.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.graly.mes.prd.workflow.JbpmException;

public abstract class IoUtil {

	public static final int BUFFERSIZE = 4096;

	public static byte[] readBytes(InputStream inputStream) throws IOException {
		byte[] bytes = null;
		if (inputStream == null) {
			throw new JbpmException("inputStream is null");
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		transfer(inputStream, outputStream);
		bytes = outputStream.toByteArray();
		outputStream.close();
		return bytes;
	}

	public static int transfer(InputStream in, OutputStream out)
			throws IOException {
		int total = 0;
		byte[] buffer = new byte[BUFFERSIZE];
		int bytesRead = in.read(buffer);
		while (bytesRead != -1) {
			out.write(buffer, 0, bytesRead);
			total += bytesRead;
			bytesRead = in.read(buffer);
		}
		return total;
	}
}
