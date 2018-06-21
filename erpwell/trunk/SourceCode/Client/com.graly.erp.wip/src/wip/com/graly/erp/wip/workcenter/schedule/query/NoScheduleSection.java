package com.graly.erp.wip.workcenter.schedule.query;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.WorkShopNoSchedule;
import com.graly.erp.wip.workcenter.bom.WorkOrderBomDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class NoScheduleSection extends MasterSection {
	protected ToolItem itemBomTree;
	protected WorkShopNoSchedule selectNoSchedule;
	
	public NoScheduleSection(EntityTableManager tableManager) {
		super(tableManager);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemBomTree(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionWorkShopNoSchedule(ss.getFirstElement());
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionWorkShopNoSchedule(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void createToolItemBomTree(ToolBar tBar) {
		itemBomTree = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_BOM);
		itemBomTree.setText(Message.getString("pdm.bom"));
		itemBomTree.setImage(SWTResourceCache.getImage("bomtree"));
		itemBomTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				bomTreeAdapter();
			}
		});
	}
	
	private void setSelectionWorkShopNoSchedule(Object obj) {
		if (obj instanceof WorkShopNoSchedule) {
			selectNoSchedule = (WorkShopNoSchedule) obj;
		} else {
			selectNoSchedule = null;
		}
	}
	
	
	protected void bomTreeAdapter() {
		if (selectNoSchedule != null && selectNoSchedule.getObjectRrn() != null) {
			ADManager adManager;
			try {
				adManager = Framework.getService(ADManager.class);
			
			ManufactureOrderLine selectMoLine = new ManufactureOrderLine();
			selectMoLine.setObjectRrn(selectNoSchedule.getMoLineRrn());
			selectMoLine = (ManufactureOrderLine) adManager.getEntity(selectMoLine);
			WorkOrderBomDialog dialog = new WorkOrderBomDialog(UI.getActiveShell(), selectMoLine, tableManager.getADTable());
			dialog.open();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
