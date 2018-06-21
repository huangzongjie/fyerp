package com.graly.framework.base.application;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.security.login.LauncherDialog;
import com.graly.framework.base.security.login.LoginDialog;
import com.graly.framework.base.ui.ImagesExtensionPoint;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.Property;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.ADOrg;
import com.graly.framework.security.model.ADUser;
/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	
	private final static Logger logger = Logger.getLogger(Application.class);
	
	private static final String LOG_DIR_PROPERTY_KEY = "com.gray.framework.log.dir";
	private static final String LOG4J_CONFIG_FILE = "log4j.xml";
	
	private static String configDir = "";
	private static String rootDir = "";
	private static String logDir = "";

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			initializeLogging();
			
			initializeImage();
			
			initializeMessage();
			initializeProperty();
			
			if (authenticate(display)) {
				Platform.endSplash();
				int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
				if (returnCode == PlatformUI.RETURN_OK)
					return IApplication.EXIT_OK;
			}
			
			return IApplication.EXIT_OK;
		} finally {
			if (display != null && !display.isDisposed()) {
				display.dispose();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
	
	private boolean authenticate(Display display) {
		LoginDialog loginDialog = new LoginDialog(display);
		loginDialog.createContents();
		if (LoginDialog.CANCEL == loginDialog.getReturnCode()){
			return false;
		}
		return true;
	}

	protected void initializeLogging() throws IOException {
		File logConfFile = new File(getConfigDir(), LOG4J_CONFIG_FILE);
		getLogDir();
		if (!logConfFile.exists()){
			org.apache.log4j.xml.DOMConfigurator.configure(this.getClass().getResource("/config/log4j.xml"));	        
		} else {
			org.apache.log4j.xml.DOMConfigurator.configure(logConfFile.toURL());
		}
		logger.info("Logging for framework started.");
	}
	
	public static String getConfigDir() {
		if (configDir.equals("")){ 
			File configFile = new File(getRootDir(), "config"); 
			configFile.mkdirs();
			configDir = configFile.getAbsolutePath();
		}
		return configDir;
	}
	
	public static String getLogDir() {
		if (logDir.equals("")){
			File logDirF = new File(getRootDir(), "log");
			if (!logDirF.exists()) {
				if (!logDirF.mkdirs())
					System.err.println("Could not create log directory "+logDirF.getAbsolutePath());
			}
			logDir = logDirF.getAbsolutePath();

			// the log4j.xml references the log-directory via the system property - hence we need to set it here.
			System.setProperty(LOG_DIR_PROPERTY_KEY, logDir);
		}
		return logDir;
	}
	
	public static String getRootDir() {
		if (rootDir.equals("")) { 
			String instanceArea = System.getProperty("osgi.instance.area.default");
			if (instanceArea == null) {
				instanceArea = System.getProperty("osgi.instance.area.default");
				if (instanceArea == null) {
					System.err.println("Neither the system property \"osgi.instance.area\" nor \"osgi.instance.area.default\" is set!!! You might want to set \"osgi.instance.area.default\" in your config.ini! And you should check your OSGI environment, because even without the default, a concrete value should be set!");
					throw new IllegalStateException("Neither the system property \"osgi.instance.area\" nor \"osgi.instance.area.default\" is set!!! You might want to set \"osgi.instance.area.default\" in your config.ini! And you should check your OSGI environment, because even without the default, a concrete value should be set!");
				}
			}

			String prefixFile = "file:";
			if (instanceArea.startsWith(prefixFile)) {
				instanceArea = instanceArea.substring(prefixFile.length());
			} 
			System.setProperty("osgi.instance.area.default", instanceArea);
			File f = new File(instanceArea).getAbsoluteFile();
			f = new File(f, "data");
			f.mkdirs();
			rootDir = f.getAbsolutePath();
		}
		return rootDir;
	}
	
    protected void initializeMessage() {
    	Message.load();
	}
    
    protected void initializeProperty() {
    	Property.load();
    }
    
    protected void initializeImage() {
    	try{
	        ImagesExtensionPoint imgExtPoint = new ImagesExtensionPoint();
	        imgExtPoint.initialize();
    	} catch (Exception e){
    		logger.error("initializeImage error:", e);
    	}
	}

}
