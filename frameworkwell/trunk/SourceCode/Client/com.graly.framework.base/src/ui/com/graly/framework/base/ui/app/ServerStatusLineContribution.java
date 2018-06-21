package com.graly.framework.base.ui.app;

import java.util.Locale;

import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;

import com.graly.framework.base.ui.action.AbstractContributionItem;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.action.AbstractContributionItem;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;

public class ServerStatusLineContribution extends AbstractContributionItem {
	
	private Composite wrapper;
	private Label image;
	private Label text;
	
	public ServerStatusLineContribution(String name, boolean fillToolBar, boolean fillCoolBar, boolean fillMenuBar, boolean fillComposite) {
		super(ServerStatusLineContribution.class.getName(), name, fillToolBar, fillCoolBar, fillMenuBar, fillComposite);
		init();
	}

	public ServerStatusLineContribution(String name) {
		super(ServerStatusLineContribution.class.getName(), name);
		init();
	}
	
	private void init() {
	}
	
	@Override
	protected Control createControl(Composite parent) {
		wrapper = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		wrapper.setLayout(layout);
		StatusLineLayoutData layoutData = new StatusLineLayoutData();
		layoutData.widthHint = 200;
		wrapper.setLayoutData(layoutData);
		image = new Label(wrapper, SWT.ICON);
		image.setImage(SWTResourceCache.getImage("server")); 
		image.setLayoutData(new GridData());
		text = new Label(wrapper, SWT.NONE);		
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		try{
			String hostName = System.getProperty("hostname") != null ? System.getProperty("hostname") : "";
			text.setText(hostName);;
		} catch (Exception e) {
		}
		return wrapper;
	}
	
}
