package com.graly.erp.inv.adjust.out;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
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
	protected final String REPORT_FILE_NAME = "oout_report.rptdesign";
	
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
	
	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			MovementOut mo = (MovementOut)parentObject;
			if (mo != null && mo.getObjectRrn() != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				parentObject = invManager.approveMovementOut(mo, 
						getOutType(), Env.getUserRrn(), false);
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
	
	protected OutLineLotDialog createOutLotDialog(List<MovementLine> lines) {
		return new AdjustOutLineLotDialog(UI.getActiveShell(),
				parentObject, selectedOutLine, lines, false);
	}
	
	// 重载实现目的是调不同的报表
//	protected void previewAdapter() {
//		try {
//			form.getMessageManager().removeAllMessages();
//			HashMap<String, Object> params = new HashMap<String, Object>();
//			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
//			HashMap<String, String> userParams = new HashMap<String, String>();
//
//			MovementOut mo = (MovementOut)getParentObject();
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
		return MovementOut.OutType.ADOU;
	}
	
	@Override
	public String getReportFileName() {
		return REPORT_FILE_NAME;
	}
}
