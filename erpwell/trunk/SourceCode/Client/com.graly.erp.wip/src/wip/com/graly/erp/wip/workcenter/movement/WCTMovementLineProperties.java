package com.graly.erp.wip.workcenter.movement;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;


import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.wip.model.WCTMovement;
import com.graly.erp.wip.model.WCTMovementLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class WCTMovementLineProperties extends ChildEntityProperties {
	private static final Logger	logger				= Logger.getLogger(WCTMovementLineProperties.class);
	protected TableListManager listTableManager;
	private static final String	TABLE_NAME			= "WCTMovement";
	protected Object parentObject;
	private boolean flag;
	
	private ADTable				adTable;

	public WCTMovementLineProperties() {
		super();
	}

	public WCTMovementLineProperties(EntityBlock masterParent, ADTable table, Object parentObject,boolean flag) {
		super(masterParent, table, parentObject);
		this.parentObject = parentObject;
		this.flag = flag;
	}
	
	public void refresh() {
		super.refresh();
		getMasterParent().refresh();
	}
	
	protected ADTable getADTableOfWCTMovement() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}


	@Override
	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				WCTMovementLineEntryBlock block = (WCTMovementLineEntryBlock) getMasterParent();
				ADManager adManager = Framework.getService(ADManager.class);
				if(block.getParentObject() != null && ((WCTMovement)block.getParentObject()).getObjectRrn() != null) {
					block.setParentObject(adManager.getEntity((WCTMovement)block.getParentObject()));					
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
					WCTMovementLine wctmovementLine = (WCTMovementLine) getAdObject();
					WCTMovement wctmovement = (WCTMovement) block.getParentObject();
					if(isContainsSameMaterialInLines(wctmovement, wctmovementLine)) {
//						newAdapter();
						return;
					}

					WipManager wipManager = Framework.getService(WipManager.class);
					wctmovement = wipManager.saveWCTMovementLine(wctmovement, wctmovementLine, Env.getOrgRrn(), Env.getUserRrn());
					wctmovement = (WCTMovement) adManager.getEntity(wctmovement);
					// 刷新父子对象，并更新父对象的whereClause					
					block.setParentObject(wctmovement);
					List<WCTMovementLine> lines = wctmovement.getWCTMovementLines();
					this.setAdObject(adManager.getEntity(lines.get(0))); //adManager.getEntity(line)
					getMasterParent().setWhereClause(" movementRrn = '" + wctmovement.getObjectRrn() + "' ");
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
	protected boolean isContainsSameMaterialInLines(WCTMovement wctmovement, WCTMovementLine wctmovementLine) {
		if(wctmovement.getWCTMovementLines() != null) {
			for(WCTMovementLine l : wctmovement.getWCTMovementLines()) {
				if(l.getMaterialRrn().equals(wctmovementLine.getMaterialRrn()) && wctmovementLine.getObjectRrn() == null) {
					UI.showError(Message.getString("inv.doc_has_same_material"));
					getMasterParent().setSelection(l);
					return true;
				}
			}
		}
		return false;
	}


	protected void setStatusChanged(String status) {
	}

	@Override
	public ADBase createAdObject() throws Exception {
		WCTMovementLine wtcmovementLine = null;
		try {
			WCTMovementLineEntryBlock block = (WCTMovementLineEntryBlock) this.getMasterParent();
			if (block.isEnableByParentObject()) {
				WipManager wipManager = Framework.getService(WipManager.class);
				wtcmovementLine = wipManager.newMovementLine((WCTMovement) block.getParentObject());
			} else {
				wtcmovementLine = new WCTMovementLine();
			}
			wtcmovementLine.setOrgRrn(Env.getOrgRrn());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return wtcmovementLine;
	}
	
}
