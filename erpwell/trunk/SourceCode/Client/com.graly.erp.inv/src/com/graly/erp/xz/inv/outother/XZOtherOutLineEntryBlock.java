package com.graly.erp.xz.inv.outother;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.OutLineEntryBlock;
import com.graly.erp.inv.out.OutLineLotDialog;
import com.graly.erp.inv.outother.OutOtherLineLotDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class XZOtherOutLineEntryBlock extends OutLineEntryBlock {
	private static final Logger logger = Logger.getLogger(XZOtherOutLineEntryBlock.class);
	protected final String REPORT_FILE_NAME = "spares_oout_report_xz.rptdesign";
	
	protected ToolItem itemOldSelectLot;
	protected ToolItem itemSelectAll;
	
	public XZOtherOutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable,boolean flag) {
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
		//设置tableManager,原因系统框架初始化构造不支持checkbox
		EntityTableManager tableManager = new EntityTableManager(childTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION |SWT.BORDER 
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.HIDE_SELECTION);
		setTableManager(tableManager);
		
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSelectAll(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemSelectAll(ToolBar tBar) {
		itemSelectAll = new ToolItem(tBar, SWT.PUSH);
		itemSelectAll.setText("全选");
		itemSelectAll.setImage(SWTResourceCache.getImage("new"));
		itemSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectAllAdapter();
			}
		});
	}
	
	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}

	protected OutLineLotDialog createOutLotDialog() {
		return new OutOtherLineLotDialog(UI.getActiveShell(),
				parentObject, selectedOutLine, false);
	}
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class<?> klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new XZOtherOutLineProperties(this, table, getParentObject(),flag));
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}

	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.OOU;
	}
	
	@Override
	public String getReportFileName() {
		return REPORT_FILE_NAME;
	}
	
	
	public void setParenObjectStatusChanged() {
		MovementOut mo = (MovementOut)parentObject;
		String status = "";
		if(mo != null && mo.getObjectRrn() != null) {
			status = mo.getDocStatus();			
		}
		if(MovementOut.STATUS_APPROVED.equals(status)) {
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(true);
		} else if(MovementOut.STATUS_DRAFTED.equals(status)) {
			itemApprove.setEnabled(true);
			itemPreview.setEnabled(false);
		} else if(MovementOut.STATUS_CLOSED.equals(status)) {
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(false);
		} else {
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(false);
		}
		if(flag){
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(false);
		}
	}
	
	protected void selectAllAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			TableViewer tableViewer = (TableViewer)viewer;
			TableItem[] items = tableViewer.getTable().getItems();
			for(TableItem item : items){
				if(item.getChecked()){
					item.setChecked(false);
				}else{
					item.setChecked(true);
				}
				
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void previewAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			
			//保存打印次数
			MovementOut mo = (MovementOut)getParentObject();
			Long time = mo.getPrintTime();
			if(time == null){
				mo.setPrintTime(1L);
			}else{
				mo.setPrintTime(time + 1L);
			}
			ADManager manager = Framework.getService(ADManager.class);
			parentObject = manager.saveEntity(mo, Env.getUserRrn());			
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(!Movement.STATUS_APPROVED.equals(mo.getDocStatus())){
				UI.showWarning(Message.getString("common.is_not_approved")+","+Message.getString("common.can_not_print"));
				return;
			}
			
			if(mo == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = mo.getObjectRrn();
			userParams.put("OBJECT_RRN", String.valueOf(objectRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), getReportFileName(), params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message
					.getString("common.title_confirm"));
			if (!confirm)
				return;
			form.getMessageManager().removeAllMessages();
			CheckboxTableViewer checkTableViewer = (CheckboxTableViewer) this.viewer;
			Object[] checkObjects = checkTableViewer.getCheckedElements();
			List<MovementLine> movementLines = new ArrayList<MovementLine>();
			for(Object object : checkObjects){
				MovementLine movementLine = (MovementLine) object;
				movementLines.add(movementLine);
				if(MovementIn.STATUS_APPROVED.equals(movementLine.getLineStatus())){
					UI.showError("已审核的领料单不允许再次审核，物料编号:"+movementLine.getMaterialId());
					return;
				}
			}
			if(movementLines==null || movementLines.size() ==0){
				UI.showError("请选中一条出库单或者多条出库单打钩，然后再审核");
				return;
			}
			MovementOut mo = (MovementOut) parentObject;
			if (mo != null && mo.getObjectRrn() != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				parentObject = invManager.approveXZMovementOut(mo,
						movementLines,
						getOutType(),
						Env.getUserRrn(),
						true,
						true);
 
				// 需要用adManager再获得parentObject，打印时往数据库中记入了打印次数,如不重新获取会报该记录已被更新或删除的错误
				ADManager adManager = Framework.getService(ADManager.class);
				parentObject = adManager.getEntity((ADBase) parentObject);
				UI.showInfo(Message.getString("common.approve_successed"));
				setParenObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
 
}
