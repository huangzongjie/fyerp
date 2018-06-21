package com.graly.framework.core.config;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Config {
	private static final Logger logger = Logger.getLogger(Config.class);
	
	public static final char beforeIdentifier = '-';
	public static final char beforeMidfix = '-';
	public static final char beforeClassName = '-';
	public static final String ext = "xml";
	
	private static Map<String, Config> file2Config = new HashMap<String, Config>();
	
	public static Config sharedInstance(String configFile) {
		if (file2Config.containsKey(configFile)) {
			return file2Config.get(configFile);
		} else {
			Config config = new Config();
			file2Config.put(configFile, config);
			return config;
		}
	}
	
	private File configFile;
	private File configDir;
	private String includeFileMidfix;
	
	protected Object ioMutex = new Object();
	
	private Set<String> loadedConfigModuleClassNames = new HashSet<String>();
	
	private HashMap<String, ConfigModule> configModules = new HashMap<String, ConfigModule>();
	private HashMap<String, ConfigModule> includedCfModsByFileName = new HashMap<String, ConfigModule>();
	private HashMap<ConfigModule, String> includedCfModsByCfMod = new HashMap<ConfigModule, String>();
	
	public <T extends ConfigModule> T createConfigModule(Class<T> configModuleClass) throws ConfigException {
		return createConfigModule(configModuleClass, null);
	}
	
	public <T extends ConfigModule> T createConfigModule(Class<T> configModuleClass, String identifier)	throws ConfigException {
		loadConfigModulesForClass(configModuleClass.getName());

		try {
			ConfigModule cfMod = configModules.get(getConfigModuleIdentifyingName(configModuleClass, identifier));
			if (cfMod == null) {
				cfMod = configModuleClass.newInstance();
				cfMod.setConfig(this);
				cfMod.setIdentifier(identifier);
				cfMod.init();
				configModules.put(getConfigModuleIdentifyingName(cfMod), cfMod);
			}
			
			return configModuleClass.cast(cfMod);
		} catch (Throwable t) {
			throw new ConfigException(t);
		}
	}
	
	private void loadConfigModulesForClass(String configModuleClassName) {
		
		synchronized (ioMutex) {
			if (loadedConfigModuleClassNames.contains(configModuleClassName))
				return;

			try {
				URL url = Class.forName(configModuleClassName).getResource(createFileName(configModuleClassName));
				if (url == null) {
					// The resource can not be assigned, thus we don't have any
					// serialized configuration.
					throw new ConfigException("Configuration file "
							+ configModuleClassName + " cannot be found. File does not exist in:"+ configDir+" or class resource directory.");
				}

				// read include file from input stream
				InputStream in = url.openStream();
				try {
					ConfigModule cfMod;
					XMLDecoder d = createXMLDecoder(in, new ConfigExceptionListener("Error reading config module file \"" + url + "\"!"));
					try {
						cfMod = (ConfigModule)d.readObject();
						if(logger.isDebugEnabled()) logger.debug("Config file read: " + url);
					} finally {
						d.close();
					}

					// get deserialized config module, call init() and add it to local lists.
					cfMod.setConfig(this);
					cfMod.init();
					this.putIncludedCfMod(configModuleClassName, cfMod);
					this.configModules.put(getConfigModuleIdentifyingName(cfMod), cfMod);

				} finally {
					in.close();
				}
				loadedConfigModuleClassNames.add(configModuleClassName);
			} catch (Exception x) {
				logger.warn("Unable to load config module file: " + configModuleClassName, x);
			}
		} 
	}
	
	public <T extends ConfigModule> T getConfigModule(Class<T> configModuleClass, boolean throwExceptionIfNotExistent) {
		return configModuleClass.cast(getConfigModule(configModuleClass.getName(), null, throwExceptionIfNotExistent));
	}
	
	public ConfigModule getConfigModule(String configModuleClassName, String identifier, boolean throwExceptionIfNotExistent) {
		loadConfigModulesForClass(configModuleClassName);
		ConfigModule cfMod = configModules.get(getConfigModuleIdentifyingName(configModuleClassName, identifier));
		if (cfMod == null && throwExceptionIfNotExistent)
			throw new ConfigModuleNotFoundException("No ConfigModule of type \""+configModuleClassName+"\" existent!");
		return cfMod;
	}
	
	protected void putIncludedCfMod(String relativeCfModFileName, ConfigModule cfMod) {
		includedCfModsByFileName.put(relativeCfModFileName, cfMod);
		includedCfModsByCfMod.put(cfMod, relativeCfModFileName);
	}
	
	private static String createFileName(String configModuleClassName) {
		int lastDotPos = configModuleClassName.lastIndexOf(".");
		String fileName = configModuleClassName.substring(lastDotPos + 1);
		return fileName + "." + ext;
	}
		
	
	private static String getConfigModuleIdentifyingName(ConfigModule module) {
		return getConfigModuleIdentifyingName(module.getClass(), module.getIdentifier());
	}

	private static String getConfigModuleIdentifyingName(Class<? extends ConfigModule> moduleClass, String identifier) {
		return getConfigModuleIdentifyingName(moduleClass.getName(), identifier);
	}
	
	private static String getConfigModuleIdentifyingName(String moduleClassName, String identifier)	{
		StringBuffer result = new StringBuffer(moduleClassName);
		if(identifier != null && !identifier.equals("")) {
			result.append(beforeIdentifier);
			result.append(identifier);
		}
		return result.toString();
	}
	
	private ClassLoader classLoader;

	public ClassLoader getClassLoader() {
		return classLoader;
	}
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	private ClassLoader getDefaultClassLoader()	{
		if (classLoader == null)
			return Config.class.getClassLoader();
		else
			return classLoader;
	}
	
	private XMLDecoder createXMLDecoder(InputStream in, ExceptionListener exceptionListener) {
		return new XMLDecoder(
				in,
				null,
				exceptionListener,
				getDefaultClassLoader()
		);
	}
	
	private static class ConfigExceptionListener implements ExceptionListener
	{
		private String message;
		public ConfigExceptionListener(String message) {
			this.message = message;
		}

		@Override
		public void exceptionThrown(Exception e) {
			logger.error(message + " :: " + e, e);
			throw new RuntimeException(e);
		}
	}
}
