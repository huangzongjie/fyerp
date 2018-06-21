package com.graly.erp.inv.material.kegongquery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.pur.dialog.PurAssociatedDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialQtyQuerySection extends QuerySection {
	private Logger logger = Logger.getLogger(MaterialQtyQuerySection.class);
	
	public static final String TABLE_NAME_PR_LINE = "WorkCenterRequisitionLine";
	public static final String TABLE_NAME_PO_LINE = "WorkCenterPurchaseOrderLine";
	private Map<String,Object>	queryKeys;

	public MaterialQtyQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1 ");
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	@Override
	protected void createNewViewer(Composite client, IManagedForm form) {
		super.createNewViewer(client, form);
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				Material selectedLine = (Material) ((StructuredSelection) event.getSelection()).getFirstElement();
				try {
					if(selectedLine != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						ADTable prTable = adManager.getADTable(0L, TABLE_NAME_PR_LINE);
						prTable = adManager.getADTableDeep(prTable.getObjectRrn());
						ADTable poTable = adManager.getADTable(0L, TABLE_NAME_PO_LINE);
						poTable = adManager.getADTableDeep(poTable.getObjectRrn());
						
						Long materialRrn = selectedLine.getObjectRrn();
						String prWhereClause = "qty-(case when qtyOrdered is null then 0 else qtyOrdered end) >0";
						String poWhereClause = "qty-(case when qtyIn is null then 0 else qtyIn end) >0";

						PurAssociatedDialog cd = new PurAssociatedDialog(UI.getActiveShell(),
								prTable, poTable, materialRrn,prWhereClause,poWhereClause);
						if(cd.open() == Dialog.CANCEL) {
						}
					}
				} catch(Exception e) {
					ExceptionHandlerManager.asyncHandleException(e);
				}
			}
			
		});
	}
	
	@Override
	public void refresh() {
		try {
			if(queryDialog != null){
				queryKeys = queryDialog.getQueryKeys();
			}
			List ls = new ArrayList();
			
			INVManager invManager = Framework.getService(INVManager.class);
			if(queryKeys != null){
				if(!queryKeys.isEmpty()){
					long materialRrn = Long.valueOf((String) queryKeys.get("objectRrn"));
					ls = invManager.queryMaterialQtysKeGong(Env.getOrgRrn(), materialRrn,"客供",null);
				}else{
					ls = invManager.queryMaterialQtysKeGong(Env.getOrgRrn(), null,"客供",null);
				}
			}
			viewer.setInput(ls);		
			tableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createSectionDesc(Section section) {
		try{ 
			String text = Message.getString("common.totalshow");
			long count = ((List)viewer.getInput()).size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}
}
