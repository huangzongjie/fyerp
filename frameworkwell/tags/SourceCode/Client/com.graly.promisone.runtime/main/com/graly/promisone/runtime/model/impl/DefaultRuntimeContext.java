package com.graly.promisone.runtime.model.impl;

import com.graly.promisone.runtime.model.RuntimeContext;
import com.graly.promisone.runtime.RuntimeService;

public class DefaultRuntimeContext implements RuntimeContext{
	
	protected RuntimeService runtime;
	
	public void setRuntime(RuntimeService runtime) {
        this.runtime = runtime;
    }
}
