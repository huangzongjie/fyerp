package com.graly.erp.inv.barcode;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

/*
 * 批次录入Section
 */
public class InputLotSection extends IqcLotSection {
	private static final Logger logger = Logger.getLogger(InputLotSection.class);
	private InputLotDialog ild;
	
	protected int optional;

	protected List<String> errorLots = new ArrayList<String>();

	public InputLotSection(ADBase parent, ADBase child, ADTable adTable,
			String lotType, InputLotDialog ild) {
		super(parent, child, adTable, lotType, ild);
		this.ild = ild;
	}



	// 只进行保存和打印
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
		Composite comp = toolkit.createComposite(client, SWT.BORDER);
		comp.setLayout(new GridLayout(2, false));
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
		txtLotId.setFocus();
		
		Composite radioComp = toolkit.createComposite(comp, SWT.NONE);
		radioComp.setLayout(new GridLayout(8, false));
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 2;
		radioComp.setLayoutData(gridData2);
		Button b1 = toolkit.createButton(radioComp, "仅输入", SWT.RADIO);
		b1.setSelection(true);
		b1.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				optional = 0;
			}
		});
		Button b2 = toolkit.createButton(radioComp, "连续序号", SWT.RADIO);
		b2.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				optional = 1;
			}
		});
//		Button b3 = toolkit.createButton(radioComp, "选取", SWT.RADIO);
//		b3.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				optional = 2;
//			}
//		});
	}
	@Override
	protected void addLot() {
		String lotId = txtLotId.getText();
		errorLots.clear();
		try {
			switch (optional) {
			case 0://仅输入
				addLot(lotId);
				break;
			case 1://连续序列
				String input = UI.showInput("序列设置","请输入最后一个批次的末尾序号");
				if(input == null || input.trim().length() == 0){
					UI.showError(Message.getString("common.input_error"));
				}
				
				try {
					int nums = Integer.parseInt(input);
					String beginSerial = lotId.substring(lotId.length()-input.length());
					String lotIdPrefix = lotId.substring(0, lotId.length()-input.length());
					int startNum = Integer.parseInt(beginSerial);
					for(;startNum <= nums;startNum++){
						StringBuffer sb = new StringBuffer(lotIdPrefix);
						int len1 = String.valueOf(startNum).trim().length();
						int len2 = input.length();
						if(len1 < len2){
							for(int i=0;i<len2-len1;i++){
								sb.append(0);
							}
						}
						sb.append(startNum);
						lotId = sb.toString();
						addLot(lotId);
					}
					StringBuffer sb = new StringBuffer();
					if(errorLots != null && errorLots.size() > 0){
						int i = 0;
						for(String str : errorLots){
							sb.append(str);
							if(errorLots.size() > 1){
								if(i < errorLots.size()-1){
									sb.append("|");
								}
							}
							if(++i % 5 ==0 ){
								sb.append("\r\n");
							}
						}
						UI.showError("  "+Message.getString("inv.lotnotexist")+":\r\n"+sb.substring(0, sb.length()));
					}
					} catch (NumberFormatException e) {
						UI.showError(Message.getString("common.input_error"));
					}
				break;
			case 2://手动选取
				break;
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
		} 
	}
	
	protected void addLot(String lotId) {
		try {			
			if(lotId != null && !"".equals(lotId)) {		
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				if(validLot(lot)) {
					if(getLots().contains(lot)) {
						if(checkViewer != null) {
							checkViewer.setChecked(lot, true);
						}
					} else {
						getLots().add(lot);					
					}
					refresh();
					setDoOprationsTrue();
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at InputLotSection ：addLot() ", e);
			if(e instanceof ClientException && "inv.lotnotexist".equals(((ClientException)e).getErrorCode())){
				errorLots.add(lotId);
			}
		} finally {
			txtLotId.selectAll();
		}
	}
	
	protected void initTableContent() {
		
	}
	
	protected void saveAdapter() {
		try {
			if(validate()) {
				INVManager invManager = Framework.getService(INVManager.class);
				lots = invManager.attachLotsToIqc(getLots(), iqc, iqcLine, Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				ild.setLots(lots);
				setEnabled(false);
				lotManager.setCanEdit(false);
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected boolean validate() {
		return true;
	}
	
	@Override
	protected boolean validLot(Lot lot) {
		if(getLots().contains(lot)){
			UI.showError(String.format(Message
					.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		return true;
	}
	
	protected void setEnabled(boolean enabled) {
		itemSave.setEnabled(enabled);
		this.itemPrint.setEnabled(itemSave.getEnabled() ? false : true);
	}
}
