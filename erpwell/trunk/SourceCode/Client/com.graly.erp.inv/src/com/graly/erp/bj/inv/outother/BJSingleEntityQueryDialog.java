package com.graly.erp.bj.inv.outother;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.query.SingleQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;

/**
 * 备件出货equipmentRrn查询设备对话框
 * */
public class BJSingleEntityQueryDialog extends SingleQueryDialog {
	Logger logger = Logger.getLogger(BJSingleEntityQueryDialog.class);
	
	protected TableViewer tableViewer;
	protected ADBase selectEntity;
	protected String tempSearchCondition;
	
	public BJSingleEntityQueryDialog() {
		super();
	}
	
	public BJSingleEntityQueryDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style){
		super(listTableManager, managedForm, whereClause, style);
	}
	
	public BJSingleEntityQueryDialog(StructuredViewer viewer, Object object) {
		super();
		this.tableViewer = (TableViewer)viewer;
		super.object = object;
	}
	
	@Override
	protected void createSearchTableViewer(Composite parent) {
		listTableManager.setStyle(mStyle);
		tableViewer = (TableViewer)listTableManager.createViewer(parent,
				new FormToolkit(Display.getCurrent()));
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		buttonPressed(IDialogConstants.OK_ID );
	    	}
	    });
	}
	
	@Override
	protected void getInitSearchResult() {
		if(tempSearchCondition != null && !"".equals(tempSearchCondition.trim())) {
			List<ADBase> l = new ArrayList<ADBase>();
			try {
				ADManager manager = Framework.getService(ADManager.class);
				long objectId = listTableManager.getADTable().getObjectRrn();
				l = manager.getEntityList(Env.getOrgRrn(), objectId, 
						Env.getMaxResult(), tempSearchCondition, "");
			} catch (Exception e) {
				logger.error("Error SingleQueryDialog : refresh() " + e.getMessage(), e);
			}
			tableViewer.setInput(l);			
			listTableManager.updateView(tableViewer);			
		}
	}

	@Override
	protected void refresh(boolean clearFlag) {
		List<ADBase> l = new ArrayList<ADBase>();
		try {
        	ADManager manager = Framework.getService(ADManager.class);
        	long objectId = listTableManager.getADTable().getObjectRrn();
        	String equipmentId =null;
			if (Env.getAuthority() != null) {
				if (!Env.getAuthority().contains("BJ.EquipmentId")) {
					equipmentId = Message.getString("bj.equipmentid");//特殊处理值，只允许特殊的人能查到
				}
			}
			String keys = getKeys();
			if(equipmentId != null){
				keys = keys+" AND BJWipEquipment.equipmentId <> '"+equipmentId+"'";
			}
            l = manager.getEntityList(Env.getOrgRrn(), objectId, 
            		Env.getMaxResult(), keys, "");
        } catch (Exception e) {
        	logger.error("Error SingleQueryDialog : refresh() " + e.getMessage(), e);
        }
		if (object instanceof List) {
			exsitedItems = (List)object;
			if (exsitedItems != null) {
				l.removeAll(exsitedItems);
			}
		}
		tableViewer.setInput(l);			
		listTableManager.updateView(tableViewer);
	}

	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID) {
			if(tableViewer.getTable().getSelection().length > 0) {
				TableItem ti = tableViewer.getTable().getSelection()[0];
				selectEntity = (ADBase)ti.getData();
			}
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	public ADBase getSelectionEntity() {
		return selectEntity;
	}
	
	public void setTempSearchCondition(String temp) {
		this.tempSearchCondition = temp;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
        		Message.getString("common.ok"), false);
        createButton(parent, IDialogConstants.CANCEL_ID,
        		Message.getString("common.cancel"), false);
	}
}
