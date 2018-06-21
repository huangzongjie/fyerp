package com.graly.mes.prd.workflow.context.exe.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.bytes.ByteArray;
import com.graly.mes.prd.workflow.context.exe.Converter;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;
import com.graly.mes.prd.workflow.util.CustomLoaderObjectInputStream;

public class SerializableToByteArrayConverter implements Converter {

	private static final long serialVersionUID = 1L;

	public boolean supports(Object value) {
		if (value == null)
			return true;
		return Serializable.class.isAssignableFrom(value.getClass());
	}

	public Object convert(Object o) {
		byte[] bytes = null;
		try {
			ByteArrayOutputStream memoryStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(
					memoryStream);
			objectStream.writeObject(o);
			objectStream.flush();
			bytes = memoryStream.toByteArray();
		} catch (IOException e) {
			throw new JbpmException("couldn't serialize '" + o + "'", e);
		}
		return new ByteArray(bytes);
	}

	public Object revert(Object o) {
		ByteArray byteArray = (ByteArray) o;
		InputStream memoryStream = new ByteArrayInputStream(byteArray
				.getBytes());
		try {
			ObjectInputStream objectStream = new ObjectInputStream(memoryStream);
			return objectStream.readObject();
		} catch (IOException ex) {
			throw new JbpmException("failed to read object", ex);
		} catch (ClassNotFoundException ex) {
			throw new JbpmException("serialized object class not found", ex);
		}
	}

	public Object revert(Object o, ProcessDefinition processDefinition) {
		ByteArray byteArray = (ByteArray) o;
		InputStream memoryStream = new ByteArrayInputStream(byteArray
				.getBytes());
		try {
			ObjectInputStream objectStream = new CustomLoaderObjectInputStream(
					memoryStream, ClassLoaderUtil
							.getProcessClassLoader(processDefinition));
			return objectStream.readObject();
		} catch (IOException ex) {
			throw new JbpmException("failed to read object", ex);
		} catch (ClassNotFoundException ex) {
			throw new JbpmException("serialized object class not found", ex);
		}
	}
}
