package com.graly.framework.base.ui.report;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;


public class PreviewDialog extends Dialog {
	static final int BROWSER_STYLE = SWT.NONE;

	private Browser browser;
	private String report;
	private Map<String, Object> params;
	private Map<String, String> userParams;
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 500;

	public PreviewDialog(Shell parent, String report, Map<String, String> userParams) {
		super(parent);
		this.report = report;
		this.userParams = userParams;
	}
	
	public PreviewDialog(Shell parent, String report, Map<String, Object> params, Map<String, String> userParams) {
		super(parent);
		this.report = report;
		this.params = params;
		this.userParams = userParams;
	}

	protected Control createDialogArea(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		initialize(parent, browser);

		GridData gd = new GridData(GridData.FILL_BOTH);
        gd.minimumWidth = 600 ;
        gd.minimumHeight = 400 ;
        String url = ReportUtil.createURL(report, params, userParams);
        browser.setUrl(url);
        browser.setLayoutData(gd);
		applyDialogFont(browser);
		return browser;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		//do nothing
	}

	

	@Override
	public boolean close() {
		boolean sure = UI.showConfirm(Message.getString("common.confirm_exit"));
		if(sure){
			return super.close();
		}
		return false;
	}
	
	public void forceClose(){
		super.close();
	}
	
	/* register WindowEvent listeners */
	static void initialize(final Composite parent, Browser browser) {
		browser.addOpenWindowListener(new OpenWindowListener() {
			public void open(WindowEvent event) {
//				Shell shell = new Shell(display);
//				shell.setText("New Window");
//				shell.setLayout(new FillLayout());
				Browser browser = new Browser(parent, BROWSER_STYLE);
				initialize(parent, browser);
				event.browser = browser;
			}
		});
		browser.addVisibilityWindowListener(new VisibilityWindowListener() {
			public void hide(WindowEvent event) {
				Browser browser = (Browser)event.widget;
				Shell shell = browser.getShell();
				shell.setVisible(false);
			}
			public void show(WindowEvent event) {
				Browser browser = (Browser)event.widget;
				final Shell shell = browser.getShell();
				shell.open();
			}
		});
		browser.addCloseWindowListener(new CloseWindowListener() {
			public void close(WindowEvent event) {
				Browser browser = (Browser)event.widget;
				Shell shell = browser.getShell();
				shell.close();
			}
		});
	}

}
