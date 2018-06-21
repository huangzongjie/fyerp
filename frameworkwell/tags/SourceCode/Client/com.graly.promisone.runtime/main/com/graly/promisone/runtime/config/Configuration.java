package com.graly.promisone.runtime.config;

import com.graly.promisone.runtime.service.ServiceManager;
import com.graly.promisone.runtime.Framework;

public class Configuration {
	
    private final ServiceManager serviceMgr;
	
    public Configuration() {
        serviceMgr = Framework.getLocalService(ServiceManager.class);
    }
    
    public void load() throws Exception {
    	serviceMgr.removeServices();
    	
    	
    }
    
    public void loadServiceHosts() throws Exception {
    	
    }
}
