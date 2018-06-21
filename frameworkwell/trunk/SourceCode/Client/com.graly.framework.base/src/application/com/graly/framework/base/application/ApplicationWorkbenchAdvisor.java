package com.graly.framework.base.application;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.graly.framework.base.security.login.ChangeAreaDialog;
import com.graly.framework.base.security.util.PasswordUtil;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.security.model.ADUser;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
	private final static Logger logger = Logger.getLogger(ChangeAreaDialog.class);
	
	private static final String PERSPECTIVE_ID = "Function";

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		try {
    		ADUser user = Env.getUser();
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
	
	@SuppressWarnings("restriction")
	public void postStartup() {
		StringBuffer msg = new StringBuffer();
		if(PasswordUtil.willPwdExpired(Env.getUser(), msg)){
			UI.showWarning(msg.toString());
		}
	}
	

}
