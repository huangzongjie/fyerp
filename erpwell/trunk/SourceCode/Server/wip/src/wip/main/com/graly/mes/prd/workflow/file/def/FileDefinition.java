package com.graly.mes.prd.workflow.file.def;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.JbpmConfiguration;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.bytes.ByteArray;
import com.graly.mes.prd.workflow.module.def.ModuleDefinition;
import com.graly.mes.prd.workflow.module.exe.ModuleInstance;
import com.graly.mes.prd.workflow.util.IoUtil;

@Entity
@DiscriminatorValue("F")
public class FileDefinition extends ModuleDefinition {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FileDefinition.class);
	
	static String getRootDir() {
		String rootDir = null;
		if (JbpmConfiguration.Configs.hasObject("jbpm.files.dir")) {
			rootDir = JbpmConfiguration.Configs.getString("jbpm.files.dir");
		}
		return rootDir;
	}
	
	@Transient
	String dir = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@MapKey(name = "name")
	@JoinColumn(name = "FILE_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	Map<String, ByteArray> processFiles = null;

	public FileDefinition() {
	}
	
	public ModuleInstance createInstance() {
		return null;
	}

	 
	/**
	 * add a file to this definition.
	 */
	public void addFile(String name, byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		addFile(name, bais);
	}

	/**
	 * add a file to this definition.
	 */
	public void addFile(String name, InputStream is) {
		try {
			if (isStoredOnFileSystem()) {
				storeFileOnFileSystem(name, is);

			} else { // its stored in the database
				storeFileInDb(name, is);
			}
		} catch (Exception e) {
			throw new JbpmException("file '" + name + "' could not be stored", e);
		}
	}

	void storeFileOnFileSystem(String name, InputStream is)
			throws FileNotFoundException, IOException {
		String fileName = getFilePath(name);
		logger.trace("storing file '" + name + "' on file system to '" + fileName
				+ "'");
		FileOutputStream fos = new FileOutputStream(fileName);
		IoUtil.transfer(is, fos);
		fos.close();
	}

	void storeFileInDb(String name, InputStream is) throws IOException {
		if (processFiles == null) {
			processFiles = new HashMap<String, ByteArray>();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		logger.trace("preparing file '" + name + "' for storage in the database");
		IoUtil.transfer(is, baos);
		ByteArray byteArray = new ByteArray(name, baos.toByteArray());
		processFiles.put(name, byteArray);
	}

	// retrieving files
	// //////////////////////////////////////////////////////////

	/**
	 * retrieve a file of this definition as an inputstream.
	 */
	public InputStream getInputStream(String name) {
		InputStream inputStream = null;
		try {
			if (isStoredOnFileSystem()) {
				inputStream = getInputStreamFromFileSystem(name);
			} else { // its stored in the database
				inputStream = getInputStreamFromDb(name);
			}
		} catch (Exception e) {
			throw new JbpmException("couldn't get inputstream for file '" + name + "'", e);
		}
		return inputStream;
	}

	public boolean hasFile(String name) {
		if (isStoredOnFileSystem()) {
			return new File(getFilePath(name)).exists();
		} else {
			return processFiles == null ? false : processFiles.containsKey(name);
		}
	}

	public Map getInputStreamMap() {
		HashMap result = new HashMap();
		if (processFiles != null) {
			Iterator iterator = processFiles.keySet().iterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				result.put(name, getInputStream(name));
			}
		}
		return result;
	}

	public Map<String, byte[]> getBytesMap() {
		HashMap<String, byte[]> result = new HashMap<String, byte[]>();
		if (processFiles != null) {
			Iterator<String> iterator = processFiles.keySet().iterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				result.put(name, getBytes(name));
			}
		}
		return result;
	}

	private InputStream getInputStreamFromFileSystem(String name)
			throws FileNotFoundException {
		InputStream inputStream = null;
		String fileName = getFilePath(name);
		logger.trace("loading file '" + name + "' from file system '" + fileName + "'");
		inputStream = new FileInputStream(fileName);
		return inputStream;
	}

	private InputStream getInputStreamFromDb(String name) {
		InputStream inputStream = null;
		logger.trace("loading file '" + name + "' from database");
		ByteArray byteArray = getByteArray(name);
		if (byteArray != null) {
			inputStream = new ByteArrayInputStream(byteArray.getBytes());
		}
		return inputStream;
	}

	/**
	 * retrieve a file of this definition as a byte array.
	 */
	public byte[] getBytes(String name) {
		byte[] bytes = null;
		try {
			bytes = getBytesFromDb(name);
		} catch (Exception e) {
			throw new JbpmException("couldn't get value for file '" + name + "'", e);
		}
		return bytes;
	}

	byte[] getBytesFromFileSystem(String name) throws IOException {
		byte[] bytes = null;
		InputStream in = getInputStreamFromFileSystem(name);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IoUtil.transfer(in, out);
		bytes = out.toByteArray();
		return bytes;
	}

	byte[] getBytesFromDb(String name) {
		byte[] bytes;
		ByteArray byteArray = getByteArray(name);
		bytes = byteArray.getBytes();
		return bytes;
	}

	ByteArray getByteArray(String name) {
		return (ByteArray) (processFiles != null ? processFiles.get(name) : null);
	}

	boolean isStoredOnFileSystem() {
		String rootDir = getRootDir();
		boolean isStoredOnFileSystem = (rootDir != null);
		// if files should be stored on the file system and no directory has
		// been
		// created yet...
		if ((isStoredOnFileSystem) && (dir == null)) {
			// create a new directory
			dir = findNewDirName();
			new File(rootDir + "/" + dir).mkdirs();
		}
		return isStoredOnFileSystem;
	}

	String findNewDirName() {
		String newDirName = "files-1";

		File parentFile = new File(getRootDir());
		if (parentFile.exists()) {
			// get the current contents of the directory
			String[] children = parentFile.list();
			List fileNames = new ArrayList();
			if (children != null) {
				fileNames = new ArrayList(Arrays.asList(children));
			}

			// find an unused name for the directory to be created
			int seqNr = 1;
			while (fileNames.contains(newDirName)) {
				seqNr++;
				newDirName = "files-" + seqNr;
			}
		}
		return newDirName;
	}

	String getFilePath(String name) {
		String filePath = getRootDir() + "/" + dir + "/" + name;
		new File(filePath).getParentFile().mkdirs();
		return filePath;
	}
}
