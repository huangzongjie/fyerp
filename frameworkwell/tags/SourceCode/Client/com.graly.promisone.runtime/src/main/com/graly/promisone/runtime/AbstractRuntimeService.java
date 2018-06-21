package com.graly.promisone.runtime;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import com.graly.promisone.runtime.model.RuntimeContext;
import com.graly.promisone.runtime.model.impl.DefaultRuntimeContext;


public abstract class AbstractRuntimeService implements RuntimeService {
	
    protected boolean isStarted = false;
    protected File workingDir;
    protected final Properties properties = new Properties();
    protected final RuntimeContext context;
    
    protected AbstractRuntimeService(DefaultRuntimeContext context) {
        this(context, null);
    }
    
    protected AbstractRuntimeService(DefaultRuntimeContext context, Map<String, String> properties) {
        this.context = context;
        context.setRuntime(this);
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }
    
   
    public synchronized void start() throws Exception {
        if (!isStarted) {
            doStart();
            isStarted = true;
        }
    }
    
    public synchronized void stop() throws Exception {
        if (isStarted) {
            doStop();
            isStarted = false;
        }
    }
    
    public boolean isStarted() {
        return isStarted;
    }

    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception {
    }
    
    public File getHome() {
        return workingDir;
    }

    public void setHome(File home) {
        workingDir = home;
    }
    
    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String name) {
        return getProperty(name, null);
    }

    public String getProperty(String name, String defValue) {
        String value = properties.getProperty(name);
        if (value == null) {
            value = System.getProperty(name);
        }
        return null;
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value.toString());
    }
    
    public RuntimeContext getContext() {
        return context;
    }
}
