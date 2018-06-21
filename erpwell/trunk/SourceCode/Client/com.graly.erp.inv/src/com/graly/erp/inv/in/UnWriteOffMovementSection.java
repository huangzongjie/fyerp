package com.graly.erp.inv.in;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class UnWriteOffMovementSection extends MasterSection {
	private ToolItem itemWriteOff;
	private ADTable movementLineTable; //审核或冲销时的movement line Table
	
	private static final String TABLE_NAME_INVOICE_MOVEMENTLINE = "INVFinanceMovementLine";
	
	public UnWriteOffMovementSection() {
		super();
	}

	public UnWriteOffMovementSection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" docStatus in ('APPROVED') ");
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemWriteOff(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemWriteOff(ToolBar tBar) {
		itemWriteOff = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PIN_WRITEOFF);
		itemWriteOff.setText(Message.getString("inv.in_write_off"));
		itemWriteOff.setImage(SWTResourceCache.getImage("voice"));
		itemWriteOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				writeOffAdapter();
			}
		});
	}
	
	protected void writeOffAdapter() {
		try {
			List<MovementIn> movements = new ArrayList<MovementIn>();
			for(Object obj : ((CheckboxTableViewer)viewer).getCheckedElements()){
				if(obj instanceof MovementIn){
					MovementIn in = (MovementIn)obj;
					INVManager invManager = Framework.getService(INVManager.class);
					List<MovementLine> lines = invManager.getMovementLines(in);
					in.setMovementLines(lines);
					movements.add(in);
				}
			}
			if (movements != null) {
				UnWriteOffMovementLineDialog movementLineDialog = new UnWriteOffMovementLineDialog(UI.getActiveShell(),
						 getInvoiceMovementLineTable(), movements, true);
				if(movementLineDialog.open() == Dialog.OK) {
					List<MovementIn> ins = movementLineDialog.getPurMovements();
					
					INVManager invManager = Framework.getService(INVManager.class);
					movements = invManager.writeOffMovementIns(ins, Env.getUserRrn());
					UI.showInfo(Message.getString("inv.in_write_off_successful"));
					itemWriteOff.setEnabled(false);
					refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	@Override
	protected void createNewViewer(Composite client, final IManagedForm form){
		Composite viewArea = form.getToolkit().createComposite(client);
		GridLayout gl1= new GridLayout(1, false);
		gl1.marginWidth = 0;
		gl1.marginHeight = 0;
		gl1.horizontalSpacing = 0;
		gl1.verticalSpacing = 0;
		viewArea.setLayout(gl1);
		viewArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		final ADTable table = getADTable();
		getTableManager().setStyle(SWT.CHECK|SWT.FULL_SELECTION);
		viewer = getTableManager().createViewer(viewArea, form.getToolkit());
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try{
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase)obj).setOrgRrn(Env.getOrgRrn());
						}
						form.fireSelectionChanged(spart, new StructuredSelection(new Object[] {obj}));
					} catch (Exception e){
						e.printStackTrace();
					}
				} else {
					form.fireSelectionChanged(spart, event.getSelection());
				}
			}
		});
	    Composite buttonArea = form.getToolkit().createComposite(client);
	    GridLayout gl2= new GridLayout(2,false);
		gl2.marginWidth = 0;
		gl2.marginHeight = 0;
		gl2.horizontalSpacing = 0;
		gl2.verticalSpacing = 0;
	    buttonArea.setLayout(gl2);
	    buttonArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    Button all = form.getToolkit().createButton(buttonArea, "全选", SWT.PUSH);
	    all.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableViewer tableViewer = (TableViewer)viewer;
				TableItem[] items = tableViewer.getTable().getItems();
				for(TableItem item : items){
					item.setChecked(true);
				}
			}
	    	
	    });
	    Button inverse = form.getToolkit().createButton(buttonArea, "反选", SWT.PUSH);
	    inverse.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableViewer tableViewer = (TableViewer)viewer;
				TableItem[] items = tableViewer.getTable().getItems();
				for(TableItem item : items){
					item.setChecked(!item.getChecked());
				}
			}
	    	
	    });
	    
	    refresh();
	    createViewAction(viewer);
	}
	
	protected ADTable getInvoiceMovementLineTable() throws Exception {
//		try {
			if (movementLineTable == null) {
				ADManager adManager = Framework.getService(ADManager.class);
				movementLineTable = adManager.getADTable(0L, TABLE_NAME_INVOICE_MOVEMENTLINE);
			}
			return movementLineTable;
//		} catch (Exception e1) {
//			ExceptionHandlerManager.asyncHandleException(e1);
//		}
	}
}
