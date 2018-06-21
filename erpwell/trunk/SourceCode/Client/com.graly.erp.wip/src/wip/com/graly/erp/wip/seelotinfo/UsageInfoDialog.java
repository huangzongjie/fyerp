package com.graly.erp.wip.seelotinfo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;

public class UsageInfoDialog extends InClosableTitleAreaDialog {
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 300;
	protected IManagedForm form;
	protected Section section;
	protected ADTable adTable;
	protected TableViewerManager manager;
	protected TableViewer viewer;
	protected Lot lot;
	private String TABLE_NAME_LOT = "WIPUsageLot";

	public UsageInfoDialog(Shell parentShell,IManagedForm form, Lot lot) {
		super(parentShell);
		this.lot = lot;
		this.form = form;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		adTable = getLotTable();
//		String title = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
		setTitle(Message.getString("wip.usage_info"));

		FormToolkit toolkit = form.getToolkit();
		Composite content = toolkit.createComposite(composite, SWT.NULL);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new GridLayout(1, false));

		section = toolkit.createSection(content, Section.TITLE_BAR);
		section.setText(Message.getString("wip.usage_list"));
		section.marginWidth = 2;
		section.marginHeight = 2;
		toolkit.createCompositeSeparator(section);
		createToolBar(section);

		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 2;
		layout.leftMargin = 2;
		layout.rightMargin = 2;
		layout.bottomMargin = 2;
		content.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = true;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);
		GridData g = new GridData(GridData.FILL_BOTH);
		client.setLayoutData(g);

		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);

		return composite;
	}

	public void createToolBar(Section section) {
	}

	protected void createSectionContent(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(gd);
		toolkit.paintBordersFor(parent);
		adTable = getLotTable();
		createTableViewer(parent, toolkit);
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		manager = new TableListManager(adTable);
		viewer = (TableViewer)manager.createViewer(client, toolkit);
		manager.setInput(getUsage());
		manager.updateView(viewer);
	}
	
	private List<LotComponent> getUsage(){
		try {
			WipManager wipManager = Framework.getService(WipManager.class);
			List<LotComponent> list = wipManager.getLotUsages(lot.getObjectRrn());
			return list;
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return new ArrayList<LotComponent>();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	private ADTable getLotTable() {
		try {
			if (adTable != null) {
				return adTable;
			} else {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME_LOT);
				return adTable;
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
}
