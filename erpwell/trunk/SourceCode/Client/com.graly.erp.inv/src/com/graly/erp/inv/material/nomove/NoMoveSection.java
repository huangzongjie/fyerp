package com.graly.erp.inv.material.nomove;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.material.online.QueryDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class NoMoveSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(NoMoveSection.class);

	protected TableListManager listTableManager;
	protected NoMoveDialog noMoveDialog;
	protected Date dateApproved;
	protected Long warehouseRrn;
	protected List<Material> materials;
	protected boolean isQueryAll = false;

	public NoMoveSection(TableListManager listTableManager) {
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
		} else if(this.noMoveDialog != null && noMoveDialog.getEntityQueryDialog() != null) {
			queryDialog = noMoveDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			NoMoveDialog noDialog = new NoMoveDialog(false);
			queryDialog = noDialog.new NoMoveQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			noDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}
	
	// 如果要加whereClause或order by必须为为SQL语句，不能为JPQL语句，也就是栏位必须是DB中字段
	// 例如whereClause：MATERIAL_CATEGORY1 = '商品' 
	public void refresh() {
		// 调用供应商评估方法
		try{
			materials = new ArrayList<Material>();
			if(dateApproved != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				materials = invManager.getNoMoveMaterialList(Env.getOrgRrn(), dateApproved, warehouseRrn, 
						getWhereClause(), null, Integer.MAX_VALUE);
			}
			viewer.setInput(materials);
			listTableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e){
			logger.error("EntityBlock : refresh()  ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void createSectionDesc(Section section) {
		int count = 0;
		if(materials != null) count = materials.size();
		String text = Message.getString("common.totalshow");
		text = String.format(text, String.valueOf(count), String.valueOf(count));
		section.setDescription("  " + text);
	}

	protected ADTable getADTable() {
		return listTableManager.getADTable();
	}
	
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof QueryDialog) {
			this.noMoveDialog = (NoMoveDialog)dialog;
		} else {
			this.noMoveDialog = null;
		}
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}
}
