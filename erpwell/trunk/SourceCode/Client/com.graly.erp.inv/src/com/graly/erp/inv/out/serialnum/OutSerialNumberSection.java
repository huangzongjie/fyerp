package com.graly.erp.inv.out.serialnum;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.LotPrintDialog;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementLineOutSerial;
import com.graly.erp.inv.model.MovementOut;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class OutSerialNumberSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(OutSerialNumberSection.class);
	public static final String ORDERBY_OUT_SERIAL_ID = " outSerialId ";
	
	protected List<MovementLineOutSerial> outSerialNumbers;
	protected MovementLineOutSerial selectedOutSerial;
	protected MovementOut out;
	protected MovementLine line;
	protected ToolItem itemBarCode;

	public OutSerialNumberSection(ADTable adTable, LotDialog parentDialog,
			MovementOut out, MovementLine line) {
		super(adTable, parentDialog);
		this.out = out;
		this.line = line;
	}
	
	protected void setSectionTitle() {
		section.setText(String.format(Message.getString("common.list"),
				I18nUtil.getI18nMessage(adTable, "label")));
	}

	@Override
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolPrintBarCode(tBar);
		section.setTextClient(tBar);
	}
	
	@Override
	protected void createToolItemSave(ToolBar bar) {
		itemSave = new ToolItem(bar, SWT.PUSH);
		itemSave.setText(Message.getString("inv.gen_serial"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}

	protected void createToolPrintBarCode(ToolBar tBar) {
		itemBarCode = new ToolItem(tBar, SWT.PUSH);
		itemBarCode.setText(Message.getString("common.print"));
		itemBarCode.setImage(SWTResourceCache.getImage("print"));
		itemBarCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printAdapter();
			}
		});
	}
	
	@Override
	protected void initTableContent() {
		try {
			outSerialNumbers = getExsitedOutSerials();
			if(outSerialNumbers != null && outSerialNumbers.size() > 0) {
				setEnabled(false);
			} else {
				setEnabled(true);
			}
			refresh();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected List<MovementLineOutSerial> getExsitedOutSerials() throws Exception {
		ADManager adManager = Framework.getService(ADManager.class);
    	List<MovementLineOutSerial> list = adManager.getEntityList(Env.getOrgRrn(), MovementLineOutSerial.class, 
        		Env.getMaxResult(), getWhereClause(), null);
    	return list;
	}
	
	@Override
	protected void saveAdapter() {
		try {
			if(out != null && line != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				outSerialNumbers = invManager.generateMovementLineOutSerials(Env.getOrgRrn(), out, line, Env.getUserRrn());
				refresh();
				setEnabled(false);
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	// ½«MovementLineLot¸ÄÎªLot
	protected void printAdapter() {
		try {
			outSerialNumbers = (List<MovementLineOutSerial>)viewer.getInput();
			if(outSerialNumbers != null && outSerialNumbers.size() != 0){
				List<Lot> lots = new ArrayList<Lot>();
				for(MovementLineOutSerial outSerialNum : outSerialNumbers) {
					Lot lot = new Lot();
					lot.setObjectRrn(outSerialNum.getLotRrn());
					lot.setLotId(outSerialNum.getOutSerialId());
					lots.add(lot);					
					if(selectedOutSerial != null) {
						if(selectedOutSerial.getLotRrn().equals(lot.getObjectRrn())) {
							this.selectLot = lot;
						}
					}
				}
				LotPrintDialog printDialog = new LotPrintDialog(lots, this.selectLot);
				printDialog.open();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected List<?> getInput() {
		return getOutSerialNumbers();
	}
	
	protected String getWhereClause() {
		StringBuffer sb = new StringBuffer("");
		sb.append(" movementRrn = ");
		sb.append(out.getObjectRrn());
		sb.append(" AND movementLineRrn = ");
		sb.append(line.getObjectRrn());
		sb.append(" ");
		return sb.toString();
	}
	
	protected String getOrderByClause() {
		return ORDERBY_OUT_SERIAL_ID;
	}
	
	protected void setMovementLineSelect(Object obj) {
		if (obj instanceof MovementLineLot) {
			selectedOutSerial = (MovementLineOutSerial) obj;
		} else {
			selectedOutSerial = null;
		}
	}

	public List<MovementLineOutSerial> getOutSerialNumbers() {
		return outSerialNumbers;
	}
	
	public void setOutSerialNumbers(List<MovementLineOutSerial> outSerialNumbers) {
		this.outSerialNumbers = outSerialNumbers;
	}
	
	protected void setEnabled(boolean enabled) {
		itemSave.setEnabled(enabled);
		itemBarCode.setEnabled(enabled ? false : true);
	}
}
