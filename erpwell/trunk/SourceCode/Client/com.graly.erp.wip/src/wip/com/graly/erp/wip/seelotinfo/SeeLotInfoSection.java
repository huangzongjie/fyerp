package com.graly.erp.wip.seelotinfo;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.BrowserDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.SingleLotPrintDialog;
import com.graly.erp.inv.model.LotStorage;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADUser;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class SeeLotInfoSection extends EntitySection {
	private static final Logger logger = Logger.getLogger(SeeLotInfoSection.class);
	public static final String ADTABLE_LOTSTORAGE = "INVLotStorage";
	public static final String KEY_SEARCHBYLOT_SOID="WIP.SeeLotInfo.Soid";
	private Text text;
	private ToolItem itemComponent;
	private ToolItem itemUsageInfo;
	private ToolItem itemSoIdInfo;
	private ToolItem itemPrint;
	protected Lot lot = null;
	
	private TableListManager tableManager;
	private StructuredViewer viewer;
	private ADTable adTable_lotStorage;
	private List<LotStorage> input;

	public SeeLotInfoSection() {
		super();
	}

	public SeeLotInfoSection(ADTable table) {
		super(table);
	}

	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		if(getDetailForms().get(0).getFields().get("dateIn") != null) {
			getDetailForms().get(0).getFields().get("dateIn").setEnabled(false);			
		}
		if(getDetailForms().get(0).getFields().get("dateOut") != null) {
			getDetailForms().get(0).getFields().get("dateOut").setEnabled(false);			
		}
		if(getDetailForms().get(0).getFields().get("dateProduct") != null) {
			getDetailForms().get(0).getFields().get("dateProduct").setEnabled(false);			
		}
		if(getDetailForms().get(0).getFields().get("workCenterRrn") != null) {
			getDetailForms().get(0).getFields().get("workCenterRrn").setEnabled(false);			
		}
		if(getDetailForms().get(0).getFields().get("isUsed") != null) {
			getDetailForms().get(0).getFields().get("isUsed").setEnabled(false);			
		}
		createTableViewerContent(form, client);
	}

	protected void createSectionTitle(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(3, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, Message.getString("inv.lotid"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		text = toolkit.createText(top, "", SWT.BORDER);
		GridData gLabel = new GridData();
		gLabel.horizontalAlignment = GridData.FILL;
		gLabel.grabExcessHorizontalSpace = true;

		GridData gText = new GridData();
		gText.widthHint = 200;
		text.setLayoutData(gText);
		text.setTextLimit(32);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				Text tLotId = ((Text) event.widget);
				tLotId.setForeground(SWTResourceCache.getColor("Black"));
				switch (event.keyCode) {
				case SWT.CR:
					String lotId = tLotId.getText();
					lot = searchLot(lotId);
					tLotId.selectAll();
					if (lot == null) {
						tLotId.setForeground(SWTResourceCache.getColor("Red"));
						initAdObject();
						break;
					} else {
						itemSoIdInfo.setEnabled(true);
						updateLotContent(lot);
						refresh();
					}
					break;
				}
			}

			public Lot searchLot(String lotId) {
				try {
					INVManager invManager = Framework.getService(INVManager.class);
					return invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				} catch (Exception e) {
					logger.error("SearchByLotSection searchLot(): Lot isn' t exsited!");
					ExceptionHandlerManager.asyncHandleException(e);
					return null;
				}
			}
		});

		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Text tLotId = ((Text) e.widget);
				tLotId.setText(tLotId.getText());
				tLotId.selectAll();
			}
		});
	}
	
	protected void createTableViewerContent(IManagedForm form, Composite parent) {
		try {
			if(this.adTable_lotStorage == null) {
				ADManager adManager = Framework.getService(ADManager.class);
				adTable_lotStorage = adManager.getADTable(0L, ADTABLE_LOTSTORAGE);
			}
			tableManager = new TableListManager(adTable_lotStorage);
			viewer = tableManager.createViewer(parent, form.getToolkit(), 80);
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	public void initAdObject() {
		Lot lot = new Lot();
		lot.setOrgRrn(Env.getOrgRrn());
		setAdObject(lot);
		input = null;
		refresh();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemComponent(tBar);
		// 使用信息暂不显示
//		createToolItemUsageInfo(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSoId(tBar);
		section.setTextClient(tBar);
	}

//	private void createToolItemUsageInfo(ToolBar tBar) {
//		itemUsageInfo = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SEARCHBYLOT_LOTUSAGE);
//		itemUsageInfo.setText(Message.getString("wip.seelot_usageinfo"));
//		itemUsageInfo.setImage(SWTResourceCache.getImage("preview"));
//		itemUsageInfo.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				usageInfoAdapter();
//			}
//		});
//	}

	protected void usageInfoAdapter() {
		if (lot == null) return;
		if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
			UI.showInfo(Message.getString("wip.can_not_trace_material"));
			return;
		}
		UsageInfoDialog usageDialog = new UsageInfoDialog(UI.getActiveShell(), form, lot);
		usageDialog.open();
	}
	
	private void createToolItemSoId(ToolBar tBar) {
		itemSoIdInfo = new AuthorityToolItem(tBar, SWT.PUSH, KEY_SEARCHBYLOT_SOID);
		itemSoIdInfo.setText(Message.getString("wip.seelot_soid"));
		itemSoIdInfo.setImage(SWTResourceCache.getImage("component"));
		itemSoIdInfo.setEnabled(false);
		itemSoIdInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				getSoIdAdapter();
			}
		});
	}

	private void createToolItemComponent(ToolBar tBar) {
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
	
	protected void createToolItemPrint(ToolBar tBar) {
		itemPrint = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SEARCHBYLOT_PRINT);
		itemPrint.setText(Message.getString("common.print"));
		itemPrint.setImage(SWTResourceCache.getImage("print"));
		itemPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printAdapter();
			}
		});
	}
	
	protected void updateLotContent(Lot lot) {
		try {
			setAdObject(lot);
			input = null;
			WipManager wipManager = Framework.getService(WipManager.class);
			input = wipManager.getLotAllStorage(Env.getOrgRrn(), lot.getObjectRrn());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	public void refresh() {
		super.refresh();
		tableManager.setInput(input);
		tableManager.updateView(viewer);
	}
	
	@Override
	protected void refreshAdapter() {
		if(getAdObject() instanceof Lot) {
			updateLotContent((Lot)getAdObject());
		}
		super.refreshAdapter();
	}
	
	protected void getSoIdAdapter(){
		String soid=lot.getSoId();
		soid=soid.substring(1);
		String urlfmt = Message.getString("url.soid");
		
			String url = String.format(urlfmt,soid,Env.getUserName());
			BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
			bd.open();
	}
	
	protected void printAdapter() {
		try {
			Lot lot = (Lot)getAdObject();
			if(lot != null && lot.getLotId() != null){
				SingleLotPrintDialog lptd = new SingleLotPrintDialog(UI.getActiveShell(), lot);
				if(lptd.open() == Dialog.OK) {
					UI.showInfo(Message.getString("bas.lot_print_finished"));
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	private void componentAdapter() {
		if (lot == null) return;
		if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
			UI.showInfo(Message.getString("wip.can_not_trace_material"));
			return;
		}
		ComponentDialog componentDialog = new ComponentDialog(UI.getActiveShell(), form, lot);
		componentDialog.open();
	}
}
