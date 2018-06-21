package com.graly.erp.test.cus;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADAuthority;

public class CustomerProperties extends EntityProperties {
//	private static final Logger logger = Logger.getLogger(ScheduleLotProperties.class);
	protected Text text;

	protected ToolItem itemAdd;
	protected ToolItem itemStart;
	protected UserAddForm userAddForm;
	
	public CustomerProperties() {
		super();
    }
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
	}
	
	@Override
	public void createSectionContent(Composite parent) {       
		super.createSectionContent(parent);
		{
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText("添加客户");
			// 在开发中不能直接在输入提示信息必须要以 Message.getString("xxx")形式获得信息。
			userAddForm = new UserAddForm(getTabs(), SWT.NONE, table, form.getMessageManager());
			item.setControl(userAddForm);
		}
		
		if (getTabs().getTabList().length > 0){
			getTabs().setSelection(0);
		}
	}
	
	@Override
	protected void createSectionTitle(Composite client){
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(2, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, "Customer Name:"); //
		label.setForeground(SWTResourceCache.getColor("Folder"));
		
		text = toolkit.createText(top, "", SWT.BORDER);
		GridData gText = new GridData();
		gText.widthHint = 256;
		text.setLayoutData(gText);
		text.setTextLimit(32);
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemAdd(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		crateTooItemStart(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemAdd(ToolBar tBar) {
		itemAdd = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_WIP_SCHEDULE);
		itemAdd.setText("添加"); //Message.getString("common.lot_schedule")
		itemAdd.setImage(SWTResourceCache.getImage("schedule"));
		itemAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event){
				addAdapter();
			}
		});
	}
	
	protected void crateTooItemStart(ToolBar tBar) {
		itemStart = new ToolItem(tBar, SWT.PUSH);
		itemStart.setText("开始");
		itemStart.setImage(SWTResourceCache.getImage("start"));
		itemStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				startAdapter();
			}
		});
	}

	protected void addAdapter() {
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
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());								
					}
					UI.showInfo(Message.getString("common.save_success"));//弹出提示框
					getMasterParent().refresh();
					this.createAdObject();
					refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}
	
	protected void startAdapter() {
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
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());								
					}
					UI.showInfo(Message.getString("common.save_successed"));//弹出提示框
					getMasterParent().refresh();
					refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}
	
	@Override
	public void setAdObject(ADBase adObject) {
		super.setAdObject(adObject);
	}
	
	public void statusChanged(String newStatus){
		if(newStatus == null || "".equals(newStatus.trim())) {
			itemAdd.setEnabled(true);
			itemStart.setEnabled(false);
		}
	}
}
