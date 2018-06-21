package com.graly.framework.base.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.part.ViewPart;

import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;


public abstract class RefreshView extends ViewPart {
	private IAction refreshWAction;
	
	public abstract void refresh();

	@Override
	public void createPartControl(Composite parent) {
		createActions();
		initToolBar();
	}
	
	 private void createActions() {
		 refreshWAction = new RefreshAction();
		 refreshWAction.setToolTipText(Message.getString("common.refresh"));
		 ImageDescriptor image = ImageDescriptor.createFromImage(SWTResourceCache.getImage("refresh"));
		 refreshWAction.setImageDescriptor(image);
	 }
	 
	 private void initToolBar(){
		 IActionBars bars = getViewSite().getActionBars();
		 IToolBarManager toolBarManager = bars.getToolBarManager();
		 toolBarManager.add(refreshWAction);
	 }
	 
	 class RefreshAction extends Action{
		@Override
		public void run() {
			refresh();
		}		 
	 }
}
