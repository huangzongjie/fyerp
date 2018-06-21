package com.graly.erp.wip.workcenter.movement;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.wip.model.WCTMovement;
import com.graly.erp.wip.model.WCTMovementLine;
import com.graly.erp.wip.model.WCTMovementLineLot;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class WCTMovementLineEntryBlock extends ParentChildEntityBlock {
	Logger logger = Logger.getLogger(WCTMovementLineEntryBlock.class);
	protected WCTMovementLine selectWCTMovementLine;
	protected WCTMovementLineProperties WCTMovementLineProperties;
//	private String FieldName_Warehouse = "warehouseRrn";
	protected boolean flag = false;
	protected ToolItem itemApprove;
	protected ToolItem itemLot;

	public WCTMovementLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable, boolean flag) {
		super(parentTable, parentObject, whereClause, childTable);
		 this.parentObject = parentObject;
		// this.flag= flag;
	}

	protected void createMasterPart(final IManagedForm managedForm,
			Composite parent) {
		super.createMasterPart(managedForm, parent);
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemCheck(tBar);
		createToolItemLot(tBar);
		section.setTextClient(tBar);
	}

	public void createToolItemCheck(ToolBar tBar) {
		itemApprove = new ToolItem(tBar, SWT.PUSH);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				 approveAdapter();
			}
		});
	}

	public void createToolItemLot(ToolBar tBar) {
		itemLot = new ToolItem(tBar, SWT.PUSH);
		itemLot.setText(Message.getString("inv.barcode"));
		itemLot.setImage(SWTResourceCache.getImage("barcode"));
		itemLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				lotAdapter();
			}
		});
	}
	//审核方法
	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			WCTMovement wctMovement = (WCTMovement)parentObject;
			ADManager adManager = Framework.getService(ADManager.class);
			List<WCTMovementLine> wctMovementlines = new ArrayList<WCTMovementLine>();
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(),
					getTableManager().getADTable().getObjectRrn(), Env
							.getMaxResult(), getWhereClause(), null);
			for (ADBase adBase : list) {
				if (adBase instanceof WCTMovementLine)
					wctMovementlines.add((WCTMovementLine) adBase);
			}
			
			for(WCTMovementLine wctLine:wctMovementlines){
				List<WCTMovementLineLot> wctMlines =null;
				wctMlines=adManager.getEntityList(Env.getOrgRrn(),WCTMovementLineLot.class,Env.getMaxResult(),"movementLineRrn="+wctLine.getObjectRrn(),null);
				if(wctMlines!=null && wctMlines.size()>0)
					continue;
				else {
					UI.showError("对不起绑定批次的数量与实际不符合");
					return;
				}
			}
			if (wctMovement != null ) {
				WipManager wipManager = Framework.getService(WipManager.class);
				wipManager.approveWCTMovement(wctMovement, Env.getUserRrn());
				// 需要用adManager再获得parentObject，打印时往数据库中记入了打印次数,如不重新获取会报该记录已被更新或删除的错误
				UI.showInfo(Message.getString("common.approve_successed"));
//				setParenObjectStatusChanged();
//				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	protected void lotAdapter() {
		try {
			// WCTMovementLine wctMovementLine = (WCTMovementLine)
			// StructuredSelection ss = (StructuredSelection)
			// this.viewer.getSelection();

			WCTMovement out = (WCTMovement) parentObject;
			if (out != null && out.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				out = (WCTMovement) adManager.getEntity((WCTMovement) out);
				parentObject = out;
				List<WCTMovementLine> wctMovementlines = new ArrayList<WCTMovementLine>();
				if (selectWCTMovementLine != null) {
					selectWCTMovementLine = (WCTMovementLine) adManager.getEntity(selectWCTMovementLine);
				}
				List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(),
						getTableManager().getADTable().getObjectRrn(), Env
								.getMaxResult(), getWhereClause(), null);
				for (ADBase adBase : list) {
					if (adBase instanceof WCTMovementLine)
						wctMovementlines.add((WCTMovementLine) adBase);
				}
				if ((wctMovementlines == null || wctMovementlines.size() == 0)
						&& selectWCTMovementLine == null)
					return;

				WCTMovementLotDialog od = new WCTMovementLotDialog(UI
						.getActiveShell(), parentObject, selectWCTMovementLine,
						wctMovementlines, false);

				if (od.open() == Dialog.CANCEL) {
					selectWCTMovementLine = null;
					this.viewer.setSelection(null);
					 out = (WCTMovement)adManager.getEntity((WCTMovement)out);
					 parentObject = out;
					refresh();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setMovementLineSelect(ss.getFirstElement());
					refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setMovementLineSelect(Object obj) {
		if (obj instanceof WCTMovementLine) {
			selectWCTMovementLine = (WCTMovementLine) obj;
		} else {
			selectWCTMovementLine = null;
		}
	}

	public boolean isEnableByParentObject() {
		WCTMovement movement = (WCTMovement) this.getParentObject();
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			WCTMovementLineProperties = new WCTMovementLineProperties(this,table, getParentObject(), flag);
			detailsPart.registerPage(klass, WCTMovementLineProperties);
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}

}