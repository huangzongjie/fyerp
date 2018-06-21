package com.graly.erp.pur.request;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class POLineListSection extends EntitySection {
	protected TableListManager tableManager;
	protected StructuredViewer viewer;
	protected ADTable adTable;
	protected Section section;
	protected IFormPart spart;
	protected List<PurchaseOrderLine> poLines;
	private PurchaseOrderLine selectedpoLine;
	protected POLineListDialog poLineListDialog;

	public POLineListSection(ADTable adTable, List<PurchaseOrderLine> list, POLineListDialog poLineListDialog) {
		super(adTable);
		this.poLines = list;
		this.poLineListDialog = poLineListDialog;
		tableManager = new TableListManager(adTable);
	}

	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();

		section = toolkit.createSection(parent, sectionStyle);
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
		section.setText(String.format(Message.getString("common.detail"), I18nUtil.getI18nMessage(table, "label")));
		createTableViewer(client, toolkit);

		section.setClient(client);
		createViewAction(viewer);
	}

	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		viewer = tableManager.createViewer(client, toolkit);
		if (poLines != null) {
			viewer.setInput(poLines);
			tableManager.updateView(viewer);
		}
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				poLineListDialog.flag = true;
				poLineListDialog.buttonPressed(Dialog.OK);
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof PurchaseOrderLine) {
			selectedpoLine = (PurchaseOrderLine) obj;
		} else {
			selectedpoLine = null;
		}
	}

	public void createToolBar(Section section) {

	}

	public PurchaseOrderLine getSelectedpoLine() {
		return selectedpoLine;
	}

	public void setSelectedpoLine(PurchaseOrderLine selectedpoLine) {
		this.selectedpoLine = selectedpoLine;
	}
}
