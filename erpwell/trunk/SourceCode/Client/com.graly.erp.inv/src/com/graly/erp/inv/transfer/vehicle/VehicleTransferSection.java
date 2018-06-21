package com.graly.erp.inv.transfer.vehicle;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.transfer.LotTrsDialog;
import com.graly.erp.inv.transfer.LotTrsSection;
import com.graly.erp.inv.transfer.TransferSection;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class VehicleTransferSection extends TransferSection {
	private static final Logger logger = Logger.getLogger(VehicleTransferSection.class);
	
	protected String inputValue;
	protected VehicleTransferInputDialog shipInputDialog;
	protected VehicleTransferInputDialog backShipInputDialog;
	protected ToolItem itemTrsBack;

	public VehicleTransferSection(EntityTableManager tableManager) {
		super(tableManager);
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemTrs(tBar);
		createToolItemTrsBack(tBar);//退料
		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	

	protected void createToolItemTrs(ToolBar tBar) {
		itemTrs = new ToolItem(tBar, SWT.PUSH);
		itemTrs.setText("领料");
		itemTrs.setImage(SWTResourceCache.getImage("ship"));
		itemTrs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				trsAdapter();
			}
		});
	}
	
	protected void createToolItemTrsBack(ToolBar tBar) {
		itemTrsBack = new ToolItem(tBar, SWT.PUSH);
		itemTrsBack.setText("退料");
		itemTrsBack.setImage(SWTResourceCache.getImage("backship"));
		itemTrsBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				trsBackAdapter();
			}
		});
	}
	
	protected void trsBackAdapter() {
		if (backShipInputDialog != null) {
			backShipInputDialog.setVisible(true);
		} else {
			backShipInputDialog =  new VehicleTransferInputDialog(UI.getActiveShell(), tableManager, this, VehicleTransferInputDialog.BACKSHIP_TYPE);
			backShipInputDialog.open();
		}
	}

	@Override
	protected void trsAdapter() {
		if (shipInputDialog != null) {
			shipInputDialog.setVisible(true);
		} else {
			shipInputDialog =  new VehicleTransferInputDialog(UI.getActiveShell(), tableManager, this, VehicleTransferInputDialog.SHIP_TYPE);
			shipInputDialog.open();
		}
	}
	
	protected void ship(){
		VehicleLotTrsDialog olbd = new VehicleLotTrsDialog(UI.getActiveShell(), VehicleLotTrsSection.SHIP_TYPE, null, getInputValue());
		if(olbd.open() == Dialog.CANCEL) {
			MovementTransfer newTrs = ((VehicleLotTrsSection)olbd.getLotMasterSection()).getTransfer();
			if(newTrs != null && newTrs.getObjectRrn() != null) {
				setSelectedMt(newTrs);
				if(getSelectedMt() != null && getSelectedMt().getObjectRrn() != null)
					refreshAdd(getSelectedMt());
				editAdapter();
			}
		}
	}
	
	protected void backShip(){
		VehicleLotTrsDialog olbd = new VehicleLotTrsDialog(UI.getActiveShell(), VehicleLotTrsSection.BACKSHIP_TYPE, getInputValue(), null);
		if(olbd.open() == Dialog.CANCEL) {
			MovementTransfer newTrs = ((VehicleLotTrsSection)olbd.getLotMasterSection()).getTransfer();
			if(newTrs != null && newTrs.getObjectRrn() != null) {
				setSelectedMt(newTrs);
				if(getSelectedMt() != null && getSelectedMt().getObjectRrn() != null)
					refreshAdd(getSelectedMt());
				editAdapter();
			}
		}
	}


	public String getInputValue() {
		return inputValue;
	}


	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}
}

class VehicleTransferInputDialog extends EntityQueryDialog{
	private static final Logger logger = Logger.getLogger(VehicleTransferInputDialog.class);
	
	public static Color COLOR_GRALY = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	public static Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	public static Color COLOR_BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	
	public static final int SHIP_TYPE = 1;
	public static final int BACKSHIP_TYPE = 2;
	
	protected Text vehicleId;
	protected Text vehicleName;
	protected Label lblVehicleName;
	protected Label lblVehicleId;
	
	protected int transType = 1;

	public VehicleTransferInputDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh, int transType) {
		super(parent, tableManager, refresh);
		this.transType = transType;
	}

	public VehicleTransferInputDialog(Shell parent) {
		super(parent);
	}
	
		@Override
		protected Control createDialogArea(Composite parent) {
			try {
				FormToolkit toolkit = new FormToolkit(Display.getCurrent());
				setTitleImage(SWTResourceCache.getImage("vehicle"));
				setTitle("车辆信息录入");
				setMessage("请输入车辆信息");
				Composite composite = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout();
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				layout.verticalSpacing = 0;
				layout.horizontalSpacing = 0;
				composite.setLayout(layout);
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));
				composite.setFont(parent.getFont());
				// Build the separator line
				Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
						| SWT.SEPARATOR);
				titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				Composite client = toolkit.createComposite(composite);
				GridLayout layout2 = new GridLayout(2, false);
				layout2.marginTop = 20;
				layout2.marginLeft = 10;
				layout2.marginRight = 10;
				layout2.marginHeight = 0;
				layout2.marginWidth = 0;
				layout2.verticalSpacing = 10;
				layout2.horizontalSpacing = 10;
				client.setLayout(layout2);
				GridData gridData = new GridData(GridData.FILL_BOTH);
				gridData.grabExcessHorizontalSpace = true;
				client.setLayoutData(gridData);
				
				
				lblVehicleName = toolkit.createLabel(client, "车辆名称");
				GridData gd = new GridData();
				lblVehicleName.setLayoutData(gd);
				
				vehicleName = toolkit.createText(client, "");
				GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
				vehicleName.setLayoutData(gd2);
				final INVManager invManager = Framework.getService(INVManager.class);
				vehicleName.addKeyListener(new KeyAdapter(){

					@Override
					public void keyPressed(KeyEvent e) {
						vehicleName.setForeground(SWTResourceCache.getColor("Black"));
						switch(e.keyCode){
						case SWT.CR :
						case SWT.TRAVERSE_RETURN :
							String name = vehicleName.getText();
							try {
									String id = invManager.getVehicleIdByName(name);
									if(id == null){
										vehicleName.setForeground(SWTResourceCache.getColor("Red"));
									}else{
										vehicleId.setText(id);
									}
								} catch (ClientException e1) {
									logger.error(e1);
								}
							break;
						}
					}

					@Override
					public void keyReleased(KeyEvent e) {
					}
					});
				
				lblVehicleId = toolkit.createLabel(client, "车辆ID");
				lblVehicleId.setLayoutData(gd);
				vehicleId = toolkit.createText(client, "");
				vehicleId.setLayoutData(gd2);
				vehicleId.addKeyListener(new KeyAdapter(){

					@Override
					public void keyPressed(KeyEvent e) {
						vehicleId.setForeground(SWTResourceCache.getColor("Black"));
						switch(e.keyCode){
						case SWT.CR :
						case SWT.TRAVERSE_RETURN :
							String id = vehicleId.getText();
							try {
									String name = invManager.getVehicleNameById(id);
									if(name == null){
										vehicleId.setForeground(SWTResourceCache.getColor("Red"));
									}else{
										vehicleName.setText(name);
									}
								} catch (ClientException e1) {
									logger.error(e1);
								}
							break;
						}
					}

					@Override
					public void keyReleased(KeyEvent e) {
					}
					});
				
//				Button radio1 = toolkit.createButton(client, "输入", SWT.RADIO);
//				Button radio2 = toolkit.createButton(client, "刷卡", SWT.RADIO);
//				
//				radio1.addSelectionListener(new SelectionListener(){
//
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {
//					}
//
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						vehicalNameEndable();
//					}
//					
//				});
//				
//				radio2.addSelectionListener(new SelectionListener(){
//
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {
//					}
//
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						vehicalIdEnable();
//					}
//					
//				});
//				
//				radio2.setSelection(true);
				vehicalIdEnable();
				
				return composite;
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
			return parent;
		}
		
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
	        createButton(parent, IDialogConstants.OK_ID,
	        		Message.getString("common.ok"), false);
	        createButton(parent, IDialogConstants.CANCEL_ID,
	        		Message.getString("common.cancel"), false);
	    }
		
	@Override
	protected void createAdvanceButtonBar(Composite parent) {
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}
	
	@Override
    protected void okPressed() {
		if(!validateInput()) return;
		this.setVisible(false);
		getInputValue();
		((VehicleTransferSection)iRefresh).setInputValue(sb.toString());
		switch (this.transType) {
		case 1:
			((VehicleTransferSection)iRefresh).ship();
			break;
		case 2:
			((VehicleTransferSection)iRefresh).backShip();
			break;
		}
    }

	private void getInputValue() {
		String name = vehicleName.getText();
		sb = new StringBuffer();
		sb.append(name);
	}

	protected boolean validateInput() {
		return true;
	}

	protected void vehicalIdEnable() {
		vehicleId.setEnabled(true);
		vehicleId.setBackground(COLOR_WHITE);
		vehicleId.setForeground(COLOR_BLACK);
		lblVehicleId.setForeground(COLOR_BLACK);
		vehicleName.setEnabled(false);
		vehicleName.setBackground(COLOR_GRALY);
		vehicleName.setForeground(COLOR_BLACK);
		lblVehicleName.setForeground(COLOR_GRALY);
		vehicleId.setFocus();
	}

	protected void vehicalNameEndable() {
		vehicleName.setEnabled(true);
		vehicleName.setBackground(COLOR_WHITE);
		vehicleName.setForeground(COLOR_BLACK);
		lblVehicleName.setForeground(COLOR_BLACK);
		vehicleId.setEnabled(false);
		vehicleId.setBackground(COLOR_GRALY);
		vehicleId.setForeground(COLOR_BLACK);
		lblVehicleId.setForeground(COLOR_GRALY);
		vehicleName.setFocus();
	}
}