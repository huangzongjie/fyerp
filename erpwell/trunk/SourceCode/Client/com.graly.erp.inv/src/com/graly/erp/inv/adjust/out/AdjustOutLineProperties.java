package com.graly.erp.inv.adjust.out;

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

import com.graly.erp.inv.MovementChildForm;
import com.graly.erp.inv.client.INVManager;
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

public class AdjustOutLineProperties extends OutLineProperties {
	private static final Logger logger = Logger.getLogger(AdjustOutLineProperties.class);
	private String PREFIX = " movementRrn = '";
	
	public AdjustOutLineProperties() {
		super();
    }
	
	public AdjustOutLineProperties(EntityBlock masterParent, ADTable table, Object parentObject,boolean flag) {
		super(masterParent, table, parentObject,flag);
	}

	// ����ʵ�ִ���MovementChildForm(ʹ�����ؼ������Ϊֻ��)
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
			ChildEntityForm itemForm = new ChildEntityForm(getTabs(), SWT.NONE, null, tab, mmng, parentObject);
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
		AdjustOutLineEntryBlock block = (AdjustOutLineEntryBlock)getMasterParent();
		block.setParenObjectStatusChanged();
	}

	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				AdjustOutLineEntryBlock block = (AdjustOutLineEntryBlock)getMasterParent();
				if(!block.saveParent()) {
					form.getMessageManager().setAutoUpdate(true);
					return;   // �жϱ��游�����Ƿ�ɹ������򷵻�
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
					if(isContainsSameMaterialInLines(mm, line)) {
//						newAdapter();
						return;
					}
					// ��ø�Line��Ӧ��MovementLineLots
					line.setMovementLots(getLineLotList(mm, line));

					// ����savePOLine()����
					INVManager invManager = Framework.getService(INVManager.class);
					line = invManager.saveMovementOutLine(mm, line, MovementOut.OutType.ADOU, Env.getUserRrn());
					// ˢ�¸��Ӷ��󣬲����¸������whereClause
					ADManager adManager = Framework.getService(ADManager.class);
					mm.setObjectRrn(line.getMovementRrn());
					block.setParentObject(adManager.getEntity(mm));
					this.setAdObject(adManager.getEntity(line));  //adManager.getEntity(line)
					getMasterParent().setWhereClause(PREFIX + mm.getObjectRrn() + "' ");
					// ��ʾ����ɹ�
					UI.showInfo(Message.getString("common.save_successed"));
					refresh();
					this.getMasterParent().refresh();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			logger.error("Error at AdjustOutLineProperties saveAdapter() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	// �жϵ������Ƿ�������ͬ�����ϣ�����У�����ʾֱ����ԭ�е�line�ϸ�����������ɾ��ԭ�е�line�ؽ�
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
