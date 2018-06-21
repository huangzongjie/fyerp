package com.graly.erp.bj.inv.in.createfrom.po;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BJPoLineSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(BJPoLineLotSelectPage.class);
	private static String POLINELOT_NEXT = "poLineLotSelect";
	private static final String PREVIOUS = "poSelect";

	private BJPoCreateWizard wizard;
	private BJPoLineSelectSection section;
	
	public BJPoLineSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (BJPoCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		ADTable adTable = wizard.getContext().getTable(BJCreateContext.TableName_PoLine);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);
		
       // create section
		section = new BJPoLineSelectSection(adTable, this);
		section.createContents(managedForm, composite);
		updateLocalPageContent();
		this.setPageComplete(false);
		setControl(composite);
	}
	
	@Override
	public void refresh() {
		if(section != null) {
			section.setParentPo(wizard.getContext().getPo());
			section.refresh();			
		}
	}

	@Override
	public String doNext() {
		try {
			List<PurchaseOrderLine> lines = section.getSelectedPoLine();
			if(lines != null && lines.size() > 0) {
				if(validate(lines) && validateQty(lines)) {
					//备件收货数 就为采购入库的数量，因为可以多次收货			
					for(PurchaseOrderLine poline :lines){
						poline.setQty(poline.getQtyDelivered());
					}
					wizard.getContext().setPoLines(lines);
					updateNextPage();
					return POLINELOT_NEXT;
				}
			}
		} catch(Exception e) {
			logger.error("PoLineSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return "";		
	}
	
	private boolean validateQty(List<PurchaseOrderLine> lines) {
		for(PurchaseOrderLine poLine : lines) {
			BigDecimal qty = poLine.getQty()!=null ? poLine.getQty() :BigDecimal.ZERO;
			BigDecimal qtyDelivered = poLine.getQtyDelivered() !=null ? poLine.getQtyDelivered() : BigDecimal.ZERO;
			BigDecimal qtyIn = poLine.getQtyIn() !=null ? poLine.getQtyIn() : BigDecimal.ZERO;
			if(qty.compareTo(BigDecimal.ZERO) ==0){
				UI.showError(Message.getString("inv.in_quantity_zero"));
				return false;
			}else if(qtyDelivered.compareTo(BigDecimal.ZERO) <=0){
				UI.showError("收货数不能0或者负数");
				return false;
			}else if(qty.compareTo(qtyDelivered) <0){
				UI.showError("收货数不能大于订单数");
				return false;
			}else if(qtyIn.add(qtyDelivered).compareTo(qty) >0){
				UI.showError(Message.getString("inv.in_larger_than_order"));
				return false;
			}
		}
		return true;
	}
	
	private boolean validate(List<PurchaseOrderLine> lines) {
		Map<Long, PurchaseOrderLine> poLineMap = new HashMap<Long, PurchaseOrderLine>();
		for(PurchaseOrderLine poLine : lines) {
			if (!poLineMap.containsKey(poLine.getWarehouseRrn())) {
				poLineMap.put(poLine.getWarehouseRrn(), poLine);
			}
			Material material = poLine.getMaterial();
			if(material == null || !material.getIsLotControl()) {
				this.setErrorMessage(String.format(Message.getString("inv.material_is_not_control_by_lot"),
						poLine.getMaterialId()));
				return false;
			}
		}
		if(poLineMap.size() > 1){
			this.setErrorMessage(Message.getString("inv.different_warehouse"));
			return false;
		}
		return true;
	}
	
	public void updateLocalPageContent() {
		this.refresh();
	}
	
	public void updateNextPage() {
		((BJPoLineLotSelectPage)wizard.getPage(POLINELOT_NEXT)).updateLocalPageContent();
	}

	@Override
	public boolean canFlipToNextPage() {
        return isPageComplete();
    }
	
	public String doPrevious() {
		this.setErrorMessage(null);
		return PREVIOUS;
	}
	
	public IWizardPage getPreviousPage() {
		return wizard.getPage(PREVIOUS);
	}
}
