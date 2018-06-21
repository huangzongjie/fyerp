package com.graly.erp.wip.mo.molinecreate;

import java.util.Date;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.base.model.Material;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.wip.client.WipManager;

public class NewMOLineDialog extends InClosableTitleAreaDialog {
	private NewMOLineSection moLineSection;
	protected ADTable adTable;
	protected ManagedForm managedForm;
	protected WorkCenter workCenter;

	public NewMOLineDialog(Shell parent) {
		super(parent);
	}

	public NewMOLineDialog(Shell shell, ADTable adTable, WorkCenter workCenter) {
		this(shell);
		this.adTable = adTable;
		this.workCenter = workCenter;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		String dialogTitle = String.format(Message.getString("common.detail"), I18nUtil.getI18nMessage(adTable, "label"));
		setTitle(dialogTitle);
		Composite composite = (Composite) super.createDialogArea(parent);
		createFormContent(composite);
		return composite;
	}

	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);

		Composite body = sForm.getForm().getBody();
		configureBody(body);
		ManufactureOrderLine line = new ManufactureOrderLine();
		line.setWorkCenterRrn(workCenter.getObjectRrn());
		line.setOrgRrn(workCenter.getOrgRrn());
		line.setLineNo(10L);
		line.setLineStatus("APPROVED");
		moLineSection = new NewMOLineSection(adTable, line);
		moLineSection.createContents(managedForm, body);
	}

	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, false);
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

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 1000;
		p.y = 480;
		return p;
	}

	@SuppressWarnings("deprecation")
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			try {
				if (moLineSection.getAdObject() != null) {
					boolean saveFlag = true;
					for (Form detailForm : moLineSection.getDetailForms()) {
						if (!detailForm.saveToObject()) {
							saveFlag = false;
						}
					}
					if (saveFlag) {
						for (Form detailForm : moLineSection.getDetailForms()) {
							PropertyUtil.copyProperties(moLineSection.getAdObject(), detailForm.getObject(), detailForm.getFields());
						}
						WipManager wipManager = Framework.getService(WipManager.class);
						if (moLineSection.getAdObject() != null) {
							if (moLineSection.getAdObject() instanceof ManufactureOrderLine) {
								ManufactureOrderLine moLine = (ManufactureOrderLine) moLineSection.getAdObject();
								ADManager adManager = Framework.getService(ADManager.class);
								PrdManager prdManager = Framework.getService(PrdManager.class);
								Material material = new Material();
								material.setObjectRrn(moLine.getMaterialRrn());
								material=(Material) adManager.getEntity(material);
								Process pf = new Process();
								pf.setOrgRrn(Env.getOrgRrn());
								pf.setName(material.getProcessName());
								pf = (Process)prdManager.getActiveProcessDefinition(pf);
								if(!pf.getWorkcenterRrn().equals(moLine.getWorkCenterRrn())){
									UI.showError("子工作令工作中心与物料设置工艺必须一致");
									return;
								}
								if (moLine.getTimeStart() != null && moLine.getTimeEnd() != null) {
									int hoursStart = moLine.getTimeStart().getHours();
									int minutesStart = moLine.getTimeStart().getMinutes();
									Date dateStart = moLine.getDateStart();
									dateStart.setHours(hoursStart);
									dateStart.setMinutes(minutesStart);
									dateStart.setSeconds(0);
									moLine.setDateStart(dateStart);

									int hoursEnd = moLine.getTimeEnd().getHours();
									int minutesEnd = moLine.getTimeEnd().getMinutes();
									Date dateEnd = moLine.getDateEnd();
									dateEnd.setHours(hoursEnd);
									dateEnd.setMinutes(minutesEnd);
									dateEnd.setSeconds(0);
									moLine.setDateEnd(dateEnd);
								}
							}
						}
						ManufactureOrderLine moLine = (ManufactureOrderLine) moLineSection.getAdObject();

						moLine = wipManager.addMoLine(moLine, Env.getUserRrn());
						ADManager entityManager = Framework.getService(ADManager.class);
						moLineSection.setAdObject(entityManager.getEntity(moLine));
						// UI.showInfo(Message.getString("common.save_successed"));//
						okPressed();
					}
				}

			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
	}
}
