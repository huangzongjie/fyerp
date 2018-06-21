package com.graly.promisone.base.security.handlers;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.StartupThreading;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.StartupThreading.StartupRunnable;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.internal.misc.Policy;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.bindings.BindingManager;

import com.graly.promisone.base.application.Application;
import com.graly.promisone.base.application.ApplicationWorkbenchAdvisor;
import com.graly.promisone.base.security.login.LoginDialog;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.core.exception.ClientException;

public class ReLoginHandlerHandler implements IHandler {
	private LoginDialog loginDialog;
	private final static Logger logger = Logger.getLogger(Application.class);

	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		BindingService service  = (BindingService)PlatformUI.getWorkbench().getService(IBindingService.class);
		if (service != null) {
			Listener listener = service.getKeyboard().getKeyDownFilter();
			PlatformUI.getWorkbench().getDisplay().removeFilter(SWT.KeyDown, listener);
			PlatformUI.getWorkbench().getDisplay().removeFilter(SWT.Traverse, listener);
		}
		PlatformUI.getWorkbench().restart();
		Display display = PlatformUI.getWorkbench().getDisplay();

		if (authenticate(display)) {
			PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
		}
		return null;
	}
	
	private boolean authenticate(Display display) {
		LoginDialog loginDialog = new LoginDialog(display);
		loginDialog.createContents();
		if (LoginDialog.CANCEL == loginDialog.getReturnCode()){
			return false;
		}
		return true;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}
}
