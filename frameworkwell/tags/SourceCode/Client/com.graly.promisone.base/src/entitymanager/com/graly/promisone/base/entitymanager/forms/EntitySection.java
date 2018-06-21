package com.graly.promisone.base.entitymanager.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADTab;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.ui.forms.Form;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.base.ui.util.UI;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.promisone.security.model.ADOrg;

import com.graly.promisone.security.client.SecurityManager;

public abstract class EntitySection {
	private static final Logger logger = Logger.getLogger(EntitySection.class);

	protected IManagedForm form;
	public ADBase adObject;
	protected CTabFolder tabs;
	protected List<Form> detailForms = new ArrayList<Form>();
	protected ADTable table;
	protected ToolItem itemNew;
	protected ToolItem itemSave;
	protected ToolItem deleteItem;
	protected ToolItem refreshItem;
	protected Section section;

	public EntitySection() {
	}

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
		section.marginHeight = 5;
		toolkit.createCompositeSeparator(section);

		createToolBar(section);

		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);

		/*
		 * final Form sform = toolkit.createForm(client);
		 * sform.addMessageHyperlinkListener( new HyperlinkAdapter() { public
		 * void linkActivated(HyperlinkEvent e) { String title = e.getLabel();
		 * Object href = e.getHref(); Point hl = ((Control)
		 * e.widget).toDisplay(0, 0); hl.x += 10; hl.y += 10;
		 * 
		 * Shell shell = new Shell(sform.getShell(), SWT.ON_TOP | SWT.TOOL);
		 * shell.setImage(getImage(sform.getMessageType()));
		 * shell.setText(title); shell.setLayout(new FillLayout()); FormText
		 * text = toolkit.createFormText(shell, true); configureFormText(sform,
		 * text); if (href instanceof IMessage[])
		 * text.setText(createFormMessageContent((IMessage[]) href), true,
		 * false); shell.setLocation(hl); shell.pack(); shell.open(); } } );
		 */

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
		Color selectedColor = toolkit.getColors()
				.getColor(FormColors.SEPARATOR);
		getTabs()
				.setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
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
		itemNew = new ToolItem(tBar, SWT.PUSH);
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
		itemSave = new ToolItem(tBar, SWT.PUSH);
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
		deleteItem = new ToolItem(tBar, SWT.PUSH);
		deleteItem.setText(Message.getString("common.delete"));
		deleteItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		deleteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	protected void createToolItemRefresh(ToolBar tBar) {
		refreshItem = new ToolItem(tBar, SWT.PUSH);
		refreshItem.setText(Message.getString("common.refresh"));
		refreshItem.setImage(SWTResourceCache.getImage("refresh"));
		refreshItem.addSelectionListener(new SelectionAdapter() {
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
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					ADManager entityManager = Framework.getService(ADManager.class);
					ADBase obj;
					if(getAdObject() instanceof ADOrg){
						SecurityManager securityManager = Framework.getService(SecurityManager.class);
						obj = securityManager.saveOrg((ADOrg) getAdObject(), Env.getUserId());
					} else {
						obj = entityManager.saveEntity(getTable().getObjectId(), getAdObject(), Env.getUserId());
					}
					setAdObject(entityManager.getEntity(obj));
					UI.showInfo(Message.getString("common.save_successed"));// µ¯³öÌáÊ¾¿ò
					refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void deleteAdapter() {
		try {
			boolean confirmDelete = UI.showConfirm(Message
					.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectId() != null) {
					ADManager entityManager = Framework
							.getService(ADManager.class);
					entityManager.deleteEntity(getAdObject());
					setAdObject(createAdObject());
					refresh();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}

	protected void refreshAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			ADBase adBase = getAdObject();
			if (adBase != null && adBase.getObjectId() != null) {
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
		base.setOrgId(Env.getOrgId());
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
