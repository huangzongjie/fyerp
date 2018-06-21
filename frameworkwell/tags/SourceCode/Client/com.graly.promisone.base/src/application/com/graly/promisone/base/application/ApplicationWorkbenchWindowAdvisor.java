package com.graly.promisone.base.application;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.graly.promisone.base.ui.app.LocaleStatusLineContribution;
import com.graly.promisone.base.ui.app.LoginStateStatusLineContribution;
import com.graly.promisone.base.ui.app.ServerStatusLineContribution;
import com.graly.promisone.base.ui.app.TimeStatusLineContribution;
import com.graly.promisone.base.ui.util.RCPUtil;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowMenuBar(false);
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowFastViewBars(false);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowProgressIndicator(false);

        configurer.setTitle("MESwell");
        PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.ENABLE_ANIMATIONS, true);
    	PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,false);
    	PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
    }
    
    public void postWindowOpen() {
    	getWindowConfigurer().getWindow().getShell().setMaximized(true);
    }
    
    @Override
	public void createWindowContents(Shell shell) {
		super.createWindowContents(shell);
		LocaleStatusLineContribution locale = new LocaleStatusLineContribution("Locale"); 
		LoginStateStatusLineContribution loginState = new LoginStateStatusLineContribution("Status");	
		TimeStatusLineContribution time = new TimeStatusLineContribution("Time");
		ServerStatusLineContribution server = new ServerStatusLineContribution("Server");
		
		RCPUtil.addContributionItemTrim(shell, locale, null);
		RCPUtil.addContributionItemTrim(shell, loginState, null);
		RCPUtil.addContributionItemTrim(shell, server, null);
		RCPUtil.addContributionItemTrim(shell, time, null);
	}
}
