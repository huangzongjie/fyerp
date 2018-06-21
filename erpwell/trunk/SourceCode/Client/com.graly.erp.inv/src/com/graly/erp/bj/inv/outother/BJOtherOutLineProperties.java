package com.graly.erp.bj.inv.outother;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.BJWipEquipment;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.OutLineEntryBlock;
import com.graly.erp.inv.out.OutLineProperties;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BJOtherOutLineProperties extends OutLineProperties {
	private static final Logger logger = Logger.getLogger(BJOtherOutLineProperties.class);
	private String PREFIX = " movementRrn = '";
	
	public BJOtherOutLineProperties() {
		super();
    }
	
	public BJOtherOutLineProperties(EntityBlock masterParent, ADTable table, Object parentObject,boolean flag) {
		super(masterParent, table, parentObject,flag);
	}

	// 重载实现创建MovementChildForm(使各个控件保存后为只读)
	@Override
	protected void createSectionContent(Composite client) {
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

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			BJOtherOutPropertiesForm itemForm = new BJOtherOutPropertiesForm(getTabs(), SWT.NONE, null, tab, mmng, parentObject);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		if (parentObject != null) {
			loadFromParent();
		}
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	public void refresh() {
		super.refresh();
		BJOtherOutLineEntryBlock block = (BJOtherOutLineEntryBlock)getMasterParent();
		block.setParenObjectStatusChanged();
	}

	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				BJOtherOutLineEntryBlock block = (BJOtherOutLineEntryBlock)getMasterParent();
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
					if(line.getEquipmentRrn()!=null){
						ADManager adManager = Framework.getService(ADManager.class);
						BJWipEquipment equipment = new BJWipEquipment();
						equipment.setObjectRrn(line.getEquipmentRrn());
						equipment = (BJWipEquipment) adManager.getEntity(equipment);
						if(equipment !=null ){
							line.setEquipmentId(equipment.getEquipmentId());
						}
					}
					MovementOut mm = (MovementOut)block.getParentObject();
					if(isContainsSameMaterialInLines(mm, line)) {
//						newAdapter();
						return;
					}
					// 获得该Line对应的MovementLineLots
					line.setMovementLots(getLineLotList(mm, line));

					// 调用savePOLine()方法
					INVManager invManager = Framework.getService(INVManager.class);
					line = invManager.saveMovementOutLine(mm, line, MovementOut.OutType.OOU, Env.getUserRrn());
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
			logger.error("Error at OtherOutLineProperties saveAdapter() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	// 判断单据中是否已有相同的物料，如果有，则提示直接在原有的line上更改数量，或删除原有的line重建
	protected boolean isContainsSameMaterialInLines(MovementOut mm, MovementLine line) {
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
	
	protected List<MovementLineLot> getLineLotList(MovementOut out, MovementLine line) throws Exception {
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
	
	@Override
	public ADBase createAdObject() throws Exception {
		MovementLine outLine = null;
		try {
			OutLineEntryBlock block = (OutLineEntryBlock)this.getMasterParent();
			if(block.isEnableByParentObject()) {
				INVManager invManager = Framework.getService(INVManager.class);			
				outLine = invManager.newMovementLine((MovementOut)block.getParentObject());
			} else {
				outLine = new MovementLine();
			}
			outLine.setOrgRrn(Env.getOrgRrn());
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return outLine;
	}

	protected void addMaterialUomIdListener() {
	}
	
	protected void setStatusChanged(String status) {
		if(PurchaseOrder.STATUS_DRAFTED.equals(status)) {
			itemNew.setEnabled(true);
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if(PurchaseOrder.STATUS_APPROVED.equals(status)) {
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		}
		OutLineEntryBlock block = (OutLineEntryBlock)this.getMasterParent();
		if(block.isViewOnly()) {
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
}
