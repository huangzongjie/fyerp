package com.graly.erp.wip.workcenter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.LotPrintDialog;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.seelotinfo.ComponentDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class SeeLotSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(SeeLotSection.class);
	
	private ToolItem itemPreview;
	private ToolItem itemComponent;
	protected ManufactureOrderLine selectMoLine;
	WipManager wipManager;

	public SeeLotSection(ADTable table, ManufactureOrderLine selectMoLine) {
		super(table);
		this.selectMoLine = selectMoLine;
	}

	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemComponent(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemComponent(ToolBar tBar) {
		itemComponent = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SEARCHBYLOT_COMPONENT);
		itemComponent.setText(Message.getString("wip.seelot_component"));
		itemComponent.setImage(SWTResourceCache.getImage("component"));
		itemComponent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				componentAdapter();
			}
		});
	}

	private void createToolItemPreview(ToolBar tBar) {
		itemPreview = new ToolItem(tBar, SWT.PUSH);
		itemPreview.setText(Message.getString("common.print"));
		itemPreview.setImage(SWTResourceCache.getImage("print"));
		itemPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewAdapter();
			}
		});
	}
	
	protected void componentAdapter() {
		if (selectLot == null) return;
		if(Lot.LOTTYPE_MATERIAL.equals(selectLot.getLotType())) {
			UI.showInfo(Message.getString("wip.can_not_trace_material"));
			return;
		}
		ComponentDialog componentDialog = new ComponentDialog(UI.getActiveShell(), form, selectLot);
		componentDialog.open();
	}

//	@Override
//	protected void initTableContent() {
//		if(!Lot.LOTTYPE_MATERIAL.equals(this.selectMoLine.getLotType())) {
//			super.initTableContent();			
//		}
//		else {
//			try {
//				List<Lot> l = new ArrayList<Lot>();
//				INVManager invManager = Framework.getService(INVManager.class);
//				l.add(invManager.getMaterialLot(Env.getOrgRrn(),
//						this.selectMoLine.getMaterial(), Env.getUserRrn()));
//				setLots(l);
//	            refresh();				
//			} catch(Exception e) {
//				ExceptionHandlerManager.asyncHandleException(e);
//			}
//		}
//	}

	protected String getWhereClause() {
		return " moLineRrn = '" + selectMoLine.getObjectRrn() + "' ";
	}
	
	protected void initTableContent() {
		String lotType = selectMoLine.getMaterial().getLotType();
		List<ADBase> list = null;
		try {
			if(Lot.LOTTYPE_MATERIAL.equals(lotType)) {
				INVManager invManager = Framework.getService(INVManager.class);
				List<Lot> l = new ArrayList<Lot>();
				Lot lot = invManager.getMaterialLotForMoLine(Env.getOrgRrn(),
						selectMoLine.getMaterial(), Env.getUserRrn(), selectMoLine);
				l.add(lot);
				setLots(l);
			}else{
				ADManager manager = Framework.getService(ADManager.class);
	            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
	            		Env.getMaxResult(), getWhereClause(), getOrderByClause());
	            List<Lot> l = new ArrayList<Lot>();
	            for(ADBase ab : list) {
	            	Lot lot = (Lot)ab;
	            	l.add(lot);
	            }
	            setLots(l);
			}
        	
            refresh();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	logger.error(e.getMessage(), e);
        }
	}

	protected String getOrderByClause() {
		return " lotId ";
	}
	
	
	protected void previewAdapter() {
		try {
			lots = (List<Lot>) viewer.getInput();
			if(lots != null && lots.size() != 0){
				LotPrintDialog printDialog = new LotPrintDialog(lots, this.selectLot);
				printDialog.open();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
}
