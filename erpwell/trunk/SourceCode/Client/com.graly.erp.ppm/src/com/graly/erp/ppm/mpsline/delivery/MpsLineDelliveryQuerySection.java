package com.graly.erp.ppm.mpsline.delivery;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.ppm.model.MpsLineDelivery;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class MpsLineDelliveryQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MpsLineDelliveryQuerySection.class);

	protected ToolItem itemAgree;
	protected ToolItem itemNoAgree;
	protected ToolItem itemDelete;

	protected TableListManager listTableManager;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	
	public MpsLineDelliveryQuerySection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	 
	
	protected ADTable getADTableOfInLineDialog(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
		}
		return null;
	}
	
	protected ADTable getADTableOfMovement(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("InSection : getADTableOfRequisition()", e);
		}
		return null;
	}
	
	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
		}
		return null;
	}
	
	//总共多少条记录
	protected void createSectionDesc(List<MpsLineDelivery> lineDelays){
		try{ 
			String text = Message.getString("common.totalshow");
			long count = lineDelays.size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("MasterSection : createSectionDesc ", e);
		}
	}
	
	
	
	@Override
	public void refresh() {
		
//		List<VMpsLineDelivery> moLineDelays =new ArrayList<VMpsLineDelivery>();
//		ADManager adManager;
//		try {
//			adManager = Framework.getService(ADManager.class);
//			moLineDelays = adManager.getEntityList(Env.getOrgRrn(), VMpsLineDelivery.class,Integer.MAX_VALUE,
//					"","");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		viewer.setInput(moLineDelays);
//		tableManager.updateView(viewer);
////		this.createSectionDesc(moLineDelays);
		
		
		List<MpsLineDelivery> moLineDelays =new ArrayList<MpsLineDelivery>();
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			moLineDelays = adManager.getEntityList(Env.getOrgRrn(), MpsLineDelivery.class,Integer.MAX_VALUE,
					getWhereClause(),"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewer.setInput(moLineDelays);
		tableManager.updateView(viewer);
		this.createSectionDesc(moLineDelays);
	}
}

