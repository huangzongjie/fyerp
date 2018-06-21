package com.graly.erp.wip.mo.create;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.workcenter.MoLineSection;
import com.graly.erp.wip.workcenter.WorkCenterSection;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.client.WipManager;

public class ViewWorkCenterDialog extends InClosableTitleAreaDialog {
	public static final String STATUS = "lineStatus";
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 500;
	private WorkCenter workCenter;
	private SimpleWorkSection wcSection;
	protected IManagedForm form;

	public ViewWorkCenterDialog(Shell parentShell, WorkCenter workCenter) {
		super(parentShell);
		this.workCenter = workCenter;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		form = new ManagedForm(toolkit, sForm);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);		
		createContent(body, toolkit);
		setTitle(String.format(Message.getString("common.detail"),
				I18nUtil.getI18nMessage(wcSection.getWcAdTable(), "label")));
		refresh();
		return comp;
	}
	
	protected void createContent(Composite parent, FormToolkit toolkit) {
		wcSection = new SimpleWorkSection(form);
		wcSection.createContent(parent);
		wcSection.setWorkCenter(workCenter);
	}
	
	protected void refresh() {
		try {
			wcSection.refreshWorkCenter();			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	@Override
	protected void okPressed() {
		setErrorMessage(null);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	class SimpleWorkSection extends WorkCenterSection {
		public SimpleWorkSection(IManagedForm form) {
			super(form);
		}
		
		@Override
		protected void createMOSectionContent(IManagedForm form,
				Composite parent) {
			if(moLineAdTable == null) {
				moLineAdTable = getAdTableByName(TABLE_ANME_MO_LINE);
				setStatusDisplay();
			}
			TableListManager tableManager = new TableListManager(moLineAdTable);
			moLineSection = new SimpleMoLineSection(tableManager);
			moLineSection.createContent(form, parent);
		}	
		
		private void setStatusDisplay() {
			if(moLineAdTable != null) {
				for(ADField adField : moLineAdTable.getFields()) {
					if(STATUS.equals(adField.getName())) {
						adField.setIsMain(true);
						break;
					}
				}
			}
		}
		
		protected void refreshWorkCenter() throws Exception {
			if(moLineSection instanceof SimpleMoLineSection) {
				((SimpleMoLineSection)moLineSection).refreshWorkCenter();
			}
		}
		
		protected void createInvMaterialContent(IManagedForm form, Composite parent) {}
		
		protected void setOrientation(int[] weights) {}
	}
	
	class SimpleMoLineSection extends MoLineSection {
		public SimpleMoLineSection(TableListManager tableManager) {
			super(tableManager);
		}
		
		@Override
		public void createToolBar(Section section) {
			ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
			createToolItemChart(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemRefresh(tBar);
			section.setTextClient(tBar);
		}
		
		public void refreshWorkCenter() throws Exception {
			if (workCenter != null && workCenter.getObjectRrn() != 0) {
				String whereClause = " (lineStatus = '" + Documentation.STATUS_APPROVED + "' "
					+ " OR lineStatus = '" + Documentation.STATUS_DRAFTED + "') ";
				WipManager wipManager = Framework.getService(WipManager.class);
				List<ManufactureOrderLine> list = wipManager.getMoLineByWorkCenter(Env.getOrgRrn(), workCenter.getObjectRrn(), whereClause);
				if (list != null)
					this.displayCount = list.size();
				else
					displayCount = 0;
				viewer.setInput(list);
				tableManager.updateView(viewer);
				createSectionDesc(section);
			}
		}
		
		public void refreshAll() {
			try {
				refreshWorkCenter();				
			} catch(Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}

		protected void setWorkStatusChanged(String status) {}
		
		protected void setLineStatusChanged(String status) {}
	}
}
