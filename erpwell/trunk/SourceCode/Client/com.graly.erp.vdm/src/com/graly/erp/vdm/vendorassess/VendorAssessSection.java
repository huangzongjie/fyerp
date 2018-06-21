package com.graly.erp.vdm.vendorassess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.VendorAssessment;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class VendorAssessSection extends MasterSection implements IVdmAssess{
	private static final Logger logger = Logger.getLogger(VendorAssessSection.class);

	protected TableListManager listTableManager;
	private Long vendorRrn;
	private Long materialRrn;
	private String purchaser;
	private Date startDate;
	private Date endDate;
	protected VendorAssessDialog assessDialog;
	protected List<VendorAssessment> input;

	protected VendorAssessment selectedVdmAssess;
	
	public VendorAssessSection(TableListManager listTableManager) {
		super(null);
		this.listTableManager = listTableManager;
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
		section.setTextClient(tBar);
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else if(this.assessDialog != null && assessDialog.getEntityQueryDialog() != null) {
			queryDialog = assessDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			VendorAssessDialog vaDialog = new VendorAssessDialog(false);
			queryDialog = vaDialog.new VendorAssessQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			vaDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}
	
	protected void createNewViewer(Composite client, final IManagedForm form){
		viewer = listTableManager.createViewer(client, form.getToolkit());
	}
	
	public void refresh(){
		// 调用供应商评估方法
		try{
			input = new ArrayList<VendorAssessment>();
			if(startDate != null && endDate != null) {
				VDMManager vdmManager = Framework.getService(VDMManager.class);
				if(vendorRrn != null && materialRrn != null) {
					String whereClause = " materialRrn = " + materialRrn + " AND vendorRrn = " +vendorRrn + " ";
					ADManager manager = Framework.getService(ADManager.class);
					List<VendorMaterial> vms = manager.getEntityList(Env.getOrgRrn(), VendorMaterial.class,
							Env.getMaxResult(), whereClause, null);
					if(vms == null || vms.size() == 0) {
						UI.showError(Message.getString("vdm.vendor_material_not_match"));
						input = null;
					} else {
						input.add(vdmManager.generateVendorAssessment(
								Env.getOrgRrn(), materialRrn, vendorRrn, purchaser, startDate, endDate));
					}
				} else {
					VendorAssessProgressDialog progressDiglog = new VendorAssessProgressDialog(UI.getActiveShell(), "");
					progressDiglog.run(true, true,
							progressDiglog.createProgress(materialRrn, vendorRrn, purchaser, startDate, endDate));
					
					if(!progressDiglog.isFinished()) {
						if(progressDiglog.isNoVnedors()) 
							// 在评估时间段内该物料没有采购
							UI.showError(Message.getString("vdm.has_not_vendor"));
						else if(progressDiglog.isNoMaterials())
							// 在评估时间段内没有从该供应商采购物料
							UI.showError(Message.getString("vdm.has_not_material"));
						else {
							// 评估时间段内该采购员没有采购物料
							UI.showError(Message.getString("vdm.purchaser_has_not_vendor"));
						}
					}
					input = progressDiglog.getVendorAssesses();
				}

				viewer.setInput(input);
				listTableManager.updateView(viewer);
				createSectionDesc(section);				
			}
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
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

	@Override
	public void setEndDate(Date dateEnd) {
		this.endDate = dateEnd;
	}

	@Override
	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	@Override
	public void setStartDate(Date dateStart) {
		this.startDate = dateStart;
	}

	@Override
	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}
	
	@Override
	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	protected ADTable getADTable() {
		return listTableManager.getADTable();
	}
	
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof VendorAssessDialog) {
			this.assessDialog = (VendorAssessDialog)dialog;
		} else {
			this.assessDialog = null;
		}
	}

}
