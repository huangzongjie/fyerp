package com.graly.mes.prd.workflow.instantiation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.file.def.FileDefinition;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;

public class ProcessClassLoader extends ClassLoader {

	private ProcessDefinition processDefinition = null;

	public ProcessClassLoader(ClassLoader parent,
			ProcessDefinition processDefinition) {
		super(parent);
		this.processDefinition = processDefinition;
	}

	public URL findResource(String name) {
		URL url = null;
		FileDefinition fileDefinition = processDefinition.getFileDefinition();
		if (fileDefinition != null) {
			// if the name of the resources starts with a / 
			if (name.startsWith("/")) {
				// then we start searching from the root of the process archive

				// we know that the leading slashes are removed in the names of the 
				// file definitions, therefor we skip the leading slashes
				while (name.startsWith("/")) {
					name = name.substring(1);
				}
			} else {
				// otherwise, (if the resource is relative), we look in the classes
				// directory in the process archive
				name = "classes/" + name;
			}

			byte[] bytes = null;
			if (fileDefinition.hasFile(name)) {
				bytes = fileDefinition.getBytes(name);
			}
			if (bytes != null) {
				try {
					url = new URL(null, "processresource://"
							+ processDefinition.getName() + "/classes/" + name,
							new BytesUrlStreamHandler(bytes));
				} catch (MalformedURLException e) {
					throw new JbpmException("couldn't create url", e);
				}
			}
		}
		return url;
	}

	public static class BytesUrlStreamHandler extends URLStreamHandler {
		byte[] bytes;

		public BytesUrlStreamHandler(byte[] bytes) {
			this.bytes = bytes;
		}

		protected URLConnection openConnection(URL u) throws IOException {
			return new BytesUrlConnection(bytes, u);
		}
	}

	public static class BytesUrlConnection extends URLConnection {
		byte[] bytes = null;

		public BytesUrlConnection(byte[] bytes, URL u) {
			super(u);
			this.bytes = bytes;
		}

		public void connect() throws IOException {
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(bytes);
		}
	}

	public Class findClass(String name) throws ClassNotFoundException {
		Class clazz = null;

		FileDefinition fileDefinition = processDefinition.getFileDefinition();
		if (fileDefinition != null) {
			String fileName = "classes/" + name.replace('.', '/') + ".class";
			byte[] classBytes;
			try {
				classBytes = fileDefinition.getBytes(fileName);
				clazz = defineClass(name, classBytes, 0, classBytes.length);
			} catch (JbpmException e) {
				clazz = null;
			}
		}

		if (clazz == null) {
			throw new ClassNotFoundException("class '" + name + "' could not be found by the process classloader");
		}

		return clazz;
	}
}
