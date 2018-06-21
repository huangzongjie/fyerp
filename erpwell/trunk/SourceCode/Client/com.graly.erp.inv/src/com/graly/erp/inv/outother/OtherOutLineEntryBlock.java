package com.graly.erp.inv.outother;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
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
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.LotSelectFromDbDialog;
import com.graly.erp.inv.out.LotSelectFromDbDialogOld;
import com.graly.erp.inv.out.OutLineEntryBlock;
import com.graly.erp.inv.out.OutLineLotDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;
public class OtherOutLineEntryBlock extends OutLineEntryBlock {
	private static final Logger logger = Logger.getLogger(OtherOutLineEntryBlock.class);
	protected final String REPORT_FILE_NAME = "oout_report.rptdesign";
	private final Long BT_ORG_RRN = 12644730L;
	private final String USER_NAME_LUOXIAOHUA = "300185";
	private static final String INV_OUT_OLD_SELECT_LOT = "Inv.Out.OldSelectLot";
	
	protected ToolItem itemOldSelectLot;
	
	public OtherOutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable) {
		super(parentTable, parentObject, whereClause, childTable);
	}
	
	public OtherOutLineEntryBlock(ADTable parentTable, Object parentObject,
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
		createToolItemRackLot(tBar);
		createToolItemOldSelectLot(tBar);
		createToolItemSelectLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		//此处做出修改，如果是奔泰环境，且相关单位包含“售后”等字眼时，审核人只有罗小华
		
		createToolItemApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemOutSerial(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		createToolItemExport(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_APPROVED);
		checkApproveAuthority();
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}

	/**
	 * 如果是奔泰，并且相关单位包含“售后”的话，审核人只有罗小华
	 */
	private void checkApproveAuthority() {
		if(BT_ORG_RRN==Env.getOrgRrn()){
			Object obj = getParentObject();
			if(obj != null && obj instanceof MovementOut){
				MovementOut mo = (MovementOut)obj;
				if(mo.getKind() != null && mo.getKind().contains("售后")){
					if(!USER_NAME_LUOXIAOHUA.equals(Env.getUserName())){
						//如果是奔泰，并且相关单位包含“售后”
						//如果审核人不是罗小华，就把用户权限中的该权限删除，即只允许罗小华有此权限
						Env.getAuthority().remove(Constants.KEY_OOU_APPROVED);
					}
				}
			}
		}
	}

	protected OutLineLotDialog createOutLotDialog() {
		return new OutOtherLineLotDialog(UI.getActiveShell(),
				parentObject, selectedOutLine, false);
	}
	
	private void createToolItemOldSelectLot(ToolBar tBar) {
		itemOldSelectLot = new AuthorityToolItem(tBar, SWT.PUSH, INV_OUT_OLD_SELECT_LOT);
		itemOldSelectLot.setText("自动选批");
		itemOldSelectLot.setImage(SWTResourceCache.getImage("barcode"));
		itemOldSelectLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				oldSelectLotAdapter();
			}
		});
	}
	
	protected void oldSelectLotAdapter() {
		try {
			if(selectedOutLine != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				MovementOut out = (MovementOut)parentObject;
				out = (MovementOut)adManager.getEntity((MovementOut)out);
				parentObject = out;
				selectedOutLine = (MovementLine)adManager.getEntity(selectedOutLine);
				INVManager invManager = Framework.getService(INVManager.class);
//				List<Lot> lots = invManager.getOptionalOutLot(selectedOutLine);
				List<Lot> lots =null;
				if(Env.getOrgRrn()== 139420L && out.getWarehouseRrn()==151043L){
					if(out.getWmsWarehouse()!=null && out.getWmsWarehouse().length()>0){
						lots = invManager.getOptionalOutLotInWms(selectedOutLine);
					}else{
						lots = invManager.getOptionalOutLotNoWms(selectedOutLine);
					}
				}else{
					 lots = invManager.getOptionalOutLot(selectedOutLine);
				}
				if(lots == null || lots.size() == 0) {
					String name = selectedOutLine.getMaterialName();
					UI.showInfo(String.format(Message.getString("inv.material_no_lot"), name));
					return;
				}
				// 打开批次界面，可以进行删除、保存等操作
				LotSelectFromDbDialogOld dialog = new LotSelectFromDbDialogOld(UI.getActiveShell(),
						parentObject, selectedOutLine, lots);
				if (dialog.open() == Dialog.CANCEL) {
					selectedOutLine = null;
					this.viewer.setSelection(null);
					parentObject = adManager.getEntity((MovementOut)parentObject);
					refresh();
				}
			} else {
				UI.showWarning(Message.getString("inv.entityisnull"));
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
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
			detailsPart.registerPage(klass, new OtherOutLineProperties(this, table, getParentObject(),flag));
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}

	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.OOU;
	}
	
	@Override
	public String getReportFileName() {
		if(Env.getOrgRrn()==68088906L){
			return "oout_report_yn.rptdesign";
		}
		return REPORT_FILE_NAME;
	}
	
	
	public void setParenObjectStatusChanged() {
		MovementOut mo = (MovementOut)parentObject;
		String status = "";
		if(mo != null && mo.getObjectRrn() != null) {
			status = mo.getDocStatus();			
		}
		if(MovementOut.STATUS_APPROVED.equals(status)) {
			itemSelectLot.setEnabled(false);
			itemOldSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
			itemOutSerial.setEnabled(true);
			itemPreview.setEnabled(true);
		} else if(MovementOut.STATUS_DRAFTED.equals(status)) {
			itemSelectLot.setEnabled(true);
			itemOldSelectLot.setEnabled(true);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(true);
			itemOutSerial.setEnabled(false);
			itemPreview.setEnabled(true);
		} else if(MovementOut.STATUS_CLOSED.equals(status)) {
			itemSelectLot.setEnabled(false);
			itemOldSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
			itemOutSerial.setEnabled(true);
			itemPreview.setEnabled(false);
		} else {
			itemSelectLot.setEnabled(false);
			itemOldSelectLot.setEnabled(false);
			itemLot.setEnabled(true);
			itemApprove.setEnabled(false);
			itemOutSerial.setEnabled(false);
			itemPreview.setEnabled(false);
		}
		if(flag){
			itemLot.setEnabled(true);
			itemSelectLot.setEnabled(false);
			itemOldSelectLot.setEnabled(false);
			itemApprove.setEnabled(false);
			itemOutSerial.setEnabled(true);
			itemPreview.setEnabled(false);
		}
	}
}
