package com.graly.erp.inv.material.conventional;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.MaterialConventional;
import com.graly.erp.inv.model.VConventional;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;



public class ConventionalSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(ConventionalSection.class);
	protected ToolItem itemAssociate;
	protected VConventional selectedLine;
	protected ToolItem itemNote;
	public ConventionalSection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemModify(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	protected void createToolItemModify(ToolBar tBar) {
		String authorityToolItem = "Alarm.Iqc.Note";
		itemNote = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemNote.setText("备注");
		itemNote.setImage(SWTResourceCache.getImage("save"));
		itemNote.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				modifyAdapter();
			}
		});
	}
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionLine(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	private void setSelectionLine(Object obj) {
		if(obj instanceof VConventional) {
			selectedLine = (VConventional)obj;
		} else {
			selectedLine = null;
		}
	}
	
//	protected void queryAdapter() {
//		if (queryDialog != null) {
//			queryDialog.setVisible(true);
//		} else {
//			queryDialog =  new EntityQueryDialog4WC(UI.getActiveShell(), tableManager, this);
//			queryDialog.open();
//		}
//	}
	
	protected void refreshSection() {
		refresh();
	}
	
	@Override
	public void refresh() {
		super.refresh();
		compareWithRelValue();
	}
	
	public void compareWithRelValue() {
		if (viewer instanceof TableViewer) {
			TableViewer tViewer = (TableViewer) viewer;
			Table table = tViewer.getTable();

			for (TableItem item : table.getItems()) {
				Object obj = item.getData();
				if (obj instanceof VConventional) {
					VConventional vc = (VConventional) obj;
					BigDecimal qtyOnhand = vc.getQtyOnhand()!=null?vc.getQtyOnhand():BigDecimal.ZERO;
					BigDecimal qtyMin = vc.getQtyMin()!=null?vc.getQtyMin():BigDecimal.ZERO;
					BigDecimal qtyNext= vc.getNextQty()!=null?vc.getNextQty():BigDecimal.ZERO;
					//如果定义了参考价格并且实际采购价高于参考价用红色反显
//					if (qtyOnhand.compareTo(qtyMin)<0) {
//						item.setBackground(new Color(null, 255, 0, 0));
//					}
					if (qtyOnhand.compareTo(qtyNext)<0) {
						item.setBackground(new Color(null, 255, 0, 0));
					}
				}
			}
		}
	}
	
	protected void modifyAdapter() {
		if (selectedLine != null) {
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable adTable = adManager.getADTable(0L, "MaterialConventional");
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
				MaterialConventional mc = new MaterialConventional();
				mc.setObjectRrn(selectedLine.getObjectRrn());
				mc = (MaterialConventional) adManager.getEntity(mc);
				ConventionalModifyDialog modifyDialog = new ConventionalModifyDialog(UI.getActiveShell(),mc,adTable);
				if(modifyDialog.open() == Dialog.OK){
					mc= modifyDialog.getMc();
					adManager.saveEntity(mc, Env.getUserRrn());
					refresh();
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
}
