package com.graly.promisone.base.ui.app;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.graly.promisone.base.ui.action.AbstractContributionItem;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.SWTResourceCache;

public class TimeStatusLineContribution extends AbstractContributionItem {
	private Logger logger = Logger.getLogger(TimeStatusLineContribution.class);
	private Composite wrapper;
	private Label image;
	private static Label text;
	
	public TimeStatusLineContribution(String name, boolean fillToolBar, boolean fillCoolBar, boolean fillMenuBar, boolean fillComposite) {
		super(TimeStatusLineContribution.class.getName(), name, fillToolBar, fillCoolBar, fillMenuBar, fillComposite);
		init();
	}

	public TimeStatusLineContribution(String name) {
		super(TimeStatusLineContribution.class.getName(), name);
		init();
	}
	
	private void init() {
	}
	@Override
	protected Control createControl(Composite parent) {
		wrapper = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		wrapper.setLayout(layout);
		StatusLineLayoutData layoutData = new StatusLineLayoutData();
		layoutData.widthHint = 200;
		wrapper.setLayout(layout);
		wrapper.setLayoutData(layoutData);
		image = new Label(wrapper, SWT.ICON);
		image.setImage(SWTResourceCache.getImage("time"));
		image.setLayoutData(new GridData());
		text = new Label(wrapper, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 120;
		text.setLayoutData(gd);
		String loginTime = I18nUtil.formatDateTime((new Date()));
		text.setText(loginTime);
		
		/*if(timeThread == null) {
			Runnable run = new ClockTime();
			timeThread = new Thread(run);
			timeThread.start();
		}
		*/
		return wrapper;
	}

	/*class ClockTime implements Runnable {
		public void run() {
			boolean flag = true;
			while(flag) {
				if(text != null && !text.isDisposed()) {
					try {
						Thread.sleep(999);
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								synchronized (this) {
									if(text != null && !text.isDisposed()) {
										String time = I18nUtil.formatDateTime((new Date()));
										text.setText(time);
									}
								}
							}
						});
					} catch (Exception e) {
					}
				} else {
					flag = false;
				}
			}
		}
	}
	*/
}
