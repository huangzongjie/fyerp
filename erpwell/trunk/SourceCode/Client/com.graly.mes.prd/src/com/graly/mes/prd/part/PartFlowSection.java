package com.graly.mes.prd.part;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.runtime.Framework;
import com.graly.mes.prd.model.Part;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;

public class PartFlowSection extends EntitySection implements IValueChangeListener{
	
	private static final Logger logger = Logger.getLogger(PartFlowSection.class);

	protected Text text;
	protected IField partRefTableField;
	protected PartFlowForm itemForm;

	protected final static String VERSION = Message.getString("common.versionshow");
	private static final String FIELD_ID_PARTNAME = "partName";
	private ADField partNameField;
	
	public PartFlowSection(ADTable table) {
		super(table);
	}

	@Override
	public void createContents(IManagedForm form, Composite parent) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.NO_TITLE);
		section.marginWidth = 0;
		section.marginHeight = 0;

		
		for(ADTab tab : table.getTabs()) {
			for(ADField adField : tab.getFields()) {
				if(FIELD_ID_PARTNAME.equals(adField.getName())) {
					partNameField = adField;
				}
			}
		}	
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		section.setLayout(layout);
		
		TableWrapData td = new TableWrapData(TableWrapData.CENTER, TableWrapData.MIDDLE);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);

		createSectionTitle(client);
		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);
	}

	public void initAdObject() {
		Part part = new Part();
		part.setVersion(0L);
		setAdObject(part);
		refresh();
	}

	@Override
	protected void createSectionTitle(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(3, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, Message.getString("wip.part"));
		label.setForeground(SWTResourceCache.getColor("Folder"));		

		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADRefTable refTable = new ADRefTable();
			refTable.setObjectRrn(partNameField.getReftableRrn());
			refTable = (ADRefTable)entityManager.getEntity(refTable);
			ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
			
			TableListManager tableManager = new TableListManager(adTable);
			TableViewer viewer = (TableViewer)tableManager.createViewer(Display.getCurrent().getActiveShell(), 
					new FormToolkit(Display.getCurrent().getActiveShell().getDisplay()));
			if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
					|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
				List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), 
						adTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
				viewer.setInput(list);
			}
			int mStyle = SWT.READ_ONLY | SWT.BORDER;
			partRefTableField = new RefTableField("", viewer, refTable, mStyle);
			partRefTableField.setLabel(null);  //
			partRefTableField.addValueChangeListener(this);
			partRefTableField.createContent(top, toolkit);
		} catch (Exception e1) {
			logger.error("PartFlowSection : creatSectionTitle",e1);
		}	
	}


	@Override
	public void setFocus() {
	}

	@Override
	protected void createSectionContent(Composite client) {
		final IMessageManager mmng = form.getMessageManager();
		itemForm = new PartFlowForm(client, SWT.NONE, table, mmng);
		itemForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		getDetailForms().add(itemForm);
	}
	
	public List<Node> getFlowList() {
		return itemForm.getFlowList();
	}

	@Override
	public void valueChanged(Object sender, Object newValue) {
		Part part = null;
		part = searchPart(Long.valueOf((String)newValue));
		if (part == null) {
			initAdObject();
		} else {
			setAdObject(part);
		}
		refresh();
	}
	
	private Part searchPart(Long partRrn) {
		try {			
			if(partRrn != null) {
				Part part = new Part();
				part.setObjectRrn(partRrn);
				ADManager entityManager = Framework.getService(ADManager.class);
				return (Part)entityManager.getEntity(part);
			}			
		} catch (Exception e) {
			logger.error("PartFlowSection searchLotEntity(): Lot isn' t exsited!");
		}
		return null;
	}
}
