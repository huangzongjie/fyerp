package com.graly.erp.vdm.vendor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.pdm.model.WmsVendor;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.Vendor;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class VendorProperties extends EntityProperties {
	private static final Logger logger = Logger.getLogger(VendorProperties.class);
	private static final String CODETYPE_VENDOR = "VDM";
//	private static String TAB_NAME_COUNTMANAGER = "AccountInfo"; // 是否开具发票
	private static String IsIssueInvoice = "isIssueInvoice"; // 是否开具发票
	private static String InvoiceType = "invoiceType"; // 发票类型
	private static final String VatRate = "vatRate"; // 增值税率(默认0.17)
	private String previousInvoiceType = PurchaseOrder.INVOICE_TYPE_REGULAR;//默认是普通发票
	private String previousVatRate = "0.17";//增值税率(默认0.17)
	
	protected ToolItem viewContract;
	protected static String URL = "http://192.168.0.235:81/system/compact_erp_list.jsp?vender_code=";
	
//	public final static Long orgRrn = 139420L + 12644730L;//奔泰的orgrrn
	public final static Map<Long,long[]> syncOrgMaps = new HashMap<Long,long[]>();
	{
		syncOrgMaps.put(139420L, new long[]{12644730,41673024});
		syncOrgMaps.put(12644730L, new long[]{139420});
		syncOrgMaps.put(41673024L, new long[]{139420});
	}
	
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		
		final IField isInvoiceField = getIField(IsIssueInvoice);
		final IField invoiceTypeField = getIField(InvoiceType);
		final IField vatRateField = getIField(VatRate);
		
		if(isInvoiceField != null && invoiceTypeField != null && vatRateField != null){
			isInvoiceField.addValueChangeListener(new IValueChangeListener(){
				@Override
				public void valueChanged(Object sender, Object newValue) {
					if(newValue == null || newValue instanceof Boolean){
						boolean enabled = (newValue == null ? false : ((Boolean)newValue).booleanValue());
						invoiceTypeField.getControls()[0].setEnabled(enabled);
						if (!enabled){
							vatRateField.getControls()[0].setVisible(false);
							vatRateField.getControls()[1].setVisible(false);
							vatRateField.setValue(null);
							invoiceTypeField.setValue(null);//如果不需要发票则发票类型是空的
						}else{
							invoiceTypeField.setValue(previousInvoiceType);
						}
					}
					checkEnabled();
					invoiceTypeField.refresh();
				}
			});
			
			invoiceTypeField.addValueChangeListener(new IValueChangeListener(){
				@Override
				public void valueChanged(Object sender, Object newValue) {
					boolean flag = false;//是否开具发票
					if(newValue instanceof String){
						if(((String)newValue).trim().length() == 0){
							newValue = null;
						}
					}
					if(isInvoiceField.getValue() == null || isInvoiceField.getValue() instanceof Boolean){
						flag = (isInvoiceField.getValue() == null ? false : ((Boolean)isInvoiceField.getValue()).booleanValue());
					}
					if(newValue != null){
						if(newValue.equals(PurchaseOrder.INVOICE_TYPE_VAT)){
							previousInvoiceType = String.valueOf(newValue);
							vatRateField.getControls()[0].setVisible(true);
							vatRateField.getControls()[1].setVisible(true);
							if(previousVatRate != null)
								vatRateField.setValue(previousVatRate);
						}else if(newValue.equals(PurchaseOrder.INVOICE_TYPE_REGULAR)){
							previousInvoiceType = String.valueOf(newValue);
							vatRateField.getControls()[0].setVisible(false);
							vatRateField.getControls()[1].setVisible(false);
							vatRateField.setValue(null);
						}
					}else{
						if(flag){
							invoiceTypeField.setValue(PurchaseOrder.INVOICE_TYPE_REGULAR);
						}
					}
					checkEnabled();
					vatRateField.refresh();
				}
			});
			
			vatRateField.addValueChangeListener(new IValueChangeListener(){
				@Override
				public void valueChanged(Object sender, Object newValue) {
					if(newValue != null && String.valueOf(newValue).trim().length() != 0){
						previousVatRate = String.valueOf(newValue);
					}
					checkEnabled();
				}
				
			});
		}
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemViewContract(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemViewContract(ToolBar tBar) {
		viewContract = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_BARCODE_IQCLINE);	
		viewContract.setText(Message.getString("common.viewcontract"));
		viewContract.setImage(SWTResourceCache.getImage("viewcontract"));
		viewContract.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				viewContractAdapter();
			}
		});
	}

	protected void viewContractAdapter() {
		ADBase obj = this.getAdObject();
		if(obj == null || obj.getObjectRrn() == null){
			UI.showError("请先选择供应商");
			return;
		}
		Vendor v = (Vendor)obj;
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), URL + v.getVendorId());
		bd.open();
	}

	private IField getIField(String fieldId) {
		IField f = null;
		for (Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return f;
	}
	
	private void checkEnabled() {
		final IField isInvoiceField = getIField(IsIssueInvoice);
		final IField invoiceTypeField = getIField(InvoiceType);
		final IField vatRateField = getIField(VatRate);
		boolean flag1 = false;
		boolean flag2 = false;
		if(isInvoiceField.getValue() == null || isInvoiceField.getValue() instanceof Boolean){
			flag1 = (isInvoiceField.getValue() == null ? false : ((Boolean)isInvoiceField.getValue()).booleanValue());
		}
		
			flag2 = (invoiceTypeField.getValue() == null); 
		
		if(!flag1){
			invoiceTypeField.setEnabled(false);
		} else {
			invoiceTypeField.setEnabled(true);
		}
		
		if(flag2){
			vatRateField.getControls()[0].setVisible(false);
			vatRateField.getControls()[1].setVisible(false);
		}
		
	}
	
	protected void saveAdapter() {
		
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				ADBase oldBase = getAdObject();
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
					Vendor vendor = (Vendor)getAdObject();
					if(vendor.getContractStart() != null && vendor.getContractEnd() != null){
						if(dateCompare(vendor.getContractStart(),vendor.getContractEnd()) > 0){
							UI.showError(Message.getString("vdm.contract_date"));
							return;
						}
					}
					String vendorId = vendor.getVendorId();//供应商编号允许用户手工输入，如果用户没有输入则系统自动生成
					VDMManager vdmManager = Framework.getService(VDMManager.class);
					if(vendorId == null || "".equals(vendorId.trim())){					
						vendorId = vdmManager.generateVendorCode(Env.getOrgRrn(), CODETYPE_VENDOR);
					}
					if(vendorId != null && !"".equals(vendorId.trim())){
						vendor.setVendorId(vendorId);
					}
					ADManager entityManager = Framework.getService(ADManager.class);
					
					//save vendor of this area and synchronize in another area
					//1.save this vendor
					ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), vendor, Env.getUserRrn());
					setAdObject(entityManager.getEntity(obj));
					
					if(Env.getOrgRrn() ==139420L){//WMS供应商
						WmsVendor wmsVendor = new WmsVendor();
						wmsVendor.setOrgRrn(Env.getOrgRrn());
						wmsVendor.setIsActive(true);
						wmsVendor.setSupplierCode(vendor.getVendorId());
						wmsVendor.setSupplierName(vendor.getCompanyName());
						wmsVendor.setErpWrite(BigDecimal.ONE);
						wmsVendor.setErpWriteTime(new Date());
						wmsVendor.setWmsRead(BigDecimal.ZERO);
						entityManager.saveEntity(wmsVendor, Env.getUserRrn());
					}
					
					//2.find vendor in another area by vendor id
					Vendor thisVendor = (Vendor) getAdObject();
					long[] syncOrgs = syncOrgMaps.get(Env.getOrgRrn());
					if(syncOrgs!=null){
						for(long orgRrn : syncOrgs){
							List<Vendor> anotherVendorList = vdmManager.getVendorByVendorId(orgRrn,thisVendor.getVendorId());
							Vendor anotherVendor = null;
							if(anotherVendorList == null || anotherVendorList.size() == 0){
								
							}else{
								anotherVendor = anotherVendorList.get(0);
							}
							anotherVendor = copyVender(thisVendor, anotherVendor, orgRrn);
							entityManager.saveEntity(getTable().getObjectRrn(), anotherVendor, Env.getUserRrn());
						}
					}
					
					UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
					refresh();
				}
				ADBase newBase = getAdObject();
				if (oldBase.getObjectRrn() == null) {
					getMasterParent().refreshAdd(newBase);
				} else {
					getMasterParent().refreshUpdate(newBase);
				}
//				getMasterParent().refresh();
			}			

			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	public Integer dateCompare (Date dateOne, Date dateTwo) {
		Integer result = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateOne = dateFormat.parse(dateFormat.format(dateOne));
			dateTwo = dateFormat.parse(dateFormat.format(dateTwo));
			result = dateOne.compareTo(dateTwo);
			return result;
		} catch (Exception e) {
			logger.error("SetPlanProperties : salePlanDateCompare ", e);
			return result;
		}
	}
	
	private Vendor copyVender(Vendor source, Vendor target, long targetOrgRrn){
		if(target == null){
			try {
				target = (Vendor) source.clone();
				target.setOrgRrn(targetOrgRrn);
			} catch (CloneNotSupportedException e) {
				logger.error("VendorProperties:copyVendor()",e);
			}
		}else{
			target.setVendorType(source.getVendorType());
			target.setShipmentCode(source.getShipmentCode());
			target.setStatus(source.getStatus());
			target.setCompanyName(source.getCompanyName());
			target.setDescription(source.getDescription());
//			target.setContact(source.getContact());
//			target.setPhone1(source.getPhone1());
//			target.setPhone2(source.getPhone2());
			target.setAddress(source.getAddress());
			target.setUrl(source.getUrl());
//			target.setFax(source.getFax());
			target.setZipCode(source.getZipCode());
			target.setCountry(source.getCountry());
			target.setArea(source.getArea());
			target.setTermsCode(source.getTermsCode());
			target.setContractLife(source.getContractLife());
			target.setBankName(source.getBankName());
			target.setAccountId(source.getAccountId());
			target.setComments(source.getComments());
			target.setContractDoc(source.getContractDoc());
			target.setContractStart(source.getContractStart());
			target.setContractEnd(source.getContractEnd());
			target.setIsIssueInvoice(source.getIsIssueInvoice());
			target.setInvoiceType(source.getInvoiceType());
			target.setVatRate(source.getVatRate());
		}
		return target;
	}
}

class BrowserDialog extends TrayDialog{
	protected String url;
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 500;
	
	public BrowserDialog(Shell parentShell, String url) {
		super(parentShell);
		this.url = url;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Browser browser = new Browser(parent,SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
        gd.minimumWidth = MIN_DIALOG_WIDTH ;
        gd.minimumHeight = MIN_DIALOG_HEIGHT ;
        browser.setUrl(url);
        browser.setLayoutData(gd);
		browser.setUrl(url);
		return browser;
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
	protected void createButtonsForButtonBar(Composite parent) {}
	
}