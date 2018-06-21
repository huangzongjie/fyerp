package com.graly.promisone.base.application;

import org.apache.log4j.Logger;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.base.security.login.ChangeAreaDialog;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.security.model.ADUser;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
	private final static Logger logger = Logger.getLogger(ChangeAreaDialog.class);
	
	private static final String PERSPECTIVE_ID = "Function";

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		try {
    		ADManager manager = Framework.getService(ADManager.class);
    		ADUser user = new ADUser();
    		user.setObjectId(Env.getUserId());
    		user = (ADUser)manager.getEntity(user);
    		if (user.getDefaultView() != null && !"".equals(user.getDefaultView().trim())) {
    			IPerspectiveDescriptor desc = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(user.getDefaultView());
    			if (desc != null) {
    				return user.getDefaultView();
    			}
    		} 
		} catch(Exception e) {
			logger.error("ChangeAreaDialog : initContent() " + e);
		}
		return PERSPECTIVE_ID;
	}
	
}
