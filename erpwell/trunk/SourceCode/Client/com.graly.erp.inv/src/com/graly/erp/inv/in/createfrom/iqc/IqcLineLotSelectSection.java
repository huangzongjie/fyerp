package com.graly.erp.inv.in.createfrom.iqc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class IqcLineLotSelectSection {
	private static final Logger logger = Logger.getLogger(IqcLineLotSelectSection.class);
	private String VALUE_FIELD_NAME = "objectRrn";
	private String KEY_FIELD_NAME = "warehouseId";
	private String TableName_VUser_Warehouse = "VUserWarehouse";
	
	private IqcLineLotSelectPage parentPage;
	private ADTable adTable;
	private ManagedForm form;
//	private CheckEntityTableManager tableManager;
	private TableListManager listTableManager;
	private CheckboxTableViewer viewer;
	private Iqc parentIqc;
	private List<IqcLine> lines;
	private Text txtLotId;
	private RefTableField refField;
	private Iqc iqc;
	private Map<Lot, IqcLine> iqcLineMap;
	private INVManager invManager;

	public IqcLineLotSelectSection(ADTable table, IqcLineLotSelectPage parentPage, Iqc iqc) {
		this.adTable = table;
		this.parentPage = parentPage;
		this.iqc = iqc;
		iqcLineMap = new HashMap<Lot, IqcLine>();
	}
	
	public void createContents(ManagedForm form, Composite parent) {
		this.form = form;
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.createSectionContent(parent);
	}

	protected void createSectionContent(Composite client) {
		try {
			FormToolkit toolkit = form.getToolkit();
			Composite comp = toolkit.createComposite(client, SWT.BORDER);
			comp.setLayout(new GridLayout(5, false));
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Label label = toolkit.createLabel(comp, Message.getString("inv.lotid"));
			label.setForeground(SWTResourceCache.getColor("Folder"));
			label.setFont(SWTResourceCache.getFont("Verdana"));
			txtLotId = toolkit.createText(comp, "", SWT.BORDER);
			txtLotId.setTextLimit(48);
			GridData gd = new GridData();//GridData.FILL_HORIZONTAL
			gd.heightHint = 13;
			gd.widthHint = 340;
			txtLotId.setLayoutData(gd);
			txtLotId.addKeyListener(getKeyListener());
			
			label = toolkit.createLabel(comp, "       ", SWT.NULL);
//			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			/* 创建仓库控件*/
			ADTable whTable = getADTableOfVUserWarehouse();
			ADRefTable refTable = new ADRefTable();
			refTable.setKeyField(VALUE_FIELD_NAME);
			refTable.setValueField(KEY_FIELD_NAME);
			String where = " userRrn = " + Env.getUserRrn() + " AND (isVirtual = 'N' OR isVirtual is null)";
			refTable.setWhereClause(where);
			refTable.setTableRrn(whTable.getObjectRrn());
			TableListManager tlm = new TableListManager(whTable);
			TableViewer tv = (TableViewer)tlm.createViewer(UI.getActiveShell(), toolkit);
			ADManager adManager = Framework.getService(ADManager.class);
			if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
					|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
				List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), whTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
				tv.setInput(list);
			}
			refField = new RefTableField(KEY_FIELD_NAME, tv, refTable, SWT.BORDER | SWT.READ_ONLY);
			refField.setLabel(Message.getString("inv.warehouse_id") + " *");
			refField.createContent(comp, toolkit);
			/*若iqc单中有Warehouse，则将WarehouseId值直接带出，并不可再修改*/
			if(iqc != null && iqc.getWarehouseRrn() != null){
				refField.setValue(iqc.getWarehouseRrn());
				refField.setEnabled(false);
				refField.refresh();
			}
			
			listTableManager = new CheckListTableManager(adTable, this);
			listTableManager.addStyle(SWT.CHECK);
			viewer = (CheckboxTableViewer)listTableManager.createViewer(client, form.getToolkit());
			
			viewer.addCheckStateListener(getCheckStateListener());
			viewer.addDoubleClickListener(getDoubleClickListener());
		} catch(Exception e) {
			logger.error("IqcSelectSection : createSectionContent() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}
	
	protected IDoubleClickListener getDoubleClickListener() {
		return new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
				if(ss.getFirstElement() instanceof Lot) {
					Lot lot = (Lot)ss.getFirstElement();
					BigDecimal max = lot.getQtyCurrent();
					if(iqcLineMap.get(lot) != null) {
						max = iqcLineMap.get(lot).getQtyQualified();
					}
					if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						PinFromIqcQtyDialog dialog = new PinFromIqcQtyDialog(UI.getActiveShell(),
								lot, max);
						if(dialog.open() == Dialog.OK) {
							viewer.refresh(lot);
						}
					}
				}
	    	}
	    };
	}
	
	private KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					switch (e.keyCode) {
					case SWT.CR:
						String lotId = txtLotId.getText();
						addLot(lotId);						
						break;
					}
				}
			};			
		} catch(Exception e) {
			logger.error("Error at MoLineReceiveSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	private void addLot(String lotId) {
		Lot lot = null;
		try {
			if(lotId != null && !"".equals(lotId)) {
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				//lot位置检查
				if(!lot.getPosition().equals(Lot.POSITION_IQC) && !Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())){
					UI.showError(String.format(Message.getString("inv.lot_position_isnot_correct"),
							lot.getPosition(), Lot.POSITION_IQC));
					return;
				}
				
				// 若lot在列表中则选中lot，并使Finish按钮可用，若不在列表中没有提示信息
				if(viewer.setChecked(lot, true)) {
					updateParentPage(true);
				}
			}
		} catch(Exception e) {
			logger.error("Error at IqcLineLotSelectSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}

	private ICheckStateListener getCheckStateListener() {
		return new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				boolean isChecked = event.getChecked();
				if(isChecked || hasOtherChecked()) {
					updateParentPage(true);
				} else {
					updateParentPage(false);
				}
			}
	    };
	}
	
	public void updateParentPage(boolean isChecked) {		
		parentPage.setPageComplete(isChecked);
	}
	
	private boolean hasOtherChecked() {
		Object[] os = viewer.getCheckedElements();
		if(os.length > 0) return true;
		else return false;
	}

	public void refresh() {
		try {
			boolean hasBarCode = false;
        	ADManager manager = Framework.getService(ADManager.class);
            List<ADBase> list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), null);
            if(list == null || list.size() == 0) {
            	// 如果没有Lots,并且检验单中含有非Material类型的物料，则提示不能入库
            	if(lines != null) {
            		boolean flag = false;
            		for(IqcLine iqcLine : lines) {
            			if(!Lot.LOTTYPE_MATERIAL.equals(iqcLine.getMaterial().getLotType())) {
            				flag = true;
            				break;
            			}
            		}
            		if(flag) {
            			//判断时候否含有barCode，如有表明是无纸化作业
            			for(IqcLine iqcLine : parentPage.getWizard().getContext().getIqcLines()){
                			PurchaseOrderLine poLine = new PurchaseOrderLine();
                			poLine.setObjectRrn(iqcLine.getPoLineRrn());
                			poLine =  (PurchaseOrderLine) manager.getEntity(poLine);
                			if(poLine.getBarCode()!=null && (Env.getOrgRrn() ==139420L || Env.getOrgRrn() ==41673024L || Env.getOrgRrn()== 12644730L || Env.getOrgRrn()== 63506125L )){
                				hasBarCode = true;
                				break;
                			}
            			}
            			if(!hasBarCode){
                   			this.parentPage.setErrorMessage(Message.getString("wip.no_lot_can_input_warehouse"));
                			return;
            			}
 
            		}
            	}
            }
            List<Lot> temp = new ArrayList<Lot>();
            if(invManager == null)
            	invManager = Framework.getService(INVManager.class);
            if(parentPage.getWizard().getContext().getIqcLines() != null) {
            	for(IqcLine iqcLine : parentPage.getWizard().getContext().getIqcLines()) {
            		if(Lot.LOTTYPE_MATERIAL.equals(iqcLine.getLotType())) {
            			Lot lot = invManager.getMaterialLot(Env.getOrgRrn(), iqcLine.getMaterial(), Env.getUserRrn());
            			if(lot != null) {
            				lot.setPoId(iqc.getPoId());
            				lot.setPoRrn(iqc.getPoRrn());
            				lot.setReceiptId(iqc.getReceiptId());
            				lot.setReceiptRrn(iqc.getReceiptRrn());
            				lot.setIqcId(iqc.getDocId());
            				lot.setIqcRrn(iqc.getObjectRrn());
            				lot.setIqcLineRrn(iqcLine.getObjectRrn());
            				lot.setQtyCurrent(iqcLine.getQtyQualified());
            				list.add(lot);
            				this.iqcLineMap.put(lot, iqcLine);
            				temp.add(lot);
            			}
            		}else{
            			//对于batch、serial类型，如果barCode有值那么跟material的处理方式一样
            			if(hasBarCode &&( Env.getOrgRrn() ==139420L || Env.getOrgRrn() ==41673024L  || Env.getOrgRrn()== 12644730L  || Env.getOrgRrn()== 63506125L  )){
                			ADManager adManager = Framework.getService(ADManager.class);
                			PurchaseOrderLine poLine = new PurchaseOrderLine();
                			poLine.setObjectRrn(iqcLine.getPoLineRrn());
                			poLine =  (PurchaseOrderLine) adManager.getEntity(poLine);
                			if(poLine.getBarCode()!=null){
                    			if(Lot.LOTTYPE_SERIAL.equals(iqcLine.getLotType())){
                    				String whereClause ="";
                    				String[] spiltBarCode = poLine.getBarCode().split("\\;");
                    				if(spiltBarCode.length==1){
                    					StringBuffer whereLotId = new StringBuffer();
                    					whereLotId.append("'");
                    					whereLotId.append(poLine.getBarCode());
                    					whereLotId.append("'");
                    					whereClause= whereLotId.toString();
                    				}else{
                    					StringBuffer whereLotId = new StringBuffer();
                    					for(String barCode : spiltBarCode){
                    						whereLotId.append("'");
                        					whereLotId.append(barCode);
                        					whereLotId.append("',");
                        					whereClause= whereLotId.substring(0, whereLotId.length()-1);
                    					}
                    				}
                    				List<Lot> lots = adManager.getEntityList(Env.getOrgRrn(), Lot.class,Integer.MAX_VALUE,"lotId in ("+whereClause+") AND inRrn is null","");
                    				for(Lot lot :lots){
                          				lot.setPoId(iqc.getPoId());
                        				lot.setPoRrn(iqc.getPoRrn());
                        				lot.setReceiptId(iqc.getReceiptId());
                        				lot.setReceiptRrn(iqc.getReceiptRrn());
                        				lot.setIqcId(iqc.getDocId());
                        				lot.setIqcRrn(iqc.getObjectRrn());
                        				lot.setIqcLineRrn(iqcLine.getObjectRrn());
                        				lot.setQtyCurrent(BigDecimal.ONE);
                        				list.add(lot);
                        				this.iqcLineMap.put(lot, iqcLine);
                        				temp.add(lot);
                    				}
                    			}else{
                    				List<Lot> lots = adManager.getEntityList(Env.getOrgRrn(), Lot.class,Integer.MAX_VALUE,"lotId = '"+poLine.getBarCode()+"'","");
                        			Lot lot = lots.get(0);
                      				lot.setPoId(iqc.getPoId());
                    				lot.setPoRrn(iqc.getPoRrn());
                    				lot.setReceiptId(iqc.getReceiptId());
                    				lot.setReceiptRrn(iqc.getReceiptRrn());
                    				lot.setIqcId(iqc.getDocId());
                    				lot.setIqcRrn(iqc.getObjectRrn());
                    				lot.setIqcLineRrn(iqcLine.getObjectRrn());
                    				lot.setQtyCurrent(iqcLine.getQtyQualified());
                    				list.add(lot);
                    				this.iqcLineMap.put(lot, iqcLine);
                    				temp.add(lot);
                    			}
                			}
            			}

            		}
            	}
            }
            viewer.setInput(list);
            listTableManager.updateView(viewer);
            for(Lot lot : temp) {
            	if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())){
            		viewer.setChecked(lot, true);
            	}
            	
            }
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	public void initContent() {
		this.iqcLineMap = new HashMap<Lot, IqcLine>();
	}

	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
		if(parentIqc != null && parentIqc.getObjectRrn() != null) {
			whereClause.append(" iqcRrn = " + parentIqc.getObjectRrn());
		}
		if(lines != null && lines.size() > 0) {
			whereClause.append(" AND iqcLineRrn IN (");
			for(IqcLine line : lines) {
				whereClause.append(line.getObjectRrn() + ", ");
			}
			whereClause.replace(whereClause.length() - 2, whereClause.length() - 1, ") ");
		}
		whereClause.append(" AND position = '" + Lot.POSITION_IQC + "' ");
		return whereClause.toString();
	}

	public void setParentIqc(Iqc parentIqc) {
		this.parentIqc = parentIqc;
	}
	
	public void setIqcLines(List<IqcLine> lines) {
		this.lines = lines;
	}
	
	public List<Lot> getSelectedLots() {
		List<Lot> lines = new ArrayList<Lot>();
		Object[] os = viewer.getCheckedElements();
		if(os.length != 0) {
			for(Object o : os) {
				Lot lot = (Lot)o;
				if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					if(iqcLineMap.get(lot) != null) {
						//使iqcLineMap中只剩下未选中的iqcLine列表
						iqcLineMap.remove(lot);
					}
				}else{
					if(Env.getOrgRrn() ==139420L || Env.getOrgRrn() ==41673024L || Env.getOrgRrn()== 12644730L){
						//reservedField10不为空代表有barCode
						if(lot.getReverseField10()!=null){
							if(iqcLineMap.get(lot) != null) {
								//使iqcLineMap中只剩下未选中的iqcLine列表
								iqcLineMap.remove(lot);
							}
						}
					}
				}
				lines.add(lot);					
			}
		}
		return lines;
	}
	
	protected List<IqcLine> getFinallyIqcLines() {
		List<IqcLine> fils = this.parentPage.getWizard().getContext().getIqcLines();
		fils.removeAll(iqcLineMap.values());
		return fils;
	}
	
	private ADTable getADTableOfVUserWarehouse() {
		ADTable table = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			table = entityManager.getADTable(0L, TableName_VUser_Warehouse);
		} catch(Exception e) {
			logger.error("IqcCreateContext : getTable_IqcLine()", e);
		}
		return table;
	}
	
	/* 若输入了入库仓库，则新建一个入库单，并将入库仓库数据赋给入库单*/
	protected MovementIn getInWarehouse() {
		Object obj = refField.getValue();
		if(obj instanceof String) {
			String longValue = (String)obj;
			if(!"".equals(longValue.trim())) {
				MovementIn in = new MovementIn();
				in.setOrgRrn(iqc.getOrgRrn());
				in.setDocStatus(MovementIn.STATUS_DRAFTED);
				in.setWarehouseRrn(Long.parseLong(longValue));
				return in;
			}
		} else {
			this.parentPage.setErrorMessage(String.format(Message.getString("common.ismandatory"), Message.getString("inv.warehouse_id")));
		}
		return null;
	}

}
