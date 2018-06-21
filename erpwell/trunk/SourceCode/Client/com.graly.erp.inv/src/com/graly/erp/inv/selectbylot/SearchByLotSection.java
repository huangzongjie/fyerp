package com.graly.erp.inv.selectbylot;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
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

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.SingleLotPrintDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class SearchByLotSection extends EntitySection {
	private static final Logger logger = Logger.getLogger(SearchByLotSection.class);
	protected Text text;
	private ToolItem itemPrint;

	public SearchByLotSection(ADTable table) {
		super(table);
	}
	
	public void createContents(IManagedForm form, Composite parent) {
		super.createContents(form, parent);
		getDetailForms().get(0).getFields().get("dateIn").setEnabled(false);
		getDetailForms().get(0).getFields().get("dateOut").setEnabled(false);
		getDetailForms().get(0).getFields().get("warehouseRrn").setEnabled(false);
		getDetailForms().get(0).getFields().get("locatorRrn").setEnabled(false);
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
					Lot lot = null;
					String lotId = tLotId.getText();
					lot = searchLot(lotId);
					tLotId.selectAll();
					if (lot == null) {
						tLotId.setForeground(SWTResourceCache.getColor("Red"));
						initAdObject();
					} else {
						setAdObject(lot);
					}
					refresh();
					break;
				}
			}

			public Lot searchLot(String lotId) {
				try {
					INVManager invManager = Framework.getService(INVManager.class);
					return invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				} catch (Exception e) {
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

	public void initAdObject() {
		Lot lot = new Lot();
		lot.setOrgRrn(Env.getOrgRrn());
		setAdObject(lot);
		refresh();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemRefresh(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
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
}
