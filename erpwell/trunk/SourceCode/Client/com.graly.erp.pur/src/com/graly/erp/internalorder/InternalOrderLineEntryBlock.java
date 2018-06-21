package com.graly.erp.internalorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Material;
import com.graly.erp.internalorder.po.InternalOrderVendorQueryDialog;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.ppm.model.InternalOrderLine;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.erp.pur.po.POSection;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.Vendor;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

/**
 * 营运：有完成和取消完成的权限
 * 采购：只能通过生成采购订单来完成内部订货单
 * */
public class InternalOrderLineEntryBlock extends ParentChildEntityBlock {
	private static final Logger logger = Logger.getLogger(InternalOrderLineEntryBlock.class);
	protected ToolItem itemApprove;
	protected ToolItem itemSelectAll;//全选
	protected ToolItem itemPO;//采购计划
	protected ToolItem itemExport;//导出
	protected ToolItem itemPPM;//完成
	protected ToolItem itemCloseCompleted;//取消完成
	protected ToolItem itemGenPO;
	
	protected InternalOrderLine selectPILine;
	protected boolean flag = false;
	protected CheckboxTableViewer tViewer;
	
	protected Button btnSelectAll;
	protected Button btnInvertAll;
	
	public InternalOrderLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable, boolean flag){
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
		//设置tableManager,原因系统框架初始化构造不支持checkbox
		EntityTableManager tableManager = new EntityTableManager(childTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION |SWT.BORDER 
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.HIDE_SELECTION);
		setTableManager(tableManager);
	}
	
//	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
//		super.createMasterPart(managedForm, parent);	
//		refresh();
//		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
//		setParenObjectStatusChanged();
//		changedLineCheckBox();
//	}
	
	private void changedLineCheckBox() {
		if (viewer instanceof CheckboxTableViewer) {
			tViewer = (CheckboxTableViewer) viewer;
			tViewer.addCheckStateListener(new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					boolean isChecked = event.getChecked();
					if(isChecked){
						Table table = tViewer.getTable();
//						Object object = event.getElement();
//						
						for (TableItem item : table.getItems()) {
							Object obj = item.getData();
							if (obj instanceof InternalOrderLine) {
								InternalOrderLine ioLine = (InternalOrderLine)obj;
								BigDecimal qtyWip = ioLine.getQty()!=null?ioLine.getQty():BigDecimal.ZERO;
								if (InternalOrderLine.LINESTATUS_COMPLETED.equals(ioLine.getLineStatus()) || qtyWip.compareTo(BigDecimal.ZERO) == 0) {
									item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
									item.setChecked(false);
								}
							}
						}
					}
				}			
			});
		}
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
	    viewer = getTableManager().createViewer(client, toolkit, 300);
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
	    
		Composite buttonBar = toolkit.createComposite(client);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		GridData gd = new GridData(GridData.FILL_BOTH);
		buttonBar.setLayout(gl);
		buttonBar.setLayoutData(gd);
		btnSelectAll = toolkit.createButton(buttonBar, "全部选择", SWT.PUSH);
		btnSelectAll.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (viewer instanceof CheckboxTableViewer) {
					tViewer = (CheckboxTableViewer) viewer;
					tViewer.setAllChecked(true);
					Table table = tViewer.getTable();
					for (TableItem item : table.getItems()) {
						Object obj = item.getData();
						InternalOrderLine ioLine = (InternalOrderLine) obj;
						BigDecimal qtyWip = ioLine.getQty()!=null?ioLine.getQty():BigDecimal.ZERO;
						if(qtyWip.compareTo(BigDecimal.ZERO)==0 || InternalOrderLine.LINESTATUS_COMPLETED.equals(ioLine.getLineStatus())){
							item.setChecked(false);
						}
					}
				}
			}
			
		});
		btnInvertAll = toolkit.createButton(buttonBar, "反相选择", SWT.PUSH);
		btnInvertAll.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (viewer instanceof CheckboxTableViewer) {
					tViewer = (CheckboxTableViewer) viewer;
					Object[] checkedObjects = tViewer.getCheckedElements();
					tViewer.setAllChecked(true);
					for(Object o : checkedObjects){
						tViewer.setChecked(o, false);
						
					}
				}
			}
			
		});
	    
	    section.setClient(client);
	    createViewAction(viewer);
		refresh();
		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
		setParenObjectStatusChanged();
		changedLineCheckBox();
		compareWithQty();
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
	
	protected void createViewAction(StructuredViewer viewer){
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					Object obj = ((StructuredSelection) event.getSelection()).getFirstElement();
					if(obj instanceof InternalOrderLine) {
						selectPILine = (InternalOrderLine)obj;
					} else {
						selectPILine = null;
					}
					//editAdapter();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				editAdapter();
			}
		});
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemSelectAll(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPPM(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPO(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemGenPO(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemSelectAll(ToolBar tBar) {
		itemSelectAll = new ToolItem(tBar, SWT.PUSH);
		itemSelectAll.setText("全选");
		itemSelectAll.setImage(SWTResourceCache.getImage("export"));
		itemSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectAllAdapter();
			}
		});
	}
	
	protected void createToolItemExport(ToolBar tBar) {
		itemExport = new ToolItem(tBar, SWT.PUSH);
		itemExport.setText(Message.getString("common.export"));
		itemExport.setImage(SWTResourceCache.getImage("export"));
		itemExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter();
			}
		});
	}
	
	protected void createToolItemPPM(ToolBar tBar) {
		itemPPM = new ToolItem(tBar, SWT.PUSH);
		itemPPM.setText("生成预处理临时计划");
		itemPPM.setImage(SWTResourceCache.getImage("approve"));
		itemPPM.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ppmAdapter();
			}
		});
	}

	public void setParenObjectStatusChanged() {
		InternalOrder pi = (InternalOrder)parentObject;
		String status = "";
		if(pi != null && pi.getObjectRrn() != null) {
			status = pi.getDocStatus();			
		}
//		if(flag){
//			itemApprove.setEnabled(false);
//		}
		if(InternalOrder.STATUS_COMPLETED.equals(status)){
			itemPPM.setEnabled(false);
			itemPO.setEnabled(false);
		}
	}
	
	
	protected void setChildObjectStatusChanged() {
		InternalOrderLineProperties page = (InternalOrderLineProperties)this.detailsPart.getCurrentPage();
		page.setStatusChanged(((InternalOrder)parentObject).getDocStatus());
	}
	
	
	
	protected void ppmAdapter() {
		try {
			InternalOrderIdSetDialog orderIdSetDialog = new InternalOrderIdSetDialog(
					null, Display.getCurrent().getActiveShell());
			InternalOrder io = (InternalOrder)parentObject;
			if (orderIdSetDialog.open() == Dialog.OK) {
				List<TpsLinePrepare> tpsLines = new ArrayList<TpsLinePrepare>();
				CheckboxTableViewer checkviewer = (CheckboxTableViewer) this.viewer;
				Object[] os = checkviewer.getCheckedElements();
				if(os.length<1){
					UI.showError("请选中行在进行操作");
					return;
				}
				List<InternalOrderLine> ioLines = new ArrayList<InternalOrderLine>();
				for(Object object : os){
					InternalOrderLine ioLine = (InternalOrderLine) object;
					ioLines.add(ioLine);
				}
				if (os.length != 0) {
					PPMManager ppmManager = Framework
							.getService(PPMManager.class);
					ppmManager.saveTpsLinePrepareFromIO(orderIdSetDialog.getTxtValue(),io,ioLines,Env.getUserRrn());
					
//					ppmManager.savePPMInnerOrders(Env.getOrgRrn(), Env.getUserRrn(), InternalOrder.DOC_TYPE_PPM,InternalOrder.STATUS_APPROVED, canaInnerOrders);
				}
				//io = new InternalOrder();
				ADManager adManager = Framework.getService(ADManager.class);
				io = (InternalOrder) adManager.getEntity(io);
				boolean flag =true;
				for(InternalOrderLine ioline:io.getIoLines()){
					if(!InternalOrderLine.LINESTATUS_COMPLETED.equals(ioline.getLineStatus())){
						flag = false;
					}
				}
				if(flag){
					io.setDocStatus(InternalOrder.STATUS_COMPLETED);
					adManager.saveEntity(io, Env.getUserRrn());
				}
				UI.showInfo("成功生成计划");
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void createToolItemPO(ToolBar tBar) {
		itemPO = new ToolItem(tBar, SWT.PUSH);
		itemPO.setText("转给采购部");
		itemPO.setImage(SWTResourceCache.getImage("approve"));
		itemPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				poAdapter();
			}
		});
	}

		protected void poAdapter() {
		try {
//			InternalOrderIdSetDialog orderIdSetDialog = new InternalOrderIdSetDialog(
//					null, Display.getCurrent().getActiveShell());
			InternalOrder io = (InternalOrder) this.getParentObject();
			CheckboxTableViewer checkviewer = (CheckboxTableViewer) this.viewer;
			Object[] os = checkviewer.getCheckedElements();
			List<InternalOrderLine> selectIOLines = new ArrayList<InternalOrderLine>();
			for (Object o : os) {
				InternalOrderLine innerOrder = (InternalOrderLine) o;
				selectIOLines.add(innerOrder);
			}
			if (os.length != 0) {
				
				PPMManager ppmManager = Framework
						.getService(PPMManager.class);
				ppmManager.saveInternalOrderPoFromIO(io,selectIOLines,Env.getUserRrn());
			}
			//io = new InternalOrder();
			ADManager adManager = Framework.getService(ADManager.class);
			io = (InternalOrder) adManager.getEntity(io);
			boolean flag =true;
			for(InternalOrderLine ioline:io.getIoLines()){
				if(!InternalOrderLine.LINESTATUS_COMPLETED.equals(ioline.getLineStatus())){
					flag = false;
				}
			}
			if(flag){
				io.setDocStatus(InternalOrder.STATUS_COMPLETED);
				adManager.saveEntity(io, Env.getUserRrn());
			}
			UI.showInfo("成功转给采购部");
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	

	/* 在其对应的properties中调用此方法, 会根据parentObject的状态来初始化properties按钮是否可用*/
	public boolean isEnableByParentObject() {
		return true;
	}

	public boolean isViewOnly() {
		return flag;
	}
	
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new InternalOrderLineProperties(this, table, getParentObject(), flag));
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}
	
	protected void selectAllAdapter() {
		try {
			if (viewer instanceof CheckboxTableViewer) {
				CheckboxTableViewer tViewer = (CheckboxTableViewer) viewer;
				tViewer.setAllChecked(true);
				Table table = tViewer.getTable();
				for (TableItem item : table.getItems()) {
					Object obj = item.getData();
					InternalOrderLine ioline = (InternalOrderLine) obj;
					if(ioline.getQty().compareTo(BigDecimal.ZERO)==0){
						item.setChecked(false);
					}
				}
				//refreshItemDelSelect();
			}
			//refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	public void editAdapter(){
		
		MpsStatisticSectionDialog  iqcLineDialog = new MpsStatisticSectionDialog(UI.getActiveShell(), null);
		if(iqcLineDialog.open() == Dialog.OK){
		}
		
	}
	
	protected void createToolItemGenPO(ToolBar tBar) {
		itemGenPO = new ToolItem(tBar, SWT.PUSH);
		itemGenPO.setText("生成采购订单");
		itemGenPO.setImage(SWTResourceCache.getImage("approve"));
		itemGenPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				genPoAdapter();
			}
		});
	}
	
	protected void genPoAdapter() {
	try {
		form.getMessageManager().removeAllMessages();
		InternalOrderVendorQueryDialog vendorDialog = new InternalOrderVendorQueryDialog(form, UI.getActiveShell());
		if(vendorDialog.open() == Dialog.OK) {
			Vendor vendor = vendorDialog.getVendor();
			ADManager adManager = Framework.getService(ADManager.class);
			vendor = (Vendor) adManager.getEntity(vendor);
			InternalOrder innerOrder = (InternalOrder)parentObject;
			CheckboxTableViewer checkTableViewer = (CheckboxTableViewer) this.viewer;
			Object[] checkObjects = checkTableViewer.getCheckedElements();//选中的PILine
			List<InternalOrderLine> piLines = new ArrayList<InternalOrderLine>();
			List<PurchaseOrderLine> poLines = new ArrayList<PurchaseOrderLine>();
			if(checkObjects.length<1){
				UI.showError("请选中PI在进行操作");
				return;
			}
			PurchaseOrder po = new PurchaseOrder();
			po.setDeliveryAddress(POSection.DELIVERY_ADDRESS.get(Env.getOrgRrn()));
			po.setOrgRrn(Env.getOrgRrn());
			po.setPaymentRule11("Y");//默认开具发票
			po.setInvoiceType(PurchaseOrder.INVOICE_TYPE_REGULAR);//发票类型默认为普通发票
			po.setVendor(vendor);
			po.setVendorRrn(vendor.getObjectRrn());
			po.setPiId(innerOrder.getPiNo());
			po.setInternalOrderId(innerOrder.getOrderId());
			po.setWarehouseRrn(151043L);
			po.setWarehouseId("环保-良品");
			setValue(po,vendor);
			long i =0;
			String comments = null;
			StringBuffer sf = new StringBuffer();
			for(Object object : checkObjects){
				InternalOrderLine piLine = (InternalOrderLine) object;
				
				if(comments==null){
					if(piLine.getComments()!=null&&!"".equals(piLine.getComments())){
						comments =piLine.getComments()+";";
						sf.append(comments);
//						sf.append(";");
					}
				}else{
					if(piLine.getComments()!=null&&!"".equals(piLine.getComments())){
						String currentComments = piLine.getComments()+";";
						if(!currentComments.equals(comments)){
							sf.append(currentComments);
						} 
					} 
				}
				
				piLines.add(piLine);
				
				if(!InternalOrderLine.LINESTATUS_APPROVED.equals(piLine.getLineStatus())){
					UI.showError("只有审核状态的PI才能转PO");
					return;
				}
				
				PurchaseOrderLine poLine = new PurchaseOrderLine();
				poLine.setIsActive(true);
				poLine.setOrgRrn(Env.getOrgRrn());
				poLine.setLineNo(i+10);
				poLine.setMaterialRrn(piLine.getMaterialRrn());
				List<Material> materials= adManager.getEntityList(Env.getOrgRrn(), Material.class,Integer.MAX_VALUE,"objectRrn = "+piLine.getMaterialRrn(),null);
				Material material = materials.get(0);
				if("SERIAL".equals(material.getLotType())){
					material = (Material) adManager.getEntity(material);
					material.setLotType("BATCH");
					adManager.saveEntity(material, Env.getUserRrn());
					UI.showError("物料批次为SERIAL类型，系统已置换为BTACH类型，请重新点单.物料编号为:"+material.getMaterialId());
					return;
				}
				
				poLine.setQty(piLine.getQty());
				VDMManager vdmManager = Framework.getService(VDMManager.class);
//				VendorMaterial vm = (VendorMaterial) vdmManager.getPrimaryVendor(poLine.getMaterialRrn());
				VendorMaterial vm = vdmManager.getVendorMaterial(vendor.getObjectRrn(), poLine.getMaterialRrn());
				if(vm ==null || vm.getObjectRrn() ==null){
					UI.showError("不存在物料的供应商");
					return;
				}
				if(!vm.getVendorId().equals(vendor.getVendorId())){
					UI.showError("物料:"+piLine.getMaterialId()+"的供应商与选择的供应商不一致");
					return;
				}
				//设置采购订单的采购人员
				if(po.getPurchaser() ==null || "".equals(po.getPurchaser())){
					po.setPurchaser(vm.getPurchaser());
				}
				
				//设置单价
				if (vm.getLastPrice() != null) {
					poLine.setUnitPrice(vm.getLastPrice());// 带出上次价格
				} else if (vm.getReferencedPrice() != null) {
					poLine.setUnitPrice(vm.getReferencedPrice());
				} else {
					poLine.setUnitPrice(BigDecimal.ZERO);
				}
				//收货时间
				if (vm != null && vm.getLeadTime() != null) {
					BASManager basManager;
					try {
						basManager = Framework.getService(BASManager.class);
						BusinessCalendar prCalendar = basManager.getCalendarByDay(Env.getOrgRrn(), BusinessCalendar.CALENDAR_PURCHASE);
						Date now = Env.getSysDate();
						Date dateStart = prCalendar.findStartOfNextDay(now);
						int leadTime = Integer.parseInt(vm.getLeadTime().toString());
						Date dateEnd = prCalendar.addDay(dateStart, leadTime);
						poLine.setDateEnd(dateEnd);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else{
					poLine.setDateEnd(null);
				}
				
				//V000229“奔泰默认免检
//				if(po.getOrgRrn()==139420L&&(po.getVendorId().equals("V000229")||po.getVendorId().equals("V000556"))){
				if(po.getOrgRrn()==139420L&&(po.getVendorRrn() ==33239853L||po.getVendorRrn() ==64955413L)){
					poLine.setIsInspectionFree(true);
				}
				
				poLine.setDateEnd(piLine.getDateDelivery());
				//单位
				if (vm != null) {
					poLine.setUomId(vm.getMaterial().getInventoryUom());
				} else {
					poLine.setUomId(null);
				}
				//设置行总价
				if (poLine.getQty() == null || poLine.getUnitPrice() == null) {
					poLine.setLineTotal(BigDecimal.ZERO);
				} else {
					poLine.setLineTotal(poLine.getQty().multiply(poLine.getUnitPrice()));
				}
				
				poLines.add(poLine);
			}
			po.setComments(sf.toString());
			if(poLines.size()<1){
				UI.showError("请选中PI在进行操作");
				return;
			}
			PURManager purManager = Framework.getService(PURManager.class);
			po = purManager.savePOLine(po, poLines, Env.getUserRrn());
			po = (PurchaseOrder) adManager.getEntity(po);
			
			for(Object object : checkObjects){
				InternalOrderLine piLine = (InternalOrderLine) object;
				piLine = (InternalOrderLine) adManager.getEntity(piLine);
				piLine.setLineStatus(InternalOrder.STATUS_COMPLETED);
				adManager.saveEntity(piLine, Env.getUserRrn());
			}
			try {
				if (po != null && po.getObjectRrn() != null) {
					ADTable adTable = getADTableOfPOLine();
					po = (PurchaseOrder)adManager.getEntity(po);
					String whereClause = (" poRrn = '" + po.getObjectRrn().toString() + "' ");
					POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), getADTableOfPO(), whereClause, po,
							adTable);
					if (cd.open() == Dialog.CANCEL) {
					}
				}
				refresh();
				InternalOrder io = new InternalOrder();
				io = (InternalOrder) adManager.getEntity(innerOrder);
				boolean flag =true;
				for(InternalOrderLine ioline:io.getIoLines()){
					if(!InternalOrderLine.LINESTATUS_COMPLETED.equals(ioline.getLineStatus())){
						flag = false;
					}
				}
				if(flag){
					io.setDocStatus(InternalOrder.STATUS_COMPLETED);
					adManager.saveEntity(io, Env.getUserRrn());
				}
			} catch(Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				logger.error("Error at POSection : editAdapter() " + e);
			}
		}
	 
	}catch (Exception e) {
		ExceptionHandlerManager.asyncHandleException(e);
		return;
	}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			ADTable adTable = null;
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, "PURPurchaseOrderLine");
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected ADTable getADTableOfPO() {
		try {
			ADTable adTable = null;
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, "PURPurchaseOrder");
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	//小谢要求 0与完成状态不能选中
	protected void compareWithQty() {
		if (viewer instanceof CheckboxTableViewer) {
			CheckboxTableViewer tViewer= (CheckboxTableViewer) viewer;
			Table table = tViewer.getTable();
			for (TableItem item : table.getItems()) {
				Object obj = item.getData();
				InternalOrderLine ioLine = (InternalOrderLine) obj;
				BigDecimal qtyWip = ioLine.getQty()!=null?ioLine.getQty():BigDecimal.ZERO;
				if(qtyWip.compareTo(BigDecimal.ZERO) == 0 || InternalOrderLine.LINESTATUS_COMPLETED.equals(ioLine.getLineStatus())){
					item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
					item.setChecked(false);
				}
			}
		}
	}
	
	public void setValue(PurchaseOrder po, Vendor vendor) {
		String paymentRule1 = vendor.getPaymentRule1();
		String paymentRule2 = vendor.getPaymentRule2();
		String paymentRule3 = vendor.getPaymentRule3();
		String paymentRule4 = vendor.getPaymentRule4();
		String paymentRule5 = vendor.getPaymentRule5();
		String paymentRule6 = vendor.getPaymentRule6();
		String paymentRule7 = vendor.getPaymentRule7();
		String paymentRule8 = vendor.getPaymentRule8();
		String paymentRule9 = vendor.getPaymentRule9();
		String paymentRule10 = vendor.getPaymentRule10();
		String paymentRule11 = vendor.getPaymentRule11();
		String paymentRule12 = vendor.getPaymentRule12();
		String paymentRule13 = vendor.getPaymentRule13();
		String paymentRule14 = vendor.getPaymentRule14();
		String paymentRule15 = vendor.getPaymentRule15();
		String paymentRule16 = vendor.getPaymentRule16();

		po.setPaymentRule1(paymentRule1);
		po.setPaymentRule2(paymentRule2);
		po.setPaymentRule3(paymentRule3);
		po.setPaymentRule4(paymentRule4);
		po.setPaymentRule5(paymentRule5);
		po.setPaymentRule6(paymentRule6);
		po.setPaymentRule7(paymentRule7);
		po.setPaymentRule8(paymentRule8);
		po.setPaymentRule9(paymentRule9);
		po.setPaymentRule10(paymentRule10);
		po.setPaymentRule11(paymentRule11);
		po.setPaymentRule12(paymentRule12);
		po.setPaymentRule13(paymentRule13);
		po.setPaymentRule14(paymentRule14);
		po.setPaymentRule15(paymentRule15);
		po.setPaymentRule16(paymentRule16);
		po.setDeliveryRule(vendor.getShipmentCode());// 带出送货方式

		// 带出是否开具发票、以及发票类型，若为增值税发票则带出增值税率
		if (vendor.getIsIssueInvoice2() != null
				&& vendor.getIsIssueInvoice2().trim().length() > 0) {
			po.setPaymentRule11(vendor.getIsIssueInvoice2());
		} else {
			po.setPaymentRule11(vendor.getIsIssueInvoice() == true ? "Y" : "N");
		}
		if (vendor.getInvoiceType2() != null
				&& vendor.getInvoiceType2().trim().length() > 0) {
			po.setInvoiceType(vendor.getInvoiceType2());
		} else {
			po.setInvoiceType(vendor.getInvoiceType());
		}
		if (vendor.getVatRate2() != null) {
			BigDecimal vatRate = new BigDecimal(String.valueOf(vendor
					.getVatRate2().doubleValue()));
			po.setVatRate(vatRate);
		} else {
			BigDecimal vatRate = new BigDecimal(String.valueOf(vendor
					.getVatRate2().doubleValue()));
			po.setVatRate(vatRate);
		}
	}
	
}
