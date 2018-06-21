package com.graly.erp.inv.out;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.po.model.PurchaseOrder;
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

public class OutLineProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(OutLineProperties.class);
	private static final String FieldName_Material = "materialRrn";
	private static final String FieldName_UomId = "uomId";
	private String PREFIX = " movementRrn = '";
	protected boolean flag;
	
	public OutLineProperties() {
		super();
    }
	
	public OutLineProperties(EntityBlock masterParent, ADTable table, Object parentObject, boolean flag) {
		super(masterParent, table, parentObject);
		this.flag = flag;
	}
	
	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		// 为物料添加单位监听器
		addMaterialUomIdListener();
	}
	
	public void refresh() {
		super.refresh();
		OutLineEntryBlock block = (OutLineEntryBlock)getMasterParent();
		block.setParenObjectStatusChanged();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemNew(tBar);
//		createToolItemSave(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				OutLineEntryBlock block = (OutLineEntryBlock)getMasterParent();
				if(!block.saveParent()) {
					form.getMessageManager().setAutoUpdate(true);
					return;   // 判断保存父对象是否成功，否则返回
				}
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					MovementLine line = (MovementLine)getAdObject();
					MovementOut mm = (MovementOut)block.getParentObject();
					// 获得该Line对应的MovementLineLots
					line.setMovementLots(getLineLotList(mm, line));

					// 调用savePOLine()方法
					INVManager invManager = Framework.getService(INVManager.class);
					line = invManager.saveMovementOutLine(mm, line, MovementOut.OutType.SOU, Env.getUserRrn());
					// 刷新父子对象，并更新父对象的whereClause
					ADManager adManager = Framework.getService(ADManager.class);
					mm.setObjectRrn(line.getMovementRrn());
					block.setParentObject(adManager.getEntity(mm));
					this.setAdObject(adManager.getEntity(line));  //adManager.getEntity(line)
					getMasterParent().setWhereClause(PREFIX + mm.getObjectRrn() + "' ");
					// 提示保存成功
					UI.showInfo(Message.getString("common.save_successed"));
					refresh();
					this.getMasterParent().refresh();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected List<MovementLineLot> getLineLotList(MovementOut out, MovementLine line) throws Exception{
		List<MovementLineLot> linelots = null;
		if(out.getObjectRrn() == null || line.getObjectRrn() == null)
			return linelots;
		String whereClause = " movementRrn = " + out.getObjectRrn()
							+ " AND movementLineRrn = " + line.getObjectRrn() + " ";
    	ADManager manager = Framework.getService(ADManager.class);
    	linelots = manager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,
        		Integer.MAX_VALUE, whereClause, null);
		return linelots;
	}
	
	protected void deleteAdapter() {
		if(!PurchaseOrder.STATUS_DRAFTED.equals( ((MovementLine)getAdObject()).getLineStatus()) ) {
			return;
		}
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.deleteMovementOutLine((MovementLine)getAdObject(), Env.getUserRrn());
					// 刷新父对象
					OutLineEntryBlock block = (OutLineEntryBlock)getMasterParent();
					MovementOut mo = (MovementOut)block.getParentObject();
					ADManager adManager = Framework.getService(ADManager.class);
					mo = (MovementOut)adManager.getEntity(mo);
					block.setParentObject(mo);
					this.setParentObject(mo);
					setAdObject(createAdObject());
					refresh();
					this.getMasterParent().refresh();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	@Override
	public ADBase createAdObject() throws Exception {
		MovementLine outLine = null;
		try {
//			OutLineEntryBlock section = (OutLineEntryBlock)this.getMasterParent();
//			if(section.isEnableByParentObject()) {
//				INVManager invManager = Framework.getService(INVManager.class);			
//				outLine = invManager.newMovementLine((MovementOut)section.getParentObject());
//			} else {
				outLine = new MovementLine();
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
		if(adObject instanceof MovementLine) {
			MovementLine outLine = (MovementLine)adObject;
			setStatusChanged(outLine.getLineStatus());
			// 如果已保存则不能修改(用objectRrn不为空判断)
//			if(outLine.getObjectRrn() != null) {
//				if(itemSave != null) {
//					itemSave.setEnabled(false);
//				}
//			}
		} else {
			setStatusChanged("");
		}
	}

	protected void addMaterialUomIdListener() {
		IField materialField = getIField(FieldName_Material);
		materialField.addValueChangeListener(getUomIdrChangedListener());
	}
	
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
	}

}
