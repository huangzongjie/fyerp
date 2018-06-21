package com.graly.erp.inv.outdevelop;

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
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.OutLineEntryBlock;
import com.graly.erp.inv.out.OutLineLotDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;

public class DevelopOutLineEntryBlock extends OutLineEntryBlock {
	private static final Logger logger = Logger.getLogger(DevelopOutLineEntryBlock.class);
	protected final String REPORT_FILE_NAME = "oout_report.rptdesign";
	private final Long BT_ORG_RRN = 12644730L;
	private final String USER_NAME_LUOXIAOHUA = "300185";
	
	public DevelopOutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable) {
		super(parentTable, parentObject, whereClause, childTable);
	}
	
	public DevelopOutLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable,boolean flag) {
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
	}
	
	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_DOU_APPROVED);
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
						Env.getAuthority().remove(Constants.KEY_DOU_APPROVED);
					}
				}
			}
		}
	}

	protected OutLineLotDialog createOutLotDialog() {
		return new OutDevelopLineLotDialog(UI.getActiveShell(),
				parentObject, selectedOutLine, false);
	}
	
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class<?> klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new DevelopOutLineProperties(this, table, getParentObject(),flag));
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}

	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.DOU;
	}
	
	@Override
	public String getReportFileName() {
		return REPORT_FILE_NAME;
	}
}