package com.graly.promisone.base.entitymanager.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.promisone.base.entitymanager.adapter.EntityItemInput;
import com.graly.promisone.base.entitymanager.views.TableListManager;

public class SingleEntityQueryDialog extends SingleQueryDialog {
	
	protected TableViewer tableViewer;
	protected ADBase selectEntity;
	protected String tempSearchCondition;
	
	public SingleEntityQueryDialog(Shell shell) {
		super(shell);
	}
	
	public SingleEntityQueryDialog(Shell parent, TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style){
		super(parent, listTableManager, managedForm, whereClause, style);
	}
	
	public SingleEntityQueryDialog(Shell parent, StructuredViewer viewer, Object object) {
		super(parent);
		this.tableViewer = (TableViewer)viewer;
		super.object = object;
	}
	
	@Override
	protected void createSearchTableViewer(Composite parent) {
		listTableManager.setStyle(mStyle);
		tableViewer = (TableViewer)listTableManager.createViewer(parent,
				new FormToolkit(Display.getCurrent()));
	}

	@Override
	protected void refresh(boolean clearFlag) {
		EntityItemInput eii = new EntityItemInput(listTableManager.getADTable(), getKeys(), "");
		List<Object> l = new ArrayList<Object>();
		Object[] obj = new EntityItemAdapter().getElements(eii);
		for(Object o : obj) {
			l.add(o);
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
}
