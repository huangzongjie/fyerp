package com.graly.erp.inv.iqc;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityBlock;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class IqcLineProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(IqcLineProperties.class);
	private ADTable adTable;
	private static final String TABLE_NAME = "INVPOLine";
	protected TableListManager listTableManager;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public IqcLineProperties() {
		super();
	}

	public IqcLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table, parentObject);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void saveAdapter() {
		try {
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
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					INVManager invManager = Framework.getService(INVManager.class);
					Iqc iqc = (Iqc) parentObject;
					IqcLine iqcLine = (IqcLine) getAdObject();
					if (iqcLine == null || iqcLine.getQtyIqc() == null) {
						UI.showWarning(Message.getString("inv.entityisnull"));
						return;
					}
					if (iqcLine.getQtyIqc() != null && iqcLine.getQtyIqc().doubleValue() < iqcLine.getQtyQualified().doubleValue()) {
						UI.showError(Message.getString("inv.qtyerror"));
						return;
					}
					iqcLine = invManager.saveIqcLine(iqc, iqcLine, Env.getUserRrn());
					ADManager adManager = Framework.getService(ADManager.class);
					setAdObject(adManager.getEntity(iqcLine));
					UI.showInfo(Message.getString("common.save_successed"));
					
					iqc = (Iqc) adManager.getEntity(iqc);
					this.setParentObject(iqc);
					((ChildEntityBlock)getMasterParent()).setParentObject(iqc);
					refresh();
					getMasterParent().refresh();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}

	}

	protected ADTable getADTableOfRequisition() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("ReceiptLineProperties : getADTableOfRequisition()", e);
		}
		return null;
	}

	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		refreshByParentObject();
	}

	public void refreshToolItem(boolean enable) {
		itemSave.setEnabled(enable);
	}

	public void refreshByParentObject() {
		if (parentObject != null && parentObject instanceof Iqc) {
			String status = ((Iqc) parentObject).getDocStatus();
			if (Iqc.STATUS_DRAFTED.equals(status)) {
				refreshToolItem(true);
			} else {
				refreshToolItem(false);
			}
		}
	}
}
