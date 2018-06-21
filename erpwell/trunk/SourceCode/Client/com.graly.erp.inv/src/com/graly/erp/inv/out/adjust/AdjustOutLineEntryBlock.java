package com.graly.erp.inv.out.adjust;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
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
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.OutLineEntryBlock;
import com.graly.erp.inv.out.OutLineLotDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class AdjustOutLineEntryBlock extends OutLineEntryBlock {
	private static final Logger logger = Logger.getLogger(AdjustOutLineEntryBlock.class);
	protected final String REPORT_FILE_NAME = "adjustout_report.rptdesign";
	
	public AdjustOutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable) {
		super(parentTable, parentObject, whereClause, childTable);
	}
	
	public AdjustOutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable,boolean flag) {
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSelectLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		section.setTextClient(tBar);
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
	
	//重写审核方法
		protected void approveAdapter() {
			try {
				boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
				if(!confirm) return;
				form.getMessageManager().removeAllMessages();
				MovementOut mo = (MovementOut)parentObject;
				if (mo != null && mo.getObjectRrn() != null) {
					INVManager invManager = Framework.getService(INVManager.class);
					//2012-4-26
					if(MovementOut.OUT_TYPE_SALE_ADJUST.equals(mo.getOutType())){
						parentObject = invManager.approveMovementOut(mo, getOutType(), Env.getUserRrn(), true, true);//审核时改变财务库存也改变营运库存
					}else{
						//2012-4-20
						parentObject = invManager.approveMovementOut(mo, getOutType(), Env.getUserRrn(), false, true);//审核时改变财务库存不改变营运库存
					}
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

	protected OutLineLotDialog createOutLotDialog() {
		return new AdjustOutLineLotDialog(UI.getActiveShell(),
				parentObject, selectedOutLine, false);
	}
	
	// 重载实现目的是调不同的报表
//	protected void previewAdapter() {
//		try {
//			form.getMessageManager().removeAllMessages();
//			
//			//保存打印次数
//			MovementOut mo = (MovementOut)getParentObject();
//			Long time = mo.getPrintTime();
//			if(time == null){
//				mo.setPrintTime(1L);
//			}else{
//				mo.setPrintTime(time + 1L);
//			}
//			ADManager manager = Framework.getService(ADManager.class);
//			manager.saveEntity(mi, Env.getUserRrn());			
//			
//			HashMap<String, Object> params = new HashMap<String, Object>();
//			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
//			HashMap<String, String> userParams = new HashMap<String, String>();
//
//			if(!Movement.STATUS_APPROVED.equals(mo.getDocStatus())){
//				UI.showWarning(Message.getString("common.is_not_approved")+","+Message.getString("common.can_not_print"));
//				return;
//			}
//			if(mo == null){
//				UI.showWarning(Message.getString("common.choose_one_record"));
//				return;
//			}
//			
//			Long objectRrn = mo.getObjectRrn();
//			userParams.put("OBJECT_RRN", String.valueOf(objectRrn));
//				
//			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), REPORT_FILE_NAME, params, userParams);
//			dialog.open();
//		} catch (Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//			return;
//		}
//	}
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class<?> klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new AdjustOutLineProperties(this, table, getParentObject(),flag));
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}

	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.AOU;
	}
	
	@Override
	public void setParenObjectStatusChanged() {
		MovementOut mo = (MovementOut)parentObject;
		String status = "";
		if(mo != null && mo.getObjectRrn() != null) {
			status = mo.getDocStatus();			
		}
		if(MovementOut.STATUS_APPROVED.equals(status)) {
			itemSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
//			itemOutSerial.setEnabled(true);
		} else if(MovementOut.STATUS_DRAFTED.equals(status)) {
			itemSelectLot.setEnabled(true);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(true);
//			itemOutSerial.setEnabled(false);
		} else if(MovementOut.STATUS_CLOSED.equals(status)) {
			itemSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
//			itemOutSerial.setEnabled(true);
		} else {
			itemSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
//			itemOutSerial.setEnabled(false);
		}
		if(flag){
			itemLot.setEnabled(true);
			itemSelectLot.setEnabled(false);
			itemApprove.setEnabled(false);
//			itemOutSerial.setEnabled(true);
		}
	}
	
	@Override
	public String getReportFileName() {
		return REPORT_FILE_NAME;
	}
}
