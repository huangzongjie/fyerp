package com.graly.promisone.runtime.osgi;

import java.net.URL;
import org.osgi.framework.Bundle;

import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.RuntimeService;
import com.graly.promisone.runtime.model.impl.DefaultRuntimeContext;

public class OSGiRuntimeContext extends DefaultRuntimeContext {
	
	protected final Bundle bundle;

    public OSGiRuntimeContext(Bundle bundle) {
        this(Framework.getRuntime(), bundle);
    }
    
    public OSGiRuntimeContext(RuntimeService runtime, Bundle bundle) {
        super(runtime);
        this.bundle = bundle;
    }
    
    @Override
    public Bundle getBundle() {
        return bundle;
    }
    
    @Override
    public URL getResource(String name) {
        return bundle.getResource(name);
    }

    @Override
    public URL getLocalResource(String name) {
        return bundle.getEntry(name);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return bundle.loadClass(className);
    }
}
