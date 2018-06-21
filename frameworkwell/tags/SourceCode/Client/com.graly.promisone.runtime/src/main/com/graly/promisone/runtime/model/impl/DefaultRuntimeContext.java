package com.graly.promisone.runtime.model.impl;

import java.net.URL;
import org.osgi.framework.Bundle;


import com.graly.promisone.runtime.model.RuntimeContext;
import com.graly.promisone.runtime.RuntimeService;
import com.graly.promisone.runtime.Framework;


public class DefaultRuntimeContext implements RuntimeContext{
	
	protected RuntimeService runtime;
	
	public DefaultRuntimeContext() {
        this(Framework.getRuntime());
    }
	
	public DefaultRuntimeContext(RuntimeService runtime) {
        this.runtime = runtime;
    }
	
	public void setRuntime(RuntimeService runtime) {
        this.runtime = runtime;
    }
	
	public RuntimeService getRuntime() {
        return runtime;
    }

    public URL getResource(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }

    public URL getLocalResource(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }

    public Class loadClass(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }
    
    public void deploy(URL url) throws Exception {
    }
    
    public Bundle getBundle() {
        return null;
    }
    
    public void destroy() {
    }

}
