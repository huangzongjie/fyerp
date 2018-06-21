package com.graly.erp.wip.seelotinfo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.AbstractField;
import com.graly.mes.wip.model.Lot;

public class ComponentForm extends Form {
	protected IMessageManager mmng;
	protected LotField field;
	private static final String FIELD_ID = "componentTree";
	private TreeViewer viewer;
	private ADTable adTable;
	private ComponentTreeManager treeManager;

	public ComponentForm(Composite parent, int style, Object object) {
		super(parent, style, object);
	}

	public ComponentForm(Composite parent, int style, ADBase adBase, IMessageManager mmng, ADTable adTable) {
		this(parent, style, adBase);
		this.mmng = mmng;
		this.adTable = adTable;
		createForm();
	}

	@Override
	public void createForm() {
		super.createForm();
	}

	@Override
	public void addFields() {
		field = new LotField(FIELD_ID);
		addField(FIELD_ID, field);
	}

	private List<Lot> getInput() {
		List<Lot> list = new ArrayList<Lot>();
		list.add((Lot) getObject());
		return list;
	}

	@Override
	public boolean validate() {
		return false;
	}

	private class LotField extends AbstractField {
		public LotField(String id) {
			super(id);
		}

		@Override
		public void createContent(Composite composite, FormToolkit toolkit) {
			mControls = new Control[1];
			treeManager = new ComponentTreeManager(SWT.NULL, adTable);
			GridLayout gl = new GridLayout();
			gl.marginHeight = 0;
			gl.marginWidth = 0;
			composite.setLayout(gl);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			viewer = (TreeViewer) treeManager.createViewer(new Tree(composite, SWT.LINE_DASHDOTDOT | SWT.FULL_SELECTION | SWT.BORDER
					| SWT.H_SCROLL | SWT.V_SCROLL), toolkit);
			treeManager.setInput(getInput());
			mControls[0] = viewer.getControl();
		}

		@Override
		public void refresh() {
		}
	}
}
