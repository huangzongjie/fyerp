package com.graly.promisone.runtime;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import com.graly.promisone.runtime.model.impl.DefaultRuntimeContext;
import com.graly.promisone.runtime.model.RuntimeContext;

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
}
