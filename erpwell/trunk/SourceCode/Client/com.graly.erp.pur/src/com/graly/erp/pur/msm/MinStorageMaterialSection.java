package com.graly.erp.pur.msm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MinStorageMaterialSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MinStorageMaterialSection.class);

	protected TableListManager listTableManager;
	protected MinStorageMaterialDialog msmDialog;
	protected List<RequisitionLine> input;
	protected ToolItem itemGenPr;
	protected Long materialRrn;
	protected String materialId;
	protected List<Material> materials;
	protected boolean isQueryAll = false;
	
	protected PURManager purManager;

	public MinStorageMaterialSection(TableListManager listTableManager) {
		super(null);
		this.listTableManager = listTableManager;
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemGenPr(tBar);
		createToolItemSearch(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemGenPr(ToolBar tBar) {
		itemGenPr = new ToolItem(tBar, SWT.PUSH);
		itemGenPr.setText(Message.getString("pur.gen_to_pr"));
		itemGenPr.setImage(SWTResourceCache.getImage("merge"));
		itemGenPr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				genPrAdapter();
			}
		});
	}
	
	protected void createNewViewer(Composite client, final IManagedForm form){
		viewer = listTableManager.createViewer(client, form.getToolkit());
	}
	
	protected void genPrAdapter() {
		try {
			CheckboxTableViewer cv = (CheckboxTableViewer)viewer;
			List<RequisitionLine> toPrLines = new ArrayList<RequisitionLine>();
			for(Object obj : cv.getCheckedElements()) {
				if(obj instanceof RequisitionLine) {
					toPrLines.add((RequisitionLine)obj);
				}
			}
			if(toPrLines.size() > 0) {
				if(purManager == null)
					purManager = Framework.getService(PURManager.class);
				Requisition pr = purManager.generatePrByMin(Env.getOrgRrn(), toPrLines, Env.getUserRrn());
				UI.showInfo(String.format(Message.getString("pur.generate_pr_success"), pr.getDocId()));
				
				input.removeAll(toPrLines);
				viewer.setInput(input);
				((CheckboxTableViewer)viewer).setAllChecked(true);
				listTableManager.updateView(viewer);
				createSectionDesc(section);
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else if(this.msmDialog != null && msmDialog.getEntityQueryDialog() != null) {
			queryDialog = msmDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			MinStorageMaterialDialog msmDialog = new MinStorageMaterialDialog(false);
			queryDialog = msmDialog.new MsmQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			msmDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}
	
	public void refresh(){
		// 调用供应商评估方法
		try {
			MsmProgressMonitorDialog progressDiglog = new MsmProgressMonitorDialog(
					UI.getActiveShell(), "");
			if(materials != null && materials.size() > 0) {
				progressDiglog.run(true, true, progressDiglog.createProgress(materials));
				input = new ArrayList<RequisitionLine>();
				for(RequisitionLine prLine : progressDiglog.getMinStoragePrLines()) {
					if(prLine != null)
						input.add(prLine);
				}
				if(progressDiglog.getErrlogs() != null && progressDiglog.getErrlogs().size() > 0) {
					if(UI.showConfirm(String.format(Message.getString("pur.min_storage_pr_material_errs"), progressDiglog.getErrlogs().size()))) {
						ErrorMsgDisplayDialog dialog = new ErrorMsgDisplayDialog(progressDiglog.getErrlogs(), UI.getActiveShell());
						dialog.open();
					}
				}
			} else {
				UI.showError(Message.getString("wip.has_not_purchase_material"));
			}
			
			viewer.setInput(input);
			((CheckboxTableViewer)viewer).setAllChecked(true);
			listTableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e){
			logger.error("EntityBlock : refresh()  ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
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
		if(dialog instanceof MinStorageMaterialDialog) {
			this.msmDialog = (MinStorageMaterialDialog)dialog;
		} else {
			this.msmDialog = null;
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
