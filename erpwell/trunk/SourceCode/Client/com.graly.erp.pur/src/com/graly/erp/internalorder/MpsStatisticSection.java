package com.graly.erp.internalorder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.MpsLineDelivery;
import com.graly.erp.ppm.model.MpsStatistcLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MpsStatisticSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MpsStatisticSection.class);
	private PPMManager ppmManager;
	protected Map<String,Object> queryKeys = new LinkedHashMap<String,Object>();
	protected ToolItem itemMpsLineNotice;
	
	public Map<String, Object> getQueryKeys() {
		return queryKeys;
	}

	public void setQueryKeys(Map<String, Object> queryKeys) {
		this.queryKeys = queryKeys;
	}

	public MpsStatisticSection() {
		super();
	}

	public MpsStatisticSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	protected void createNewViewer(Composite client, final IManagedForm form){
		final ADTable table = getTableManager().getADTable();
		viewer = getTableManager().createViewer(client, form.getToolkit());
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
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				MpsStatistcLine msl = (MpsStatistcLine) ss.getFirstElement();
				if(msl!=null){
					try {
						PPMManager adManager = Framework.getService(PPMManager.class);
						boolean flag = adManager.getMoByMpsStatistcLine(Env.getOrgRrn(),msl );
						if(flag){
							itemMpsLineNotice.setEnabled(false);
						}else{
							itemMpsLineNotice.setEnabled(true);
						}
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					itemMpsLineNotice.setEnabled(true);
				}
				
			}
		});
	    final TableViewer tv = (TableViewer) viewer;
	    CellEditor[] editors = new CellEditor[tv.getTable().getColumnCount()];
	    for(int i=0;i<editors.length;i++){
	    	editors[i]=new MpsStatisticLinePropertyEditor(tv.getTable());
	    	editors[i].setValidator(new ICellEditorValidator(){

				@Override
				public String isValid(Object value) {
					try {
						if(value instanceof String){
							new BigDecimal((String)value);
						}
					} catch (RuntimeException e) {
						return "输入的是无效数值";
					}
					return null;
				}});
	    }
	    tv.setCellEditors(editors);
	    tv.setCellModifier(new ICellModifier(){

			@Override
			public boolean canModify(Object element, String property) {
				if("temporaryQty".equals(property)||"internalOrderQty".equals(property)){
					return true;
				}
				return false;
			}

			@Override
			public Object getValue(Object element, String property) {
				// TODO Auto-generated method stub
				if(element instanceof MpsStatistcLine){
					MpsStatistcLine msl = (MpsStatistcLine) element;
					if("temporaryQty".equals(property)){
						return msl.getTemporaryQty();
					}else if("internalOrderQty".equals(property)){
						return msl.getInternalOrderQty();
					}
				}
				return "";
			}

			@Override
			public void modify(Object element, String property,
					Object value) {
				if(element instanceof TableItem){
					if(value instanceof String){
						try {
							TableItem ti = (TableItem) element;
							MpsStatistcLine msl = (MpsStatistcLine)ti.getData();
							if("temporaryQty".equals(property)){
								msl.setTemporaryQty(new BigDecimal((String)value));
							}else if("internalOrderQty".equals(property)){
								msl.setInternalOrderQty(new BigDecimal((String)value));
							}
							if(ppmManager == null){
								ppmManager = Framework.getService(PPMManager.class);
							}
							
							ppmManager.updateMpsLineByStatisticLine(Env.getOrgRrn(), Env.getUserRrn(), msl);
							BigDecimal qtyFormula = msl.getQtyHandOn().add(msl.getQtyTransit()).subtract(msl.getQtyLading()).subtract(msl.getQtySalePlan()).add(msl.getQtyMps()).add(msl.getTemporaryQty()).subtract(msl.getInternalOrderQty());
							msl.setQtyFormula(qtyFormula);
							tv.refresh();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}});
	    refresh();
	    createViewAction(viewer);
	}
	
	protected void createSectionDesc(Section section){
		try{ 
			String text = Message.getString("common.totalshow");
			Object input = viewer.getInput();
			long count = 0;
			if(input instanceof List){
				count = ((List)input).size();
			}
			text = String.format(text, String.valueOf(count), String.valueOf(count));
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("MasterSection : createSectionDesc ", e);
		}
	}
	
	public void refresh(){
		try {
			if(ppmManager == null){
				ppmManager = Framework.getService(PPMManager.class);
			}
			
			if(!queryKeys.isEmpty()){
				Long materialRrn = queryKeys.get("materialRrn") == null?null:Long.parseLong((String) queryKeys.get("materialRrn"));
				String mpsId = (String) queryKeys.get("mpsId");
				viewer.setInput(ppmManager.statisticMps(Env.getOrgRrn(), mpsId, materialRrn));		
			}
			tableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MpsStatisticQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemDelivery(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		super.createToolItemSearch(tBar);
		super.createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		super.createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemDelivery(ToolBar tBar) {
		itemMpsLineNotice = new ToolItem(tBar, SWT.PUSH);
		itemMpsLineNotice.setText("主计划交期通知");
		itemMpsLineNotice.setImage(SWTResourceCache.getImage("save"));
		itemMpsLineNotice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				mpsLineNoticeAdapter();
			}
		});
	}
	
	protected void mpsLineNoticeAdapter() {	
		try {
				StructuredSelection ss = (StructuredSelection)this.viewer.getSelection();
				MpsStatistcLine staticLine =  (MpsStatistcLine) ss.getFirstElement();
				if(staticLine!=null){
					ADManager adManager = Framework.getService(ADManager.class);
					PPMManager ppmManager = Framework.getService(PPMManager.class);

					MpsLineDelivery lineDelivery = new MpsLineDelivery();
					lineDelivery.setIsActive(true);
					lineDelivery.setOrgRrn(Env.getOrgRrn());
					lineDelivery.setMpsId(staticLine.getMpsId());
					lineDelivery.setMaterialRrn(staticLine.getMaterialRrn());
					lineDelivery.setMaterialId(staticLine.getMaterialId());
					lineDelivery.setMaterialName(staticLine.getMaterialName());
					lineDelivery.setUomId(staticLine.getUomId());
//					MpsLine mpsLine = new MpsLine();
//					mpsLine.setObjectRrn(staticLine.getMpsLineRrn());
//					mpsLine = (MpsLine) adManager.getEntity(mpsLine);
					
					ADTable adTable = adManager.getADTable(0L, "PPMMpsLineDeliveryNotice");
					adTable = adManager.getADTableDeep(adTable.getObjectRrn());
					DeliveryDialog deliveryDialog = new DeliveryDialog(UI.getActiveShell(),lineDelivery,adTable);
					if(deliveryDialog.open() == Dialog.OK){
						lineDelivery = deliveryDialog.getLineDelivery();
						staticLine.setInternalOrderQty(staticLine.getInternalOrderQty().add(lineDelivery.getQty()));
						if(lineDelivery!=null){
							ppmManager.addMpsLineDelivery(Env.getOrgRrn(), Env.getUserRrn(),staticLine,lineDelivery);
						}
						UI.showInfo(Message.getString("common.save_successed"));
						refresh();
					}
				}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}		
	}
	
}

class MpsStatisticLinePropertyEditor extends DialogCellEditor{

	public MpsStatisticLinePropertyEditor() {
		super();
	}

	public MpsStatisticLinePropertyEditor(Composite parent, int style) {
		super(parent, style);
	}

	public MpsStatisticLinePropertyEditor(Composite parent) {
		super(parent);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		InputDialog d = new InputDialog(cellEditorWindow.getShell(),"输入框","在此输入新值",this.getValue().toString(),new IInputValidator(){

			@Override
			public String isValid(String newText) {
				try {
					new BigDecimal(newText);
				} catch (RuntimeException e) {
					//setErrorMessage("输入的是无效数值");
					return "输入的是无效数值";
				}
				return null;
			}});
		d.open();
		return d.getValue();
	}
}
