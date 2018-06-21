package com.graly.framework.base.ui.util;


import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.layout.TrimLayout;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.custom.XCombo;
import com.graly.framework.runtime.Framework;

public class RCPUtil {
	private static final Logger logger = Logger.getLogger(RCPUtil.class);
	private static final String TABLE_NAME = "ADUserRefName";
	
	public static void addContributionItemTrim(
			Shell shell, 
			IContributionItem contributionItem, 
			String prependTo) {
		if (shell != null && (shell.getLayout() instanceof TrimLayout)) {
			TrimLayout layout = (TrimLayout) shell.getLayout();
			Composite comp = new Composite(shell, SWT.NONE);
			contributionItem.fill(comp);
			comp.setData(contributionItem);
			org.eclipse.ui.internal.WindowTrimProxy trimProxy = new org.eclipse.ui.internal.WindowTrimProxy(
					comp,
					contributionItem.getId(), 
					contributionItem.getClass().getSimpleName(), SWT.RIGHT_TO_LEFT | SWT.TOP ) {

				@Override
				public void handleClose() {
					getControl().dispose();
				}

				@Override
				public boolean isCloseable() {
					return true;
				}
			};
			trimProxy.setWidthHint(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
			trimProxy.setHeightHint(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			if (prependTo != null && !"".equals(prependTo)) {
				org.eclipse.ui.internal.layout.IWindowTrim prependTrim = layout.getTrim(prependTo);
				layout.addTrim(SWT.BOTTOM, trimProxy, prependTrim);
			} else {
				layout.addTrim(SWT.BOTTOM, trimProxy);
			}

			comp.setVisible(true);
		}
	}
	
	public static XCombo getUserRefListCombo(Composite parent, String referenceName, long orgId) {
		ADTable adTable;
		FormToolkit toolkit = new FormToolkit(Display.getCurrent().getActiveShell().getDisplay());
		try {
			ADManager adManager = Framework.getService(ADManager.class);
    		adTable  = adManager.getADTable(Env.getOrgRrn(), TABLE_NAME);
    		
    		TableListManager tableManager = new TableListManager(adTable);
			TableViewer viewer = (TableViewer)tableManager.createViewer(Display.getCurrent().getActiveShell(),
					toolkit);

			if(referenceName != null && !"".equals(referenceName.trim())) {
				String whereClause = " referenceName = '" + referenceName + "'";
				List<ADBase> list = adManager.getEntityList(orgId, adTable.getObjectRrn(),
						Env.getMaxResult(), whereClause, null);
				viewer.setInput(list);
			}
			
			XCombo combo = new XCombo(parent, viewer, "key", "value", SWT.READ_ONLY);
	        toolkit.adapt(combo);
	        toolkit.paintBordersFor(combo);
	        
	        return combo;
		} catch(Exception e) {
			logger.error("Error at RCPUtil : getUserRefListCombo() : " + e);
		}
		return null;
	}
	
	public static void refreshUserRefListCombo(XCombo combo, String referenceName) {
		if(referenceName != null && !"".equals(referenceName.trim())) {
			ADTable adTable;
			try {
				ADManager adManager = Framework.getService(ADManager.class);
	    		adTable  = adManager.getADTable(Env.getOrgRrn(), TABLE_NAME);
	    		
	    		String whereClause = " referenceName = '" + referenceName + "'";
				List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(),
						Env.getMaxResult(), whereClause, null);
				combo.getTableViewer().setInput(list);
			} catch(Exception e) {
				logger.error("Error at RCPUtil : refreshUserRefListCombo() : " + e);
			}
		}
	}
	
	public static void startBrowser (String url) {
		String[] UNIX_BROWSERS = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
		try {
			String osName = System.getProperty("os.name").toLowerCase();
			if (osName.startsWith("mac")) {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
											  new Class[] {String.class});
				openURL.invoke(null, new Object[] {url});
			}
			else if (osName.startsWith("windows"))
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			else {
				//assume Unix or Linux
				String browser = null;
				String[] which = new String[] {"which", null};
				for (int count = 0;
					 count < UNIX_BROWSERS.length && browser == null;
					 count++)
				{
					which[1] = UNIX_BROWSERS[count];
					if (Runtime.getRuntime().exec(which).waitFor() == 0)
						browser = UNIX_BROWSERS[count];
				}
				if (browser == null)
					throw new Exception("Could not find web browser");
				else
					Runtime.getRuntime().exec(new String[] {browser, url});
			}
		} catch (Exception e) {
			logger.error("Can't locate browser: " + e.getLocalizedMessage());
		}
	}
}
