package com.graly.framework.base.application;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ContainerPlaceholder;
import org.eclipse.ui.internal.LayoutPart;
import org.eclipse.ui.internal.Perspective;
import org.eclipse.ui.internal.ViewPane;
import org.eclipse.ui.internal.ViewStack;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.graly.framework.base.ui.FrameworkPerspective;
import com.graly.framework.base.ui.IMinimize;
import com.graly.framework.base.ui.app.LocaleStatusLineContribution;
import com.graly.framework.base.ui.app.LoginStateStatusLineContribution;
import com.graly.framework.base.ui.app.ServerStatusLineContribution;
import com.graly.framework.base.ui.app.TimeStatusLineContribution;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.RCPUtil;
import com.graly.framework.base.ui.util.UI;

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

        configurer.setTitle("ERPwell");
        PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.ENABLE_ANIMATIONS, true);
    	PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,false);
    	PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
    	
    	configurer.getWindow().addPerspectiveListener(perspectiveListener);
    }
    
    public void postWindowOpen() {
    	getWindowConfigurer().getWindow().getShell().setMaximized(true);
    	Display.getCurrent().asyncExec(new Runnable(){

			@Override
			public void run() {
				MessageBox mb = new MessageBox(new Shell(Display.getDefault()), SWT.OK | SWT.ICON_INFORMATION | SWT.MODELESS);
				mb.setText(Message.getString("common.update_notice"));
				String message = Message.getString("common.update_contents");
				mb.setMessage(String.format(Message.getString("common.update_contents_prefix"),message));
				if(message != null && message.trim().length() > 0){
					mb.open();
				}
			}
			
		});
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
    
	@SuppressWarnings("restriction")
	private IPerspectiveListener perspectiveListener = new IPerspectiveListener() {
		public void perspectiveActivated(IWorkbenchPage page, 
				IPerspectiveDescriptor perspective1) {
			Perspective perspective = ((WorkbenchPage)page).getActivePerspective();
			
			boolean minimiz = true;
			LayoutPart part = perspective.getPresentation().findPart(FrameworkPerspective.FOLDER_BOTTOM, null);
			ViewStack stack = null;
			if (part instanceof ContainerPlaceholder) {
				stack = (ViewStack) ((ContainerPlaceholder)part).getRealContainer();
			} else if (part instanceof ViewStack) {
				stack = (ViewStack)part;
			}
			if (stack == null) {
				return;
			}
			LayoutPart[] children = stack.getChildren();
			for (LayoutPart child : children) {
				if (child instanceof ViewPane) {
					ViewPane pane = (ViewPane)child;
					IWorkbenchPart wPart = pane.getPartReference().getPart(false);
					if (wPart instanceof IMinimize) {
						IMinimize iPart = (IMinimize)wPart;
						if (!iPart.getFirstActive()) {
							return;
						}
						iPart.setFirstActive(false);
						if (!iPart.getMinimize()) {
							minimiz = false;
							break;
						}
					}
				}	
			}
			if (minimiz) {
				stack.setMinimized(true);
			}
		}
		
		public void perspectiveChanged(IWorkbenchPage page,
				IPerspectiveDescriptor perspective, String changeId) {
			
		}
	};
}
