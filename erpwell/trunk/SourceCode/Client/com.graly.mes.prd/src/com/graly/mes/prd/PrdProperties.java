package com.graly.mes.prd;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADAuthority;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.model.Part;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;

public class PrdProperties extends EntityProperties {

	private static final Logger logger = Logger.getLogger(PrdProperties.class);

	protected ToolItem itemCopyFrom;
	protected ToolItem itemFrozen;
	protected ToolItem itemActive;
	protected Label label;

	public PrdProperties() {
		super();
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		StructuredSelection ss = (StructuredSelection) selection;
		Object object = ss.getFirstElement();
		try {
			if (object != null && ((ADBase) object).getObjectRrn() != null) {
				if(object instanceof ProcessDefinition) {
					PrdManager prdManager = Framework.getService(PrdManager.class);
					setAdObject(prdManager.getProcessDefinition((ProcessDefinition)object));
				}
				if(object instanceof Part) {
					ADManager adManager = Framework.getService(ADManager.class);
					setAdObject(adManager.getEntity((ADBase)object));
				}
			} else {
				setAdObject((ADBase)object);
			}
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	@Override
	protected void createSectionTitle(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		label = toolkit.createLabel(client, "");
		label.setFont(JFaceResources.getHeaderFont());
		label.setForeground(SWTResourceCache.getColor("Folder"));
		label.setLayoutData(gd);
	}

	private void buildTitle(String status) {
		if (status == null) {
			status = "";
		}
		label.setText(status);
	}

	@Override
	public void createSectionContent(Composite parent) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(parent, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(
						new Color[] { selectedColor, toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			EntityForm itemForm = null;
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			String tabText = I18nUtil.getI18nMessage(tab, "label");
			item.setText(tabText);
			if (tabText.equalsIgnoreCase(Message.getString("common.parameter"))) {
				itemForm = new ParameterForm(getTabs(), SWT.NONE, tab, mmng);
			} else {
				itemForm = new EntityForm(getTabs(), SWT.NONE, tab, mmng);
			}
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemCopyFrom(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemFrozen(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemActive(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemFrozen(ToolBar tBar) {
		itemFrozen = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_PRD_PROCESS_FROZEN);
		itemFrozen.setImage(SWTResourceCache.getImage("frozen"));
		itemFrozen.setText(Message.getString("common.frozen"));
		itemFrozen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				frozenAdapter();
			}
		});
	}

	protected void createToolItemCopyFrom(ToolBar tBar) {
		itemCopyFrom = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PROCESS_COPYFROM);
		itemCopyFrom.setImage(SWTResourceCache.getImage("copy"));
		itemCopyFrom.setText(Message.getString("common.copyfrom"));
		itemCopyFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				copyFromAdapter();
			}
		});
	}

	protected void createToolItemActive(ToolBar tBar) {
		itemActive = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_PRD_PROCESS_ACTIVE);
		itemActive.setImage(SWTResourceCache.getImage("active"));
		itemActive.setText(Message.getString("common.active"));
		itemActive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				activeAdapter();
			}
		});
	}

	public void statusChanged(String newStatus) {
		buildTitle(newStatus);
		if (ProcessDefinition.STATUS_UNFROZNE.equals(newStatus)) {
			itemFrozen.setImage(SWTResourceCache.getImage("frozen"));
			itemFrozen.setText(Message.getString("common.frozen"));
			itemFrozen.setEnabled(true);
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
			itemActive.setEnabled(false);
			setEnable(true);
		} else if (ProcessDefinition.STATUS_FROZNE.equals(newStatus)) {
			itemFrozen.setImage(SWTResourceCache.getImage("unfrozen"));
			itemFrozen.setText(Message.getString("common.unfrozen"));
			itemFrozen.setEnabled(true);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			itemActive.setEnabled(true);
			setEnable(false);
		} else if (ProcessDefinition.STATUS_ACTIVE.equals(newStatus)) {
			itemFrozen.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			itemActive.setEnabled(false);
			setEnable(false);
		} else if (ProcessDefinition.STATUS_INACTIVE.equals(newStatus)) {
			itemFrozen.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			itemActive.setEnabled(true);
			setEnable(false);
		} else {
			itemFrozen.setEnabled(false);
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
			itemActive.setEnabled(false);
			setEnable(true);
		}
	}

	private void setEnable(boolean enableFlag) {
		for (Form detailForm : getDetailForms()) {
			detailForm.setEnabled(enableFlag);
		}
	}

	protected void frozenAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					PrdManager prdManager = Framework.getService(PrdManager.class);
					ProcessDefinition pf = (ProcessDefinition) getAdObject();
					if (pf.getFlowDocument() == null || "".equalsIgnoreCase(pf.getFlowDocument().trim())) {
						pf.setFlowDocument(pf.initFlowDocument(pf.getName()));
						pf.setFlowContent(pf.initFlowContent());
					}
					ADBase obj;
					if (ProcessDefinition.STATUS_UNFROZNE.equalsIgnoreCase(pf.getStatus())) {
						obj = prdManager.frozen(pf, Env.getUserRrn());
						UI.showInfo(Message.getString("common.frozen_success"));
					} else {
						obj = prdManager.unFrozen(pf, Env.getUserRrn());
						UI.showInfo(Message.getString("common.unfrozen_success"));
					}
					setAdObject(prdManager.getProcessDefinition((ProcessDefinition) obj));
					refresh();
					getMasterParent().refreshUpdate(getAdObject());
//					getMasterParent().refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void activeAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				PrdManager prdManager = Framework.getService(PrdManager.class);
				ADBase obj = prdManager.active((ProcessDefinition) getAdObject(), Env.getUserRrn());
				setAdObject(prdManager.getProcessDefinition((ProcessDefinition)obj));
				UI.showInfo(Message.getString("common.active_success"));
				refresh();
				getMasterParent().refreshUpdate(getAdObject());
//				getMasterParent().refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void copyFromAdapter() {
		List<ADField> fields = getTable().getFields();
		for (ADField field : fields) {
			if ("copyFrom".equals(field.getName())) {
				CopyToPrdDialog dialog = new CopyToPrdDialog(Display.getCurrent().getActiveShell(), field);
				if (dialog.open() == Dialog.OK) {
					String prdRrn = dialog.getFlowId();
					try {
						form.getMessageManager().removeAllMessages();
						PrdManager prdManager = Framework.getService(PrdManager.class);
						if(prdRrn == null || "".equals(prdRrn.trim())) {
							return;
						}
						ProcessDefinition copyFromPrd = (ProcessDefinition)getAdObject();
						copyFromPrd.setObjectRrn(new Long(prdRrn));
						ProcessDefinition pf = prdManager.getProcessDefinition(copyFromPrd);
						if (pf != null) {
							ProcessDefinition newPf = (ProcessDefinition)pf.clone();
							newPf.setVersion(null);
							newPf.setStatus("");
							setAdObject(newPf);
							refresh();
						}
					} catch (Exception e) {
						ExceptionHandlerManager.asyncHandleException(e);
						return;
					}
				}
				break;
			}
		}
	}

	@Override
	protected void saveAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					ADBase oldBase = getAdObject();
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					PrdManager prdManager = Framework.getService(PrdManager.class);
					ProcessDefinition pf = (ProcessDefinition) getAdObject();
					if (pf.getFlowDocument() == null || "".equalsIgnoreCase(pf.getFlowDocument().trim())) {
						pf.setFlowDocument(pf.initFlowDocument(pf.getName()));
						pf.setFlowContent(pf.initFlowContent());
					}
					ProcessDefinition obj = prdManager.saveProcessDefinition(pf, Env.getUserRrn());
					setAdObject(prdManager.getProcessDefinition(obj));

					UI.showInfo(Message.getString("common.save_successed"));// µ¯³öÌáÊ¾¿ò
					refresh();
					ADBase newBase = getAdObject();
					if (oldBase.getObjectRrn() == null) {
						getMasterParent().refreshAdd(newBase);
					} else {
						getMasterParent().refreshUpdate(newBase);
					}
//					getMasterParent().refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	@Override
	public void setAdObject(ADBase adObject) {
		super.setAdObject(adObject);
		ProcessDefinition newPf = (ProcessDefinition) this.getAdObject();
		if (newPf != null) {
			statusChanged(newPf.getStatus());
		} else {
			statusChanged("");
		}
	}

	@Override
	public void dispose() {
		if (label != null && !label.isDisposed()) {
			label.dispose();
		}
		if (itemCopyFrom != null && !itemCopyFrom.isDisposed()) {
			itemCopyFrom.dispose();
		}
		if (itemFrozen != null && !itemFrozen.isDisposed()) {
			itemFrozen.dispose();
		}
		if (itemActive != null && !itemActive.isDisposed()) {
			itemActive.dispose();
		}
		super.dispose();
	}

	@Override
	protected void refreshAdapter() {
		if(getAdObject() instanceof ProcessDefinition){
			try {
				ProcessDefinition pf = (ProcessDefinition)getAdObject();			
				if(pf != null && pf.getObjectRrn() != null){
					PrdManager prdManager = Framework.getService(PrdManager.class);
					setAdObject(prdManager.getProcessDefinition(pf));
				}
				form.getMessageManager().removeAllMessages();
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
			refresh();
		} else {
			super.refreshAdapter();
		}
	}
}