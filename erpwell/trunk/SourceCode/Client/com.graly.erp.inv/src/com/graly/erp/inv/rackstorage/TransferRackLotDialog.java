package com.graly.erp.inv.rackstorage;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.internal.forms.MessageManager;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.erp.inv.model.RackLotStorage;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.util.ADFieldUtil;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.model.Lot;

public class TransferRackLotDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(TransferRackLotDialog.class);
	
	protected static String REF_RACK_TABLE = "RackRefList";
	private static int MIN_DIALOG_WIDTH = 100;
	private static int MIN_DIALOG_HEIGHT = 150;
	protected RackLotStorage sourceLot;

	private RefTableField sourceRackField;
	private RefTableField targetRackField;
	private TextField qtyField;
	
	private INVManager invManager;
	private ADManager adManager;
	
	private Long srcRackRrn = null;
	private Long tarRackRrn = null;
	private BigDecimal qty = null;
	
	public TransferRackLotDialog(Shell shell, RackLotStorage sourceLot, INVManager invManager) {
		super(shell);
		this.sourceLot = sourceLot;
		this.invManager = invManager;
	}
	

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
        setTitleMessage();
        Composite composite = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		createContent(toolkit, body);
        return composite;
	}
	
	private void createContent(FormToolkit toolkit, Composite parent) {
		try {
			Composite body = toolkit.createComposite(parent);
			GridLayout gl = new GridLayout();
			gl.numColumns =2;
			body.setLayout(gl);
			body.setLayoutData(new GridData(GridData.FILL_BOTH));
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			ADManager entityManager = Framework.getService(ADManager.class);
			
			List<ADRefList> refList = entityManager.getADRefList(Env.getOrgRrn(), REF_RACK_TABLE);
			for (ADRefList listItem : refList){
				map.put(listItem.getValue(), listItem.getKey());
			}
			
			ADRefTable refTable = new ADRefTable();
			refTable = (ADRefTable)entityManager.getADRefTable(Env.getOrgRrn(), REF_RACK_TABLE);
			refTable.setWhereClause(StringUtil.pareseWhereClause(refTable.getWhereClause()));
			ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
			TableListManager tableManager = new TableListManager(adTable);
			TableViewer v1 = (TableViewer)tableManager.createViewer(UI.getActiveShell(), new FormToolkit(UI.getActiveShell().getDisplay()));
			TableViewer v2 = (TableViewer)tableManager.createViewer(UI.getActiveShell(), new FormToolkit(UI.getActiveShell().getDisplay()));
			if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
					|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
				List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
				String className = adTable.getModelClass();
				list.add((ADBase)Class.forName(className).newInstance());
				v1.setInput(list);
				v2.setInput(list);
			}
			sourceRackField = ADFieldUtil.createRefTableFieldComboReadOnly("srcRackRrn", "源货架", v1, refTable);
			targetRackField = ADFieldUtil.createRefTableFieldCombo("tarRackRrn", "目标货架", v2, refTable);
			qtyField = ADFieldUtil.createText("qtyTrf", "调整数量", 5);
			
			sourceRackField.createContent(body, toolkit);
			targetRackField.createContent(body, toolkit);
			qtyField.createContent(body, toolkit);
			
			sourceRackField.setEnabled(false);
			sourceRackField.setValue(sourceLot.getRackRrn());
			sourceRackField.refresh();
			
		} catch (Exception e) {
			logger.error("TransferRackLotDialog : createContent", e);
		}
	}


	protected void setTitleMessage() {
		setTitle("货架批次调整");
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
    	createButton(parent, IDialogConstants.OK_ID,
    			Message.getString("common.ok"), false);
    	createButton(parent, IDialogConstants.CANCEL_ID,
    			Message.getString("common.exit"), false);
    }
	
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected void okPressed() {
		if(sourceRackField != null){
			Object o = sourceRackField.getValue();
			if(o=="" && o==null){
				warning(1);
				return;
			}else{
				srcRackRrn = (Long) o;
			}
		}
		if(targetRackField != null){
			Object o = targetRackField.getValue();
			if(o==""||o==null){
				warning(2);
				return;
			}else{
				tarRackRrn = Long.valueOf((String) o);
			}
			
		}
		if(qtyField != null){
			Object o = qtyField.getValue();
			if(o=="" || o==null){
				warning(3);
				return;
			}else{
				qty = new BigDecimal((String)o);
			}
		}
		
		doTransfer(srcRackRrn,tarRackRrn,qty);
	}
	
	private void warning(int a){
		if(a==1){
			UI.showWarning("源货架不能为空！");
		}
		if(a==2){
			UI.showWarning("目标货架不能为空！");
		}if(a==3){
			UI.showWarning("调整数量不能为空！");
		}
	}
	protected void doTransfer(long srcRackRrn, long tarRackRrn, BigDecimal qty){
		try {
			if(invManager == null){
				invManager = Framework.getService(INVManager.class);
			}
			if(adManager == null){
				adManager = Framework.getService(ADManager.class);
			}
			
			//调整出
			RacKMovementLot rLot = new RacKMovementLot();
			rLot.setLotId(sourceLot.getLotId());
			rLot.setWarehouseRrn(sourceLot.getWarehouseRrn());
			rLot.setRackRrn(srcRackRrn);
			rLot.setQty(qty);
			rLot.setMovementType(RacKMovementLot.MOVEMENT_TYPE_TRF);
			rLot.setIoType(RacKMovementLot.IO_TYPE_OUT);
			
			rLot = invManager.saveRacKMovementLot(Env.getOrgRrn(), rLot, Env.getUserRrn(),true);
			
			//调整入
			RacKMovementLot tLot = new RacKMovementLot();
			tLot.setLotId(sourceLot.getLotId());
			tLot.setWarehouseRrn(sourceLot.getWarehouseRrn());
			tLot.setRackRrn(tarRackRrn);
			tLot.setQty(qty);
			tLot.setMovementType(RacKMovementLot.MOVEMENT_TYPE_TRF);
			tLot.setIoType(RacKMovementLot.IO_TYPE_IN);
			
			tLot = invManager.saveRacKMovementLot(Env.getOrgRrn(), tLot, Env.getUserRrn(),true);
			
			UI.showInfo(Message.getString("common.save_successed"));
		} catch (Exception e) {
			logger.error("TransferRackLotDialog : doTransfer()", e);
		}
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
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
}
