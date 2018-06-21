package com.graly.promisone.base.security.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.layout.LayoutUtil;
import org.eclipse.ui.internal.layout.TrimLayout;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.base.security.login.ChangeAreaDialog;
import com.graly.promisone.base.ui.views.RefreshTreeView;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.UI;
import com.graly.promisone.base.ui.app.LoginStateStatusLineContribution;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.security.model.ADOrg;
public class ChangeAreaHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ChangeAreaDialog caDialog = new ChangeAreaDialog(UI.getActiveShell());
    	if(caDialog.open() == Dialog.OK) {
    		Long orgId = caDialog.getChangedAreaObjectId();
    		Env.setContext(Env.getCtx(), "#AD_Org_Id", orgId);
    		try {
        		ADManager manager = Framework.getService(ADManager.class);
        		ADOrg org = new ADOrg();
				org.setObjectId(Env.getOrgId());
				org = (ADOrg)manager.getEntity(org);
				Env.setContext(Env.getCtx(), "#AD_Org_Name", org.getName());
    		} catch(Exception e) {
    		}
    		SafeRunner.run(new SafeRunnable() {
    			public void run() {
    				IWorkbenchWindow windows[] = PlatformUI.getWorkbench().getWorkbenchWindows();
    				for (int i = 0; i < windows.length; i++) {
    					IWorkbenchPage pages[] = windows[i].getPages();
    					for (int j = 0; j < pages.length; j++) {
    						pages[j].closeAllEditors(false);
    					}
    				}
    			}
    		});
    		IViewReference viewReferences[] = PlatformUI.getWorkbench()
    			.getActiveWorkbenchWindow().getActivePage().getViewReferences();
    		for (int i = 0; i < viewReferences.length; i++) {
    			IViewPart view = viewReferences[i].getView(false);
    			if (view != null && view instanceof RefreshTreeView) {
    				((RefreshTreeView)view).refresh();
    			}
    		}
    		
    		Shell shell = UI.getActiveShell();
    		if (shell != null && (shell.getLayout() instanceof TrimLayout)){
    			TrimLayout layout = (TrimLayout) shell.getLayout();
    			org.eclipse.ui.internal.WindowTrimProxy trimProxy = 
    				(org.eclipse.ui.internal.WindowTrimProxy)layout.getTrim(LoginStateStatusLineContribution.class.getName());
    			Composite comp = (Composite)trimProxy.getControl();
    			LoginStateStatusLineContribution control = (LoginStateStatusLineContribution)comp.getData();
    			control.areaChanged();
    			trimProxy.setWidthHint(SWT.DEFAULT);
    			LayoutUtil.resize(trimProxy.getControl());
    		}
    	}
    	return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
