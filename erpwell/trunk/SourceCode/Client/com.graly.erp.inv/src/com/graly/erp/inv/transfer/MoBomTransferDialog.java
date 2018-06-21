package com.graly.erp.inv.transfer;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.ppm.model.TpsLine;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class MoBomTransferDialog extends TitleAreaDialog {
	private static final Logger logger = Logger.getLogger(MoBomTransferDialog.class);
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 350;
	private static final String TABLE_NAME = "WIPManufactureOrder";
	private static final String WHERE_CLAUSE = " docStatus in ('DRAFTED','APPROVED') ";
	private static final String ORDER_BY = "";
	private ADTable adTable;
	private EntityTableManager tableManager;
	private ManufactureOrder selectedMo;
	private ManufactureOrder toMo;
	private Text textQty;
	
	private BigDecimal qty;

	public MoBomTransferDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		this.setTitle(Message.getString("wip.create_mo_from_tpsline"));
		initAdTableByTableId();
		
		Composite composite = (Composite)super.createDialogArea(parent);		
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());		
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		Composite qtyBody =  toolkit.createComposite(body);
		configureQtyBody(qtyBody);
		Label label = toolkit.createLabel(qtyBody, "数量");
		label.setForeground(SWTResourceCache.getColor("Folder"));
		textQty = toolkit.createText(qtyBody, "", SWT.BORDER);
		GridData gLabel = new GridData();
		gLabel.horizontalAlignment = GridData.FILL;
		gLabel.grabExcessHorizontalSpace = true;

		GridData gText = new GridData();
		gText.widthHint = 200;
		textQty.setLayoutData(gText);
		textQty.setTextLimit(32);
		
		
		
//		
//		
//		Label label = toolkit.createLabel(qtyBody, "请输入数量");
//		label.setSize(112, 55);
//		Text text = toolkit.createText(qtyBody, "123");
//		text.setSize(11, 55);
		toolkit.createSeparator(body, SWT.HORIZONTAL | SWT.SEPARATOR);
		StructuredViewer viewer = createTableViewer(body, toolkit);
		createViewAction(viewer);
		return composite;
	}
	
	public StructuredViewer createTableViewer(Composite parent, FormToolkit toolkit) {
		tableManager = new EntityTableManager(adTable);
		StructuredViewer viewer = tableManager.createViewer(parent, toolkit);

		EntityItemInput input =null;
		if(Env.getOrgRrn() == 139420L){
 
			input = new EntityItemInput(tableManager.getADTable(),
					WHERE_CLAUSE, ORDER_BY);
		}else{
			 input = new EntityItemInput(tableManager.getADTable(), WHERE_CLAUSE, ORDER_BY);
		}
	    
	    viewer.setInput(input);
	    tableManager.updateView(viewer);
		return viewer;
	}
	
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectedTpsLine(ss.getFirstElement());
	    		buttonPressed(IDialogConstants.OK_ID);
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectedTpsLine(ss.getFirstElement());
				} catch (Exception e){
					logger.error("WorkCenterDialog : createViewAction()", e);
				}
			}
	    });
	}
	
	private void setSelectedTpsLine(Object obj) {
		if(obj instanceof ManufactureOrder) {
			selectedMo = (ManufactureOrder)obj;
		} else {
			selectedMo = null;
		}
	}

	@Override
	protected void okPressed() {
		if(selectedMo != null) {
			if(textQty.getText()!=null){
				BigDecimal qty = new BigDecimal(textQty.getText().trim());
				setQty(qty);
				super.okPressed();
			}
		}
	}
	
	private ManufactureOrder parseManufactureOrder(TpsLine tpsLine) {
		if(tpsLine != null) {
			Date date = Env.getSysDate();
			toMo = new ManufactureOrder();
			toMo.setOrgRrn(Env.getOrgRrn());
//			toMo.setIsActive(true);
//			toMo.setCreated(date);
//			toMo.setCreatedBy(Env.getUserRrn());
//			toMo.setUpdated(date);
//			toMo.setUpdatedBy(Env.getUserRrn());
			
			toMo.setDocStatus(ManufactureOrder.STATUS_DRAFTED);
			
			toMo.setMaterialRrn(tpsLine.getMaterialRrn());
			toMo.setMaterial(tpsLine.getMaterial());
			toMo.setUomId(tpsLine.getUomId());
			toMo.setQtyProduct(tpsLine.getQtyTps());
			toMo.setDateStart(date);
			toMo.setDatePlanStart(date);
			toMo.setTpsRrn(tpsLine.getObjectRrn());
			toMo.setMpsId(tpsLine.getTpsId());
			toMo.setComments(tpsLine.getComments());//从临时计划中带入备注信息
			toMo.setOrderId(tpsLine.getOrderId());//从临时计划中带入订单编号信息
			toMo.setSalePlanType(tpsLine.getSalePlanType());//从临时计划中带入销售类型信息
			toMo.setCustomerName(tpsLine.getCustomerName());//从临时计划中带入客户名信息
			toMo.setSaler(tpsLine.getSaler());//从临时计划中带入业务员信息
			toMo.setDateEnd(tpsLine.getDateDelivered());
			toMo.setDatePlanEnd(tpsLine.getDateDelivered());
			toMo.setUserCreated(Env.getUserName());
			toMo.setPiId(tpsLine.getPiId());
			
			if(tpsLine.getIsStockUp()){//如果是备货计划,刚生成的工作令是B开头的
				toMo.setMoType(ManufactureOrder.MOTYPE_B);
			}
		}
		return toMo;
	}
	
	
	public ManufactureOrder getManufactureOrder() {
		return toMo;
	}
	
	protected void initAdTableByTableId() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0L, TABLE_NAME);
		} catch(Exception e) {
			logger.error("TpsLineSelectionDialog : initAdTableByTableId()", e);
		}
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
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
	
	protected void configureQtyBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 5;
		layout.marginRight = 0;
		layout.marginTop = 5;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	}
	
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);;
	}

	public ManufactureOrder getSelectedMo() {
		return selectedMo;
	}

	public void setSelectedMo(ManufactureOrder selectedMo) {
		this.selectedMo = selectedMo;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	
}
