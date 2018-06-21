package com.graly.framework.base.security.login;

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
import com.graly.framework.base.ui.app.LocaleStatusLineContribution;

public class LoginStateStatusLineContribution extends AbstractContributionItem {
	
	private Composite wrapper;
	private Label image;
	private Label text;
	private String earlyLoginText;
	
	public LoginStateStatusLineContribution(String name, boolean fillToolBar, boolean fillCoolBar, boolean fillMenuBar, boolean fillComposite) {
		super(LoginStateStatusLineContribution.class.getName(), name, fillToolBar, fillCoolBar, fillMenuBar, fillComposite);
		init();
	}

	public LoginStateStatusLineContribution(String name) {
		super(LoginStateStatusLineContribution.class.getName(), name);
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
		image.setImage(SWTResourceCache.getImage("login")); //$NON-NLS-1$
		image.setLayoutData(new GridData());
		text = new Label(wrapper, SWT.NONE);		
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Set some dummy text to give the item some width.
		text.setText("********@************ on *********************"); //$NON-NLS-1$
		if (earlyLoginText != null) { // if the login happened already before UI creation
			text.setText(earlyLoginText);
			text.setToolTipText(earlyLoginText);
			earlyLoginText = null;
		}
		wrapper.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
//				try {
//					Login.getLogin(false).removeLoginStateListener(LoginStateStatusLineContribution.this);
//				} catch (LoginException e) {
//					throw new RuntimeException(e);
//				}
			}
		});
		return wrapper;
	}

}
