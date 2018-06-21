package com.graly.erp.internalorder;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.CodeDoc;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.product.client.CANAManager;
import com.graly.erp.product.model.CanaInnerOrder;
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class InternalOrderQueryDialog extends SingleEntityQueryDialog {
	private static final Logger logger = Logger.getLogger(InternalOrderQueryDialog.class);
	protected CanaInnerOrder so;

	protected Section section;
	protected IManagedForm managedForm;
	
	public InternalOrderQueryDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style) {
		super(listTableManager, managedForm, whereClause, style);
		this.managedForm= managedForm;
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		section = toolkit.createSection(parent, sectionStyle);
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);

		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    section.setClient(client);
	    
	}
	@Override
    protected Control createDialogArea(Composite parent) {
		createContents(managedForm,  parent, 0);
		return super.createDialogArea(parent);
	}
	
	
	@Override
	protected void getInitSearchResult() {
		List<CanaInnerOrder> l = null;
		try {
			CANAManager canaManager = Framework.getService(CANAManager.class);
			//l = canaManager.getCanaInnerOrderList(Integer.MAX_VALUE, "", "");
			l = canaManager.getDisCanaInnerOrderList(Integer.MAX_VALUE, "", "");
			if(l!=null && l.size() >0 ){
				for(CanaInnerOrder io : l){
					List<CanaInnerOrder> ioasc = canaManager.getCanaInnerOrderList(1, "serialNumber='"+io.getSerialNumber()+"'", "selfField1");
					if(ioasc!=null && ioasc.size() >0 ){
						io.setSelfField1(ioasc.get(0).getSelfField1());
					}
				}
			}

		} catch (Exception e) {
		}
		tableViewer.setInput(l);
		listTableManager.updateView(tableViewer);
	}
	
	protected void refresh(boolean clearFlag) {
		StringBuffer temp = new StringBuffer("");
		if(getKeys() != null && !"".equals(getKeys().trim())) {
			temp.append(getKeys());
		}
		List<CanaInnerOrder> l = null;
		try {
			CANAManager canaManager = Framework.getService(CANAManager.class);
			l = canaManager.getDisCanaInnerOrderList(Integer.MAX_VALUE, temp.toString(), " serialNumber asc ");
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	logger.error("Error SaleOrderSingleQueryDialog : refresh() " + e.getMessage(), e);
        }
        tableViewer.setInput(l);		
		listTableManager.updateView(tableViewer);
	}
	
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID) {
			if(tableViewer.getTable().getSelection().length > 0) {
				TableItem ti = tableViewer.getTable().getSelection()[0];
				so = (CanaInnerOrder)ti.getData();
			}
			String serialNumber = so.getSerialNumber();
			if(serialNumber == null || "".equals(serialNumber.trim())) 
				return;
			InternalOrder io = getInnerOrderFromCanaIO(so.getSerialNumber());
			if(io == null) return;
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	protected void setTitleInfo() {
		setTitle("内部订单转ERP流程");
        setMessage("请选中内部订单后,进行下一步操作");
	}
	
	protected InternalOrder getInnerOrderFromCanaIO(String serialNumber) {
		InternalOrder internalOrder = null;
		try {
			Map<String, String> ma = CodeDoc.docMap;
			CANAManager canaManager = Framework.getService(CANAManager.class);
			internalOrder = canaManager.createInternalOrderFromCanaIO(Env.getOrgRrn(), serialNumber, Env.getUserRrn());
		} catch(Exception e) {
			logger.error("SaleOrderQueryDialog : getMovementOutBySaleOrder()", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return internalOrder;
	}
}
