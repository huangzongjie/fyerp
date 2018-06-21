package com.graly.erp.wip.virtualhouse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopLineLot;
import com.graly.erp.inv.model.MovementWorkShopVirtualHouse;
import com.graly.erp.inv.model.Warehouse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.custom.XSearchComposite;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class VirtualHouseLotSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(VirtualHouseLotSection.class);

	protected MovementWorkShopLine workShopLine;//outLine
	protected MovementWorkShopVirtualHouse virtualHouse;//out
	
	private List<MovementWorkShopLineLot> lineLots;
	protected boolean isView = false;
	protected List<Lot> lots;

	protected int optional;
	protected List<String> errorLots = new ArrayList<String>();
	protected MovementWorkShopLine selectedOutLine;
	protected Object parentObject;	
	
	protected List<MovementWorkShopLine> lines;
	
	public VirtualHouseLotSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}
	
	public VirtualHouseLotSection(ADBase parent, ADBase child, ADTable adTable,VirtualHouseLineLotDialog olld, boolean isView) {
		//super(adTable, olld);
		super(adTable);
		this.virtualHouse =(MovementWorkShopVirtualHouse)parent;
		this.workShopLine = (MovementWorkShopLine)workShopLine;
		this.isView = isView;
	}
	
	public VirtualHouseLotSection(ADTable adTable, MovementWorkShopVirtualHouse out,
			MovementWorkShopLine outLine, List<MovementWorkShopLine> lines,
			VirtualHouseLineLotDialog olld, boolean isView) {
		this(out, outLine, adTable, olld, isView);
		this.lines = lines;
	}
	
    public void createToolBar(Section section) {
        ToolBar tBar = new ToolBar(section, SWT. FLAT | SWT.HORIZONTAL );

         new ToolItem(tBar, SWT. SEPARATOR);
//        createToolItemSave(tBar);
         new ToolItem(tBar, SWT. SEPARATOR);
//        createToolItemDelete(tBar);
        section.setTextClient(tBar);
  }
   
	@Override
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
//		Composite comp = toolkit.createComposite(client, SWT.BORDER);
//		comp.setLayout(new GridLayout(3, false));
//		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//		comp.setLayoutData(gridData);
//		Label label = toolkit.createLabel(comp, Message.getString("inv.lotid"));
//		label.setForeground(SWTResourceCache.getColor("Folder"));
//		label.setFont(SWTResourceCache.getFont("Verdana"));
//		txtLotId = toolkit.createText(comp, "", SWT.BORDER);
//		txtLotId.setTextLimit(48);
//		GridData gd = new GridData();//GridData.FILL_HORIZONTAL
//		gd.heightHint = 13;
//		gd.widthHint = 340;
//		txtLotId.setLayoutData(gd);
//		txtLotId.addKeyListener(getKeyListener());
//		txtLotId.setFocus();
//		
//		Composite radioComp = toolkit.createComposite(comp, SWT.NONE);
//		radioComp.setLayout(new GridLayout(8, false));
//		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
//		gridData2.horizontalSpan = 2;
//		radioComp.setLayoutData(gridData2);
//		Button b1 = toolkit.createButton(radioComp, "仅输入", SWT.RADIO);
//		b1.setSelection(true);
//		b1.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				optional = 0;
//			}
//		});
//		Button b2 = toolkit.createButton(radioComp, "连续序号", SWT.RADIO);
//		b2.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				optional = 1;
//			}
//		});
 
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new TableListManager(adTable);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
	}
	
   
	
	private List<MovementWorkShopLineLot> getLineLotsByMaterial(Long materialRrn) {
		List<MovementWorkShopLineLot> lLots = new ArrayList<MovementWorkShopLineLot>();
		for(MovementWorkShopLineLot lineLot : getLineLots()) {
			if(lineLot.getMaterialRrn().equals(materialRrn))
				lLots.add(lineLot);
		}
		return lLots;
	}

	public void setLineLots(List<MovementWorkShopLineLot> lineLots) {
		this.lineLots = lineLots;
	}  
	protected boolean contains(MovementWorkShopLineLot lineLot) {
		if(lineLot == null) return true;
		for(MovementWorkShopLineLot temp : this.getLineLots()) {
			if(temp.getLotId().equals(lineLot.getLotId()))
				return true;
		}
		return false;
	}
	 
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getLineLots());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	protected List<MovementWorkShopLineLot> getLineLots() {
		if(lineLots == null) {
			lineLots = new ArrayList<MovementWorkShopLineLot>();
			return lineLots;
		}
		return lineLots;
	}
	
	protected void initTableContent() {
		List<ADBase> list = null;
		try {
        	ADManager manager = Framework.getService(ADManager.class);
            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), getOrderByClause());
            List<MovementWorkShopLineLot> l = new ArrayList<MovementWorkShopLineLot>();
            for(ADBase ab : list) {
            	MovementWorkShopLineLot lineLot = (MovementWorkShopLineLot)ab;
            	l.add(lineLot);
            }
            setLineLots(l);
            refresh();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
//		if(this.outLine != null) {
//			whereClause.append(" movementLineRrn = '");
//			whereClause.append(this.outLine.getObjectRrn());
//			whereClause.append("' ");
//		} else  
		if(lines != null){
			whereClause.append(" movementLineRrn IN (");
			for(MovementWorkShopLine line : lines) {
				whereClause.append("'");
				whereClause.append(line.getObjectRrn());
				whereClause.append("', ");
			}
			int length = whereClause.length();
			whereClause = whereClause.delete(length - 2, length);
			whereClause.append(")");			
			return whereClause.toString();
		}
		return " 1 <> 1 ";
	}
}
