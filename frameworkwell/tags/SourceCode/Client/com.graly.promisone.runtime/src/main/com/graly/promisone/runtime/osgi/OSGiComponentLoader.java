package com.graly.promisone.runtime.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

public class OSGiComponentLoader implements SynchronousBundleListener {
	
	private final OSGiRuntimeService runtime;
	
	public OSGiComponentLoader(OSGiRuntimeService runtime) {
        this.runtime = runtime;
        install();
    }
	
	public void install() {
        BundleContext ctx = runtime.getBundleContext();
        ctx.addBundleListener(this);
        Bundle[] bundles = ctx.getBundles();
        int mask = Bundle.RESOLVED | Bundle.STARTING | Bundle.ACTIVE;
        for (Bundle bundle : bundles) {
            int state = bundle.getState();
            if ((state & mask) != 0) { 
                if (OSGiRuntimeService.getComponentsList(bundle) != null) {
                    try {
                        runtime.createContext(bundle);
                    } catch (Throwable e) {
                    }
                }
            }
        }
    }
	
	public void uninstall() {
        runtime.getBundleContext().removeBundleListener(this);
    }
	
	@Override
	public void bundleChanged(BundleEvent event) {
		try {
            Bundle bundle = event.getBundle();
            int type = event.getType();
            switch (type) {
            case BundleEvent.RESOLVED:
                if (OSGiRuntimeService.getComponentsList(bundle) != null) {
                    runtime.createContext(bundle);
                }
                break;
            case BundleEvent.UNRESOLVED:
                if (OSGiRuntimeService.getComponentsList(bundle) != null) {
                    runtime.destroyContext(bundle);
                }
                break;
            }
        } catch (Exception e) {
        }
	}

}
