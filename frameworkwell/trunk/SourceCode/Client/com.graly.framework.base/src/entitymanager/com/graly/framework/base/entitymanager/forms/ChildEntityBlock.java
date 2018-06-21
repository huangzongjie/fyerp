package com.graly.framework.base.entitymanager.forms;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.application.Activator;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.base.ui.ManagerDialogExtensionPoint;
import com.graly.framework.base.ui.ManagerExtensionPoint;

public class ChildEntityBlock extends EntityBlock {

	private static final Logger logger = Logger.getLogger(ChildEntityBlock.class);
	
	protected Object parentObject;
	
	public ChildEntityBlock(EntityTableManager tableManager, String whereClause, Object parentObject){
		super(tableManager);
		this.setWhereClause(whereClause);
		this.parentObject = parentObject;
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
	public void createToolBar(Section section) {
	}

	public void setParentObject(Object parentObject) {
		this.parentObject = parentObject;
		IDetailsPage page = this.detailsPart.getCurrentPage();
		if (page instanceof ChildEntityProperties) {
			((ChildEntityProperties)page).setParentObject(getParentObject());
		}
	}

	public Object getParentObject() {
		return parentObject;
	}
}