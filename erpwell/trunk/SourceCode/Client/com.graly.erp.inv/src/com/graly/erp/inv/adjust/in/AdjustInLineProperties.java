package com.graly.erp.inv.adjust.in;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Locator;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AdjustInLineProperties extends ChildEntityProperties {
	protected TableListManager listTableManager;
	protected Object parentObject;
	private boolean flag;

	public AdjustInLineProperties() {
		super();
	}

	public AdjustInLineProperties(EntityBlock masterParent, ADTable table, Object parentObject,boolean flag) {
		super(masterParent, table, parentObject);
		this.parentObject = parentObject;
		this.flag = flag;
	}

	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		
		AdjustInLineEntryBlock block = (AdjustInLineEntryBlock)getMasterParent();
		IField warehouse = (IField)block.getDetailForms().get(0).getFields().get("warehouseRrn");
		Object  warehouseRrn = warehouse.getValue();
		if(warehouseRrn != null){
			reputLocator(warehouseRrn);
		}
		
		MovementIn movementIn = (MovementIn) parentObject;
		setStatusChanged(movementIn.getDocStatus());
	}
	
	public void refresh() {
		super.refresh();
//		getMasterParent().refresh();
	}

	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				AdjustInLineEntryBlock block = (AdjustInLineEntryBlock) getMasterParent();
				ADManager adManager = Framework.getService(ADManager.class);
				if(block.getParentObject() != null && ((MovementIn)block.getParentObject()).getObjectRrn() != null) {
					block.setParentObject(adManager.getEntity((MovementIn)block.getParentObject()));					
				}
				if (!block.saveParent()) {
					form.getMessageManager().setAutoUpdate(true);
					return; // 判断保存父对象是否成功，否则返回
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
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					MovementLine movementLine = (MovementLine) getAdObject();
					MovementIn movementIn = (MovementIn) block.getParentObject();
					if(isContainsSameMaterialInLines(movementIn, movementLine)) {
//						newAdapter();
						return;
					}
					// 获得该Line对应的Lots
					movementLine.setMovementLots(getLineLotList(movementIn, movementLine));
					List<MovementLine> list = new ArrayList<MovementLine>();
					list.add(movementLine);

					INVManager invManager = Framework.getService(INVManager.class);
					movementIn = invManager.saveMovementInLine(movementIn, list,
							getInType(), Env.getUserRrn());
					// 刷新父子对象，并更新父对象的whereClause					
					block.setParentObject(adManager.getEntity(movementIn));

					List<MovementLine> lines = movementIn.getMovementLines();
					this.setAdObject(adManager.getEntity(lines.get(0))); //adManager.getEntity(line)
					getMasterParent().setWhereClause(" movementRrn = '" + movementIn.getObjectRrn() + "' ");
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
	
	// 判断单据中是否已有相同的物料，如果有，则提示直接在原有的line上更改数量，或删除原有的line重建
	protected boolean isContainsSameMaterialInLines(MovementIn mm, MovementLine line) {
		if(mm.getMovementLines() != null) {
			for(MovementLine l : mm.getMovementLines()) {
				if(l.getMaterialRrn().equals(line.getMaterialRrn()) && line.getObjectRrn() == null) {
					UI.showError(Message.getString("inv.doc_has_same_material"));
					getMasterParent().setSelection(l);
					return true;
				}
			}
		}
		return false;
	}
	
	protected List<MovementLineLot> getLineLotList(MovementIn in, MovementLine line) throws Exception{
		List<MovementLineLot> lineLots = null;
		if(in.getObjectRrn() == null || line.getObjectRrn() == null)
			return lineLots;
		String whereClause = " movementRrn = " + in.getObjectRrn()
							+ " AND movementLineRrn = " + line.getObjectRrn() + " ";
    	ADManager manager = Framework.getService(ADManager.class);
    	lineLots = manager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,
        		Integer.MAX_VALUE, whereClause, null);
//    	for(MovementLineLot lineLot : lineLots) {
//    		lineLot.setObjectRrn(null);
//    	}
		return lineLots;
	}

	protected void deleteAdapter() {
		if (!Movement.STATUS_DRAFTED.equals(((MovementLine) getAdObject()).getLineStatus())) {
			return;
		}
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.deleteMovementInLine((MovementLine) getAdObject(), MovementIn.InType.OIN, Env.getUserRrn());
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

	protected void setStatusChanged(String status) {
		if (!(MovementIn.STATUS_DRAFTED.equals(status)) || flag) {
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemNew.setEnabled(true);
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
		}
	}

	@Override
	public ADBase createAdObject() throws Exception {
		MovementLine inLine = null;
		try {
			AdjustInLineEntryBlock block = (AdjustInLineEntryBlock) this.getMasterParent();
			if (block.isEnableByParentObject()) {
				INVManager invManager = Framework.getService(INVManager.class);
				inLine = invManager.newMovementLine((MovementIn) block.getParentObject());
			} else {
				inLine = new MovementLine();
			}
			inLine.setOrgRrn(Env.getOrgRrn());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return inLine;
	}
	
	public void reputLocator(Object object){
		if(object != null){
			IField locatorField = getIField("locatorRrn");
			if(locatorField instanceof RefTableField) {
				RefTableField tf = (RefTableField)locatorField;
				try{
					ADManager entityManager = Framework.getService(ADManager.class);
					String where = " orgRrn = " + Env.getOrgRrn() + " AND warehouseRrn = '" + object.toString() + "' ";
					List<Locator> list = entityManager.getEntityList(Env.getOrgRrn(), Locator.class, Env.getMaxResult(), where, "");
					list = list.size() == 0 ? new ArrayList<Locator>() : list;
					tf.setInput(list);
					tf.refresh();
				}catch(Exception e){
					ExceptionHandlerManager.asyncHandleException(e);
					return;
				}
			}
		}
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
	
	protected MovementIn.InType getInType() {
		return MovementIn.InType.ADIN;
	}
}
