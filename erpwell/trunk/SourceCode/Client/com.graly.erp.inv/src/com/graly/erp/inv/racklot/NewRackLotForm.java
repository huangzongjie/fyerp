package com.graly.erp.inv.racklot;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class NewRackLotForm extends EntityForm {
	private static final Logger logger = Logger.getLogger(NewRackLotForm.class);
	public static final String TABLE_NAME = "VUserWarehouse";
	private static final String WHERE_CLAUSE = " userRrn = " + Env.getUserRrn() + " AND (isVirtual = 'N' OR isVirtual is null) ";
	protected ADTable whTable; //”√ªß≤÷ø‚ADTable
	public static final String KEY = "objectRrn";
	public static final String VALUE = "warehouseId";
	
	protected RefTableField inWhField;
	
	protected Text txtLotId;
	protected Lot lot;
	
	public NewRackLotForm(Composite parent, int style, ADTab tab,
			IMessageManager mmng) {
		super(parent, style, tab, mmng);
	}

	public NewRackLotForm(Composite parent, int style, ADTable table,
			IMessageManager mmng) {
		super(parent, style, table, mmng);
	}

	public NewRackLotForm(Composite parent, int style, Object object,
			ADTab tab, IMessageManager mmng) {
		super(parent, style, object, tab, mmng);
	}

	public NewRackLotForm(Composite parent, int style, Object object,
			ADTable table, IMessageManager mmng) {
		super(parent, style, object, table, mmng);
	}

	public NewRackLotForm(Composite parent, int style, Object object,
			IMessageManager mmng) {
		super(parent, style, object, mmng);
	}

	@Override
	public Composite getFormBody() {
		Composite body = super.getFormBody();
		try {
			body.setLayout(new GridLayout(1, false));
			body.setLayoutData(new GridData(GridData.FILL_BOTH));
			Composite bodyHeader = toolkit.createComposite(body);
			GridLayout lt = new GridLayout();
			lt.numColumns = 3;
			bodyHeader.setLayout(lt);
			
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			bodyHeader.setLayoutData(gd);
			createLotInputComposite(bodyHeader);
			createWarehouseContent(bodyHeader);
			
			Composite bodyContent = toolkit.createComposite(body, SWT.BORDER);
			bodyContent.setLayout(new GridLayout(2, false));
			bodyContent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			return toolkit.createComposite(bodyContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}
	
	protected void createLotInputComposite(Composite client) {
		Composite comp = toolkit.createComposite(client, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = toolkit.createLabel(comp, Message.getString("inv.lotid"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		label.setFont(SWTResourceCache.getFont("Verdana"));
		txtLotId = toolkit.createText(comp, "", SWT.BORDER);
		txtLotId.setTextLimit(48);
		GridData gd = new GridData();//GridData.FILL_HORIZONTAL
		gd.heightHint = 13;
		gd.widthHint = 170;
		txtLotId.setLayoutData(gd);
		txtLotId.addKeyListener(getKeyListener());
		txtLotId.setFocus();
	}
	
	protected void createWarehouseContent(Composite parent) throws Exception {		
		if(whTable == null) whTable = this.getADTableBy(TABLE_NAME);
		ADRefTable refTable = new ADRefTable();
		refTable.setKeyField(KEY);
		refTable.setValueField(VALUE);
		refTable.setTableRrn(whTable.getObjectRrn());
		refTable.setWhereClause(WHERE_CLAUSE);
		TableListManager tableManager = new TableListManager(whTable);
		TableViewer viewer = (TableViewer)tableManager.createViewer(UI.getActiveShell(), toolkit);
		ADManager adManager = Framework.getService(ADManager.class);
		if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
				|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), whTable.getObjectRrn(),
					Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
			viewer.setInput(list);
		}
		inWhField = new RefTableField("warehouseRrn", viewer, refTable, SWT.READ_ONLY);
		inWhField.setLabel(Message.getString("inv.warehouse_id") + "*");
		inWhField.createContent(parent, toolkit);
	}
	
	protected KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					txtLotId.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR :
						findLot();
						break;
					case SWT.TRAVERSE_RETURN :
						findLot();
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at LotMasterSection £∫getKeyListener() ", e);
		}
		return null;
	}
	
	protected void findLot(){
		String lotId = txtLotId.getText();
		try {
			if(lotId != null && !"".equals(lotId)) {				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				if(getObject() != null && getObject() instanceof RacKMovementLot){
					RacKMovementLot rLot = (RacKMovementLot)getObject();
					rLot.setLotRrn(lot.getObjectRrn());
					rLot.setLotId(lot.getLotId());
					rLot.setMaterialRrn(lot.getMaterialRrn());
					rLot.setMaterialId(lot.getMaterialId());
					rLot.setMaterialName(lot.getMaterialName());
					
					setObject(rLot);
					loadFromObject();
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection : findLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	protected ADTable getADTableBy(String tableName) throws Exception {
		ADTable adTable = null;
		ADManager adManager = Framework.getService(ADManager.class);
		adTable = adManager.getADTable(0L, tableName);
		return adTable;
	}
}
