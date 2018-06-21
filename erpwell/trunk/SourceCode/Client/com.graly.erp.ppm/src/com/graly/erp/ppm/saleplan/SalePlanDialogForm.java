package com.graly.erp.ppm.saleplan;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.ppm.model.Mps;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.forms.Form;

public class SalePlanDialogForm extends Form {
	protected ADTable table;
	protected TableViewer viewer;
	protected String whereClause;
	protected Mps mps;

	public Mps getMps() {
		return mps;
	}

	public void setMps(Mps mps) {
		this.mps = mps;
	}

	public SalePlanDialogForm(Composite parent, int style, IMessageManager mmng, Mps mps) {
		super(parent, style, mps);
		this.mps = mps;
		createForm();
	}

	public SalePlanDialogForm(Composite parent, int style, ADTable table, Mps mps) {
		super(parent, style, null);
		this.table = table;
		this.mps = mps;
		createForm();
	}

	@Override
	protected void createContent() {
		toolkit = new FormToolkit(getDisplay());
		setLayout(new FillLayout());
		form = toolkit.createScrolledForm(this);

		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);

		EntityTableManager em = new EntityTableManager(table);
		viewer = (TableViewer) em.createViewer(body, toolkit);
		viewer.getTable().addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = viewer.getTable().getSelection();
				Object obj = items[0].getData();
				if (obj instanceof Mps) {
					mps = (Mps) obj;
				} else {
					mps = null;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		EntityItemInput input = new EntityItemInput(em.getADTable(), whereClause, "");
		viewer.setInput(input);
		em.updateView(viewer);
	}

	@Override
	public void addFields() {
	}

	@Override
	public boolean validate() {
		return false;
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

}
