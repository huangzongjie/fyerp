package com.graly.erp.inv.material.movesum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.material.nomove.NoMoveSection;
import com.graly.erp.inv.material.online.QueryDialog;
import com.graly.erp.wip.model.MaterialMoveSum;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MoveSumSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(NoMoveSection.class);

	protected TableListManager listTableManager;
	protected MoveSumDialog moveSumDialog;
	protected Date approvedStart, approvedEnd;
	
	protected List<MaterialMoveSum> moveMaterials;
	protected boolean isQueryAll = false;

	public MoveSumSection(TableListManager listTableManager) {
		super(null);
		this.listTableManager = listTableManager;
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createNewViewer(Composite client, final IManagedForm form) {
		viewer = listTableManager.createViewer(client, form.getToolkit());
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else if(this.moveSumDialog != null && moveSumDialog.getEntityQueryDialog() != null) {
			queryDialog = moveSumDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			MoveSumDialog sumDialog = new MoveSumDialog(false);
			queryDialog = sumDialog.new MoveSumQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			sumDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}
	
	// 如果要加whereClause或order by必须为为SQL语句，不能为JPQL语句，也就是栏位必须是DB中字段
	// 例如whereClause：MATERIAL_CATEGORY1 = '商品' 
	public void refresh() {
		// 调用供应商评估方法
		try{
			moveMaterials = new ArrayList<MaterialMoveSum>();
			if(this.approvedStart != null && this.approvedEnd != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				moveMaterials = invManager.getMaterialMoveSumList(Env.getOrgRrn(), approvedStart, approvedEnd,
						getWhereClause(), null, Integer.MAX_VALUE);
			}
			viewer.setInput(moveMaterials);
			listTableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e){
			logger.error("EntityBlock : refresh()  ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void createSectionDesc(Section section) {
		int count = 0;
		if(moveMaterials != null) count = moveMaterials.size();
		String text = Message.getString("common.totalshow");
		text = String.format(text, String.valueOf(count), String.valueOf(count));
		section.setDescription("  " + text);
	}

	protected ADTable getADTable() {
		return listTableManager.getADTable();
	}
	
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof QueryDialog) {
			this.moveSumDialog = (MoveSumDialog)dialog;
		} else {
			this.moveSumDialog = null;
		}
	}

	public Date getApprovedStart() {
		return approvedStart;
	}

	public void setApprovedStart(Date approvedStart) {
		this.approvedStart = approvedStart;
	}

	public Date getApprovedEnd() {
		return approvedEnd;
	}

	public void setApprovedEnd(Date approvedEnd) {
		this.approvedEnd = approvedEnd;
	}
}
