package com.graly.erp.internalorder;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.ppm.model.InternalOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class InternalOrderLineProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(InternalOrderLineProperties.class);
	private static final String FieldName_Material = "materialRrn";
	private static final String FieldName_UomId = "uomId";
	private String PREFIX = " movementRrn = '";
	protected boolean flag;
	
	public InternalOrderLineProperties() {
		super();
    }
	
	public InternalOrderLineProperties(EntityBlock masterParent, ADTable table, Object parentObject, boolean flag) {
		super(masterParent, table, parentObject);
		this.flag = flag;
	}
	
	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		// 为物料添加单位监听器
//		addMaterialUomIdListener();
	}
	
	public void refresh() {
		super.refresh();
		InternalOrderLineEntryBlock block = (InternalOrderLineEntryBlock)getMasterParent();
		block.setParenObjectStatusChanged();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemNew(tBar);
		createToolItemSave(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	
	@Override
	public ADBase createAdObject() throws Exception {
		InternalOrderLine outLine = null;
		try {
//			OutLineEntryBlock section = (OutLineEntryBlock)this.getMasterParent();
//			if(section.isEnableByParentObject()) {
//				INVManager invManager = Framework.getService(INVManager.class);			
//				outLine = invManager.newMovementLine((MovementOut)section.getParentObject());
//			} else {
				outLine = new InternalOrderLine();
//			}
//			outLine.setOrgRrn(Env.getOrgRrn());
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return outLine;
	}

	@Override
	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
		if(adObject instanceof InternalOrderLine) {
			InternalOrderLine outLine = (InternalOrderLine)adObject;
//			setStatusChanged(outLine.getLineStatus());
			// 如果已保存则不能修改(用objectRrn不为空判断)
//			if(outLine.getObjectRrn() != null) {
//				if(itemSave != null) {
//					itemSave.setEnabled(false);
//				}
//			}
			setStatusChanged(outLine.getLineStatus());
		} else {
			setStatusChanged("");
		}
	}

//	protected void addMaterialUomIdListener() {
//		IField materialField = getIField(FieldName_Material);
//		materialField.addValueChangeListener(getUomIdrChangedListener());
//	}
	
	private IField getIField(String fieldId) {
		IField f = null;
		for(Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if(f != null) {
				return f;
			}
		}
		return f;
	}
	
	private IValueChangeListener getUomIdrChangedListener(){
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				IField ifield = getIField(FieldName_UomId);
				if(ifield instanceof TextField) {
					Material material = (Material)newValue;
					TextField tf = (TextField)ifield;
					if(material != null) {
						tf.setValue(material.getInventoryUom());
					}else tf.setValue("");
					tf.refresh();
				}
			}
		};
	};
	
	protected void setStatusChanged(String status) {
//		if(InternalOrderLine.LINESTATUS_COMPLETED.equals(status)){
//			itemSave.setEnabled(false);
//		} 
	}
	
	 @Override
	public boolean save() {
		 try {
				form.getMessageManager().setAutoUpdate(false);
				form.getMessageManager().removeAllMessages();
				if (getAdObject() != null) {
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
						InternalOrderLine ioLine = (InternalOrderLine) getAdObject();
						
						if(ioLine.getQty().compareTo(BigDecimal.ZERO) ==0){
							ioLine.setLineStatus(InternalOrderLine.LINESTATUS_COMPLETED);
						}
						ADManager entityManager = Framework.getService(ADManager.class);
						ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), getAdObject(), Env.getUserRrn());
						setAdObject(entityManager.getEntity(obj));
						List<InternalOrderLine> ioLines = entityManager.getEntityList(Env.getOrgRrn(), InternalOrderLine.class, Integer.MAX_VALUE,"ioRrn = "+ioLine.getIoRrn(),null);
						InternalOrder io = new InternalOrder();
						io.setObjectRrn(ioLine.getIoRrn());
						io = (InternalOrder) entityManager.getEntity(io);
						boolean flag =true;
						for(InternalOrderLine ioline:ioLines){
							if(!InternalOrderLine.LINESTATUS_COMPLETED.equals(ioline.getLineStatus())){
								flag = false;
							}
						}
						if(flag){
							io.setDocStatus(InternalOrder.STATUS_COMPLETED);
							entityManager.saveEntity(io, Env.getUserRrn());
						}
						UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
						refresh();
						return true;
					}
				}
				form.getMessageManager().setAutoUpdate(true);
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
			return false;
	}
}
