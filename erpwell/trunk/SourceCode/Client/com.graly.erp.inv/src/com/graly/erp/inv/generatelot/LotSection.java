package com.graly.erp.inv.generatelot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.LotPrintDialog;
import com.graly.erp.inv.lotprint.LotPrintProgerss;
import com.graly.erp.inv.lotprint.LotPrintProgressDialog;
import com.graly.erp.inv.model.ConditionItem;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotSection {
	private static final Logger logger = Logger.getLogger(LotSection.class);

	private String lotType = "SERIAL";
	protected List<Lot> lots;

	protected LotTableManager lotManager;
	protected TableViewer viewer;
	protected ADTable adTable;
	protected Section section;
	protected IFormPart spart;
	protected IManagedForm form;

	protected ToolItem itemSave;
	protected ToolItem itemPrint;
	protected ConditionItem conditionItem;
	protected Material material = new Material();
	protected Lot selectLot;
	
	protected boolean isSaved = false;

	public LotSection(ADTable adTable) {
		this.adTable = adTable;
	}
	
	public LotSection(ADTable adTable, String lotType) {
		this(adTable);
		this.lotType = lotType;
	}

	public LotSection(ADTable adTable, ConditionItem conditionItem) {
		this.adTable = adTable;
		this.conditionItem = conditionItem;
	}

	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();

		section = toolkit.createSection(parent, sectionStyle);
		section.setText(Message.getString("wip.lot_list"));
		section.marginWidth = 3;
		section.marginHeight = 4;
		toolkit.createCompositeSeparator(section);

		createToolBar(section);
		setEnabled(true);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite client = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(gridLayout);

		spart = new SectionPart(section);
		form.addPart(spart);
		section.setText(Message.getString("wip.lot_list"));

		createTableViewer(client, toolkit);
		section.setClient(client);
	}

	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		initTableContent();
		lotManager = new LotTableManager(adTable, lotType);
		viewer = (TableViewer) lotManager.createViewer(client, toolkit, 400);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setMovementLineSelect(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		refresh();
	}

	private void setMovementLineSelect(Object obj) {
		if (obj instanceof Lot) {
			selectLot = (Lot) obj;
		} else {
			selectLot = null;
		}
	}
	
	protected void initTableContent() {
		if (conditionItem != null) {
			BigDecimal qty = conditionItem.getNumber() == null ? BigDecimal.ZERO : conditionItem.getNumber();
			int bath = conditionItem.getBatchNumber() == null ? 1 : Integer.parseInt(conditionItem.getBatchNumber().toString());
			ADManager adManager;
			try {
				adManager = Framework.getService(ADManager.class);
				String whereClause = " objectRrn ='" + conditionItem.getMaterialRrn() + "' ";
				List<Material> materials = adManager.getEntityList(Env.getOrgRrn(), Material.class, Integer.MAX_VALUE, whereClause, "");
				if (materials.size() > 0)
					material = materials.get(0);

				INVManager invManager = Framework.getService(INVManager.class);
				if (material.getIsLotControl() && material.getLotType() != null) {
					if (Lot.LOTTYPE_BATCH.equals(material.getLotType())) {
						lots = invManager.generateBatchLot(Env.getOrgRrn(), material, qty, bath, Env.getUserRrn());
					} else {
						int quentitys = (int) qty.intValue();
						lots = invManager.generateSerialLot(Env.getOrgRrn(), material, quentitys, Env.getUserRrn());
					}
					lotType = material.getLotType();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		}
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new ToolItem(tBar, SWT.PUSH);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}

	protected void createToolItemPrint(ToolBar tBar) {
		itemPrint = new ToolItem(tBar, SWT.PUSH);
		itemPrint.setText(Message.getString("common.print"));
		itemPrint.setImage(SWTResourceCache.getImage("print"));
		itemPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printAdapter();
			}
		});
	}

	protected void saveAdapter() {
		try {
			if(validate()) {
				INVManager invManager = Framework.getService(INVManager.class);
				invManager.saveGenLot(Env.getOrgRrn(), material, lots, Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				
				setEnabled(false);
				lotManager.setEdit(false);
				isSaved = true;
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected boolean validate() {
		try {
			BigDecimal qty = conditionItem.getNumber() == null ? BigDecimal.ZERO : conditionItem.getNumber();
			List<String> lotIds = new ArrayList<String>();
			String lotId;
			BigDecimal usedQty = BigDecimal.ZERO;
			for(Lot lot : lots) {
				lotId = lot.getLotId();
				if(lotId == null || "".equals(lotId.trim())) {
					UI.showError(Message.getString("inv.invalid_lotId"));
					return false;
				}
				if(lotIds.contains(lotId)) {
					UI.showError(String.format(Message.getString("inv.duplicate_lotId"), lotId));
					return false;
				}
				lotIds.add(lotId);
				usedQty = usedQty.add(lot.getQtyCurrent());
			}
			if(qty.compareTo(usedQty) != 0) {
				UI.showError(String.format(Message.getString("inv.lot_qty_total_is_not_equal"),
						usedQty.toString(), qty.toString()));
				return false;
			}
		} catch(Exception e) {
			logger.error("Error at LotMasterSection : barcodeAdapter " + e);
			return false;
		}
		return true;
	}

	protected void printAdapter() {
		try {
			lots = (List<Lot>)viewer.getInput();
			if(lots != null && lots.size() != 0){
				LotPrintDialog printDialog = new LotPrintDialog(lots, this.selectLot);
				printDialog.open();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void refresh() {
		if (lots == null) {
			lots = new ArrayList<Lot>();
		}
		lotManager.setInput(lots);
		lotManager.updateView(viewer);
		createSectionDesc(section);
	}
	
	protected void setEnabled(boolean enabled) {
		itemSave.setEnabled(enabled);
		itemPrint.setEnabled(itemSave.getEnabled() ? false : true);
	}

	protected void createSectionDesc(Section section) {
		String text = Message.getString("common.totalshow");
		int count = viewer.getTable().getItemCount();
		if (count > Env.getMaxResult()) {
			text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
		} else {
			text = String.format(text, String.valueOf(count), String.valueOf(count));
		}
		section.setDescription("  " + text);
	}
	
	protected boolean isSureExit() {
		return isSaved;
	}
	
	public boolean isSaved() {
		return isSaved;
	}
}
