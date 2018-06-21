package com.graly.framework.runtime.osgi;

import java.net.URL;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.eclipse.jface.util.SafeRunnable;

import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.extensionpoints.ServerExtensionPoint;
import com.graly.framework.runtime.extensionpoints.ServiceExtensionPoint;
import com.graly.framework.runtime.extensionpoints.ExceptionHandlerExtensionPoint;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.runtime.exceptionhandler.SaveRunnableRunner;

public class OSGiRuntimeActivator implements BundleActivator {

	protected OSGiRuntimeService runtime;
	protected OSGiComponentLoader componentLoader;
	
	@Override
	public void start(BundleContext context) throws Exception {
		runtime = new OSGiRuntimeService(context);
		URL config = context.getBundle().getResource("/OSGI-INF/framework.properties");
        if (config != null) {
            System.setProperty(OSGiRuntimeService.PROP_CONFIG_DIR, config.toExternalForm());
        }
        initialize(runtime);
        
        Framework.initialize(runtime);
        
        componentLoader = new OSGiComponentLoader(runtime);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		componentLoader.uninstall();
        componentLoader = null;
        // unregister
        Framework.shutdown();
        uninitialize(runtime);
        runtime = null;

	}
	
	protected void initExceptionHandling() {
		final Thread.UncaughtExceptionHandler oldDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				if (!ExceptionHandlerManager.syncHandleException(t, e)) {
					if (oldDefaultExceptionHandler != null) {
						oldDefaultExceptionHandler.uncaughtException(t, e);
					}
				}
			}
		});
		//SafeRunnable class can open an error dialog and should not be used outside of the UI Thread
		SafeRunnable.setRunner(new SaveRunnableRunner());
	}
	
	protected void initialize(OSGiRuntimeService runtime) {
		ExceptionHandlerExtensionPoint exHandlerExtPoint = new ExceptionHandlerExtensionPoint();
		exHandlerExtPoint.initialize();
		ServerExtensionPoint serverExtPoint = new ServerExtensionPoint();
		serverExtPoint.initialize();
		ServiceExtensionPoint serviceExtPoint = new ServiceExtensionPoint();
		serviceExtPoint.initialize();
	}
	
	protected void uninitialize(OSGiRuntimeService runtime) {
        // do nothing
    }
}
