package com.graly.erp.inv.material.onhandvswriteoff;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;



public class OnhandVsWriteOffSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(OnhandVsWriteOffSection.class);
	protected ToolItem itemAssociate;
	protected VStorageMaterial selectedLine;
	private Menu	associateMenu;
	private static final String TABLE_NAME_MATERIALTRACE = "MaterialTrace";

	public OnhandVsWriteOffSection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemAssociate(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemAssociate(final ToolBar tBar) {
		itemAssociate = new ToolItem(tBar, SWT.DROP_DOWN);
		itemAssociate.setText(Message.getString("inv.relationship"));
		itemAssociate.setImage(SWTResourceCache.getImage("search"));
		itemAssociate.setToolTipText(Message.getString("inv.relationship_tip"));
		itemAssociate.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle bounds = itemAssociate.getBounds();
				Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
				associateMenu.setLocation(point);
				associateMenu.setVisible(true);
			}
			
		});
//		createAssociateMenu();
	}

	private void createAssociateMenu() {
		associateMenu = new Menu(UI.getActiveShell(), SWT.POP_UP);
		MenuItem mi = new MenuItem(associateMenu, SWT.PUSH);
		mi.setText("物料追踪");
		mi.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedLine == null) {
					UI.showWarning(Message.getString("inv.entityisnull"));
					return;
				}
				
				ADTable adTable = getADTableByTableName(TABLE_NAME_MATERIALTRACE);
				Map<String,Object> queryKeys = new HashMap<String, Object>();
				queryKeys.put("materialRrn", String.valueOf(selectedLine.getMaterialRrn()));
				MaterialTraceSectionDialog mtd = new MaterialTraceSectionDialog(UI.getActiveShell(), adTable, queryKeys);
				if (mtd.open() == Dialog.OK) {
				}
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
		if(obj instanceof VStorageMaterial) {
			selectedLine = (VStorageMaterial)obj;
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
}
