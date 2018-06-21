package com.graly.framework.base.entitymanager.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.application.Activator;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.ManagerDialogExtensionPoint;
import com.graly.framework.base.ui.ManagerExtensionPoint;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.PropertyUtil;

public class ParentChildEntityBlock extends EntityBlock {

	private static final Logger logger = Logger.getLogger(ParentChildEntityBlock.class);
	
	protected ADTable parentTable;
	protected Object parentObject;
	private CTabFolder tabs;
	private List<Form> detailForms = new ArrayList<Form>();
	
	public ParentChildEntityBlock(ADTable parentTable, Object parentObject, String whereClause, ADTable childTable){
		super(new EntityTableManager(childTable));
		this.parentTable = parentTable;
		this.parentObject = parentObject;
		this.setWhereClause(whereClause);
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		this.form = managedForm;
		FormToolkit toolkit = managedForm.getToolkit();
		section = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		section.setText(I18nUtil.getI18nMessage(parentTable, "label"));
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout layout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(layout);
	    
	    section.setData("entityBlock", this);
	    spart = new SectionPart(section);    
	    managedForm.addPart(spart);
    
	    createToolBar(section);
	    createParentContent(client);
	    
		final ADTable table = getTableManager().getADTable();
	    viewer = getTableManager().createViewer(client, toolkit, 100);
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try{
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase)obj).setOrgRrn(Env.getOrgRrn());
						}
						managedForm.fireSelectionChanged(spart, new StructuredSelection(new Object[] {obj}));
					} catch (Exception e){
						e.printStackTrace();
					}
				} else {
					managedForm.fireSelectionChanged(spart, event.getSelection());
				}
			} 
		});
	    EntityItemInput input = new EntityItemInput(getTableManager().getADTable(), getWhereClause(), "");
	    viewer.setInput(input);
	   
	    getTableManager().updateView(viewer);
	    section.setClient(client);
	    createViewAction(viewer);
	}
	
	
	protected void createParentContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : parentTable.getTabs()) {
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
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try{
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			if (ManagerDialogExtensionPoint.getManager(table.getModelClass()) != null){
				EntityProperties page = ManagerDialogExtensionPoint.getManager(table.getModelClass());
				page.setTable(table);
				page.setMasterParent(this);
				((ChildEntityProperties)page).setParentObject(getParentObject());
				detailsPart.registerPage(klass, page);
			} else if (ManagerExtensionPoint.getManagerRegistry().get(table.getModelClass()) != null){
				EntityProperties page = ManagerExtensionPoint.getManagerRegistry().get(table.getModelClass());
				page.setTable(table);
				page.setMasterParent(this);
				if (page instanceof ChildEntityProperties) {
					((ChildEntityProperties)page).setParentObject(getParentObject());
					detailsPart.registerPage(klass, page);
				}
			} else {
				detailsPart.registerPage(klass, new ChildEntityProperties(this, table, getParentObject()));
			}
		} catch (Exception e){
			logger.error("ChildEntityBlock : registerPages ", e);
		}
	}
	
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		super.createToolBarActions(managedForm);
		sashForm.setWeights(new int[]{6, 4});
	}
	
	public boolean saveParent() {
		try {
			boolean saveFlag = true;
//			form.getMessageManager().removeAllMessages();
			if (getParentObject() != null) {
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getParentObject(), detailForm
								.getObject(), detailForm.getFields());
					}
				}
			}
			return saveFlag;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}
	
	@Override
	protected long createSectionDesc(Section section){
		return 0;
	}
	
	public void refresh() {
		for (Form detailForm : getDetailForms()) {
			detailForm.setObject(getParentObject());
			detailForm.loadFromObject();
		}
		super.refresh();
	}
	
	@Override
	public void createToolBar(Section section) {
	}

	public Object getParentObject() {
		return parentObject;
	}

	public void setTabs(CTabFolder tabs) {
		this.tabs = tabs;
	}

	public CTabFolder getTabs() {
		return tabs;
	}

	public void setDetailForms(List<Form> detailForms) {
		this.detailForms = detailForms;
	}

	public List<Form> getDetailForms() {
		return detailForms;
	}

	public void setParentObject(Object parentObject) {
		this.parentObject = parentObject;
	}
}