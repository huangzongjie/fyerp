package com.graly.erp.pur.po;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
import com.graly.erp.inv.model.ConditionItem;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotSection {
	private static final Logger logger = Logger.getLogger(LotSection.class);

	private String lotType = "BATCH";
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

	public LotSection(ADTable adTable, ConditionItem conditionItem,List<Lot> lots) {
		this.adTable = adTable;
		this.conditionItem = conditionItem;
		this.lots = lots;
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

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
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
