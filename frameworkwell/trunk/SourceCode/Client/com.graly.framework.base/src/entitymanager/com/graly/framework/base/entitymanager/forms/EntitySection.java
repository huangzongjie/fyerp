package com.graly.framework.base.entitymanager.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public abstract class EntitySection {
	private static final Logger logger = Logger.getLogger(EntitySection.class);

	protected IManagedForm form;
	public ADBase adObject;
	protected CTabFolder tabs;
	protected List<Form> detailForms = new ArrayList<Form>();
	protected ADTable table;
	protected ToolItem itemNew;
	protected ToolItem itemSave;
	protected ToolItem itemDelete;
	protected ToolItem itemRefresh;
	protected Section section;

	protected static final String ITEM_NEW = "New";
	protected static final String ITEM_SAVE = "Save";
	protected static final String ITEM_DELETE = "Delete";
	
	public EntitySection() {}

	public EntitySection(ADTable table) {
		this.setTable(table);
	}

	public void createContents(IManagedForm form, Composite parent) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(String.format(Message.getString("common.detail"),
				I18nUtil.getI18nMessage(getTable(), "label")));
		section.marginWidth = 3;
		section.marginHeight = 4;
		toolkit.createCompositeSeparator(section);

		createToolBar(section);

		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);	 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);

		createSectionDesc(section);
		createSectionTitle(client);
		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);

	}

	protected void createSectionDesc(Section section) {
	}

	protected void createSectionTitle(Composite client) {
	}

	protected void createSectionContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
//		getTabs().setSelectionBackground(
//						new Color[] { selectedColor,
//								toolkit.getColors().getBackground() },
//						new int[] { 30 });
		getTabs().setSelectionBackground(new Color(null,new RGB(122,168,243)));
		getTabs().setSelectionForeground(new Color(null,new RGB(220,232,252)));
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			EntityForm itemForm = new EntityForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemNew(ToolBar tBar) {
		if (table == null || table.getAuthorityKey() == null || table.getAuthorityKey().trim().length() == 0) {
			itemNew = new ToolItem(tBar, SWT.PUSH);
		} else {
			itemNew = new AuthorityToolItem(tBar, SWT.PUSH, table.getAuthorityKey() + "." + ITEM_NEW);	
		}
		
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	protected void createToolItemSave(ToolBar tBar) {
		if (table == null || table.getAuthorityKey() == null || table.getAuthorityKey().trim().length() == 0) {
			itemSave = new ToolItem(tBar, SWT.PUSH);
		} else {
			itemSave = new AuthorityToolItem(tBar, SWT.PUSH, table.getAuthorityKey() + "." + ITEM_SAVE);	
		}
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		if (table == null || table.getAuthorityKey() == null || table.getAuthorityKey().trim().length() == 0) {
			itemDelete = new ToolItem(tBar, SWT.PUSH);
		} else {
			itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, table.getAuthorityKey() + "." + ITEM_DELETE);	
		}
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	protected void createToolItemRefresh(ToolBar tBar) {
		itemRefresh = new ToolItem(tBar, SWT.PUSH);
		itemRefresh.setText(Message.getString("common.refresh"));
		itemRefresh.setImage(SWTResourceCache.getImage("refresh"));
		itemRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreshAdapter();
			}
		});
	}

	public void refresh() {
		for (Form detailForm : getDetailForms()) {
			detailForm.setObject(getAdObject());
			detailForm.loadFromObject();
		}
		form.getMessageManager().removeAllMessages();
	}

	protected void newAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				setAdObject(createAdObject());
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void saveAdapter() {
		save();
	}

	public boolean save() {
		try {
			form.getMessageManager().setAutoUpdate(false);
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
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					ADManager entityManager = Framework.getService(ADManager.class);
					ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), getAdObject(), Env.getUserRrn());
					setAdObject(entityManager.getEntity(obj));
					UI.showInfo(Message.getString("common.save_successed"));// µ¯³öÌáÊ¾¿ò
					refresh();
					return true;
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return false;
	}

	protected void deleteAdapter() {
		delete();
	}

	public boolean delete() {
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					ADManager entityManager = Framework.getService(ADManager.class);
					entityManager.deleteEntity(getAdObject());
					setAdObject(createAdObject());
					refresh();
					return true;
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
		return false;
	}

	protected void refreshAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			ADBase adBase = getAdObject();
			if (adBase != null && adBase.getObjectRrn() != null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				setAdObject(entityManager.getEntity(adBase));
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
		refresh();
	}

	public void disposeContent() {
		for (Form form : detailForms) {
			form.dispose();
		}
		detailForms = new ArrayList<Form>();
		adObject = null;
		setTable(null);
	}

	public ADBase createAdObject() throws Exception {
		ADBase base = getAdObject().getClass().newInstance();
		base.setOrgRrn(Env.getOrgRrn());
		return base;
	}

	public void setTabs(CTabFolder tabs) {
		this.tabs = tabs;
	}

	public CTabFolder getTabs() {
		return tabs;
	}

	public void setTable(ADTable table) {
		this.table = table;
	}

	public ADTable getTable() {
		return table;
	}

	public void setDetailForms(List<Form> detailForms) {
		this.detailForms = detailForms;
	}

	public List<Form> getDetailForms() {
		return detailForms;
	}

	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
	}

	public ADBase getAdObject() {
		return adObject;
	}

	public void setFocus() {
		return;
	}
}
