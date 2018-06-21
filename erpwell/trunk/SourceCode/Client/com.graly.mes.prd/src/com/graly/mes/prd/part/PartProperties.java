package com.graly.mes.prd.part;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.AbstractField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADAuthority;
import com.graly.mes.prd.CopyToPrdDialog;
import com.graly.mes.prd.ParameterForm;
import com.graly.mes.prd.PrdProperties;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.model.Part;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.def.Process;

public class PartProperties extends PrdProperties implements IValueChangeListener{
	
	private static final Logger logger = Logger.getLogger(PartProperties.class);
	private static final String FIELD_ID = "processName";
	protected AbstractField porcessField;
	
	public PartProperties() {
		super();
    }
	
	@Override
	public void createSectionContent(Composite parent) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(parent, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10; 
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
				porcessField = (AbstractField)itemForm.getFields().get(FIELD_ID);
				if (porcessField != null) {
					porcessField.addValueChangeListener(this);
				}
			}
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}
	
	@Override
	public void valueChanged(Object sender, Object newValue){
		if(porcessField != null) {
			if(newValue != null && !"".equals(((String)newValue).trim())) {
				Long processRrn = Long.parseLong((String)newValue);
				try {
					Process process = new Process();
					process.setObjectRrn(processRrn);
					PrdManager prdManager = Framework.getService(PrdManager.class);
					process = (Process)prdManager.getProcessDefinition(process);
					Part part = (Part)getAdObject();
					if (part != null) {
						part.setWfParameters(process.getWfParameters());
						for (Form form : getDetailForms()) {
							if (form instanceof ParameterForm) {
								form.loadFromObject();
							}
						}
					}
				} catch(Exception e) {
					logger.error("Error: " + e);
				}
			}
		}		
	}
	
	protected void createToolItemFrozen(ToolBar tBar) {
		itemFrozen = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_PRD_PART_FROZEN);
		itemFrozen.setImage(SWTResourceCache.getImage("frozen"));
		itemFrozen.setText(Message.getString("common.frozen"));
		itemFrozen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				frozenAdapter();
			}
		});
	}

	protected void createToolItemActive(ToolBar tBar) {
		itemActive = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_PRD_PART_ACTIVE);
		itemActive.setImage(SWTResourceCache.getImage("active"));
		itemActive.setText(Message.getString("common.active"));
		itemActive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				activeAdapter();
			}
		});
	}
	
	@Override
	protected void frozenAdapter() {
		try {					
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()){
					if (!detailForm.saveToObject()){
						saveFlag = false;
					}
				}
				if (saveFlag){
					for (Form detailForm : getDetailForms()){
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					PrdManager prdManager = Framework.getService(PrdManager.class);
					Part part = (Part)getAdObject();
					ADBase obj;
					if (ProcessDefinition.STATUS_UNFROZNE.equalsIgnoreCase(part.getStatus())){
						obj = prdManager.frozen(part, Env.getUserRrn());
						UI.showInfo(Message.getString("common.frozen_success"));
					} else {
						obj = prdManager.unFrozen(part, Env.getUserRrn());
						UI.showInfo(Message.getString("common.unfrozen_success"));
					} 
					ADManager entityManager = Framework.getService(ADManager.class);
					setAdObject(entityManager.getEntity(obj));

					refresh();
					getMasterParent().refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void copyFromAdapter() {
		List<ADField> fields = getTable().getFields();
		for (ADField field : fields) {
			if ("copyFrom".equals(field.getName())){
				CopyToPrdDialog dialog = new CopyToPrdDialog(Display.getCurrent().getActiveShell(), field);
				if (dialog.open() == Dialog.OK) {
					String prdId = dialog.getFlowId();
					try {					
						form.getMessageManager().removeAllMessages();
						ADManager entityManager = Framework.getService(ADManager.class);
						Part part = new Part();
						part.setObjectRrn(new Long(prdId));
						part = (Part)entityManager.getEntity(part);
						if (part != null) {
							part.setObjectRrn(null);
							part.setVersion(null);
							part.setStatus("");
//							if (part.getVariableAccesses() != null) {
//								for (VariableAccess variable : part.getVariableAccesses()) {
//									variable.setObjectRrn(null);
//								}
//							}
							setAdObject(part);
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
	protected void activeAdapter() {
		try {					
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				PrdManager prdManager = Framework.getService(PrdManager.class);
				ADBase obj = prdManager.active((Part)getAdObject(), Env.getUserRrn());
				ADManager entityManager = Framework.getService(ADManager.class);
				setAdObject(entityManager.getEntity(obj));
				UI.showInfo(Message.getString("common.active_success"));

				refresh();
				getMasterParent().refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	@Override
	protected void saveAdapter() {
		try {					
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()){
					if (!detailForm.saveToObject()){
						saveFlag = false;
					}
				}
				if (saveFlag){
					for (Form detailForm : getDetailForms()){
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					PrdManager prdManager = Framework.getService(PrdManager.class);
					Part part = (Part)getAdObject();
					ADBase obj = prdManager.savePart(part, Env.getUserRrn());						
					ADManager entityManager = Framework.getService(ADManager.class);
					setAdObject(entityManager.getEntity(obj));
					UI.showInfo(Message.getString("common.save_successed"));//µ¯³öÌáÊ¾¿ò
					
					refresh();
					getMasterParent().refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	@Override
	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
		Part newBase = (Part)this.getAdObject();
		if (newBase != null ) {
			statusChanged(newBase.getStatus());
		} else {
			statusChanged("");
		}
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}	
}
