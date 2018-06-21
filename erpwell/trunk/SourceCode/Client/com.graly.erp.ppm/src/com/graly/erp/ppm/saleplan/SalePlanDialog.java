package com.graly.erp.ppm.saleplan;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.ppm.model.Mps;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class SalePlanDialog extends ExtendDialog {
	private static final Logger logger = Logger.getLogger(SalePlanDialog.class);
	public static final String DIALOG_ID = "com.graly.erp.ppm.saleplan.SalePlanDialog";
	private static int MIN_DIALOG_WIDTH = 500;
	private static int MIN_DIALOG_HEIGHT = 300;
	protected ADTable adTable;
	protected SalePlanDialogForm salePlanDialogForm;
	protected static final String TABLE_NAME = "PPMMps";
	protected Mps mps;
	protected ADBase selectEntity;
	protected Integer result;

	public SalePlanDialog() {
		super();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		initAdTableByTableId();
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		setTitle(Message.getString("ppm.saleplan"));
		Composite content = (Composite) super.createDialogArea(parent);
		GridData gd = new GridData(GridData.FILL_BOTH);
		content.setLayoutData(gd);
		content.setLayout(new GridLayout(1, true));

		salePlanDialogForm = new SalePlanDialogForm(content, SWT.NULL, adTable, mps);
		salePlanDialogForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		createViewAction(salePlanDialogForm.getViewer());
		return content;
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				buttonPressed(IDialogConstants.OK_ID);
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
		if (obj instanceof Mps) {
			mps = (Mps) obj;
		} else {
			mps = null;
		}
	}

	protected void initAdTableByTableId() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, TABLE_NAME);
			adTable = entityManager.getADTableDeep(Long.parseLong(getTableId()));
		} catch (Exception e) {
			logger.error("SalePlanDialog : initAdTableByTableId()", e);
		}
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			initTableViewer();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	public void initTableViewer() {
		if (salePlanDialogForm.getViewer().getTable().getSelection().length > 0) {
			TableItem ti = salePlanDialogForm.getViewer().getTable().getSelection()[0];
			selectEntity = (ADBase) ti.getData();

			SalePlanEditor editor = (SalePlanEditor) getParent();
			SalePlanEntryPage page = (SalePlanEntryPage) editor.getActivePageInstance();
			mps = (Mps) this.selectEntity;

			if (this.selectEntity != null) {
				page.getPlanBlock().setWhereClause(" mpsId='" + mps.getMpsId() + "' ");
				result = reservedDateCompare(mps);
				if (result.intValue() >= 0) {
					mps.setFrozen(true);
				} else {
					mps.setFrozen(false);
				}
				page.getPlanBlock().setParentObject(mps);
			}
			page.getPlanBlock().refresh();
		}
		okPressed();

		if (this.selectEntity == null) {
			UI.showInfo(Message.getString("ppm.notchacksaleplan"));
		} else if (reservedDateCompare(mps).intValue() >= 0) {
			UI.showInfo(Message.getString("ppm.datereserve"));
		}
	}

	public Integer reservedDateCompare(Mps planSetup) {
		Integer result = null;
		try {
			Date date = planSetup.getDateReserved();
			Date now = Env.getSysDate();
			return now.compareTo(date);
		} catch (Exception e) {
			logger.error("EntityBlock : createSectionDesc ", e);
			return result;
		}
	}
}
