package com.graly.erp.inv.material.online;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class OnlineSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(OnlineSection.class);

	protected TableListManager listTableManager;
	protected QueryDialog onlineDialog;
	protected List<MaterialSum> input;
	protected Long materialRrn;
	protected String materialId;
	protected List<Material> materials;
	protected boolean isQueryAll = false;

	public OnlineSection(TableListManager listTableManager) {
		super(null);
		this.listTableManager = listTableManager;
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createNewViewer(Composite client, final IManagedForm form){
		viewer = listTableManager.createViewer(client, form.getToolkit());
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else if(this.onlineDialog != null && onlineDialog.getEntityQueryDialog() != null) {
			queryDialog = onlineDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			QueryDialog vaDialog = new QueryDialog(false);
			queryDialog = vaDialog.new OnlineQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			vaDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}
	
	public void refresh(){
		// 调用供应商评估方法
		try{
			input = new ArrayList<MaterialSum>();
			List<Material> errorMaterials = new ArrayList<Material>();
			if(materialRrn != null) {
				WipManager wipManager = Framework.getService(WipManager.class);
//				MaterialSum ms = wipManager.getMaterialSum (Env.getOrgRrn(), materialRrn, false, false);
				MaterialSum ms = wipManager.getMaterialSum2(Env.getOrgRrn(), materialRrn, false, false,false);
				if(ms == null) {
					UI.showWarning(getMessageInfo(null));
				} else {
					input.add(ms);
				}
			}
			else {
				QueryProgressMonitorDialog progressDiglog = new QueryProgressMonitorDialog(
						UI.getActiveShell(), "");
				if(materials != null && materials.size() > 0) {
					progressDiglog.run(true, true, progressDiglog.createProgress(materials));
				}
				else if(isQueryAll){
					progressDiglog.run(true, true, progressDiglog.createProgress());
				}
				input = progressDiglog.getMaterialSums();
				errorMaterials = progressDiglog.getErrorMaterials();
				List<Material> unQuerys = progressDiglog.getUnQueryMaterials();
				if(unQuerys != null && unQuerys.size() > 0) {
					UI.showWarning(getMessageInfo(unQuerys));
				}
			}
			viewer.setInput(input);
			listTableManager.updateView(viewer);
			createSectionDesc(section);
			//添加弹出错误对话框显示错误信息
			if(errorMaterials!=null && errorMaterials.size() >0 ){
				OnlineErrorDialog errorDialog = new OnlineErrorDialog(UI.getActiveShell(), null, null, errorMaterials);
				if(errorDialog.open() == Dialog.OK ){
				}
			}
		} catch (Exception e){
			logger.error("EntityBlock : refresh()  ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	private String getMessageInfo(List<Material> unQuerys) {
		if(unQuerys == null) 
			return String.format(Message.getString("inv.material_isnot_control_by_lot_or_mrp"), materialId);
		StringBuffer sb = new StringBuffer("");
		for(Material mater : unQuerys) {
			sb.append(mater.getMaterialId());
			sb.append(", ");
		}
		sb.substring(0, sb.length() - 2);
		return String.format(Message.getString("inv.material_isnot_control_by_lot_or_mrp"), sb.toString());
	}
	
	protected void createSectionDesc(Section section) {
		int count = 0;
		if(input != null) count = input.size();
		String text = Message.getString("common.totalshow");
		text = String.format(text, String.valueOf(count), String.valueOf(count));
		section.setDescription("  " + text);
	}

	protected ADTable getADTable() {
		return listTableManager.getADTable();
	}
	
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof QueryDialog) {
			this.onlineDialog = (QueryDialog)dialog;
		} else {
			this.onlineDialog = null;
		}
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public boolean isQueryAll() {
		return isQueryAll;
	}

	public void setQueryAll(boolean isQueryAll) {
		this.isQueryAll = isQueryAll;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

}
