package com.graly.erp.pur.request.refmo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardDialog;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class SubMoLinePage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(SubMoLinePage.class);
	private static String PREVIOUS = "moBomList";
	private SubMoLineSection section;
	private MoViewWizard wizard;
	// 无限供应物料对应的Bom
	private List<ManufactureOrderBom> jitBoms;
	
	public SubMoLinePage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (MoViewWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {
		ADTable adTable = wizard.getContext().getAdTable_MOLine();
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));
		
		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		
		Composite composite = null;
		FormToolkit toolkit = null;
		toolkit = managedForm.getToolkit();
		composite = toolkit.createComposite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
        // create section
		section = new SubMoLineSection(adTable, this);
		section.createContents(managedForm, composite);
		setPageComplete(false);
		setControl(composite);
		updateLocalPageContent();
	}
	
	public void updateLocalPageContent() {
		if(section != null) {
			ManufactureOrder mo = wizard.getContext().getManufactureOrder();
			List<ManufactureOrderBom> boms = wizard.getContext().getMoBoms();
			try {
				if(mo != null) {
					List<DocumentationLine> dtLines = new ArrayList<DocumentationLine>();
					boolean isSchedule = true;
					WipManager wipManager = Framework.getService(WipManager.class);
					for(DocumentationLine doLine : wipManager.getMoLine(mo, boms, Env.getUserRrn())) {
						if(doLine != null) {
							if(doLine.getDateEnd() == null || doLine.getDateStart() == null) {
								isSchedule = false;
							}
							dtLines.add(doLine);
						}
					}
					// 将无需采购和生产的Bom加到dtLines中,并按树状顺序存储到treeDocLines中
					List<DocumentationLine> treeDocLines = creatTreeDocLine(boms, dtLines, mo);
					
					MoLineItemAdapter.setMOLines(treeDocLines);
					section.refreshTreeContent(treeDocLines);
					if(wizard.isCanEdit() && dtLines != null && dtLines.size() > 0) {
						setPageComplete(true);
					}
					if(!isSchedule) {
						// 提示系统排程失败
						this.setErrorMessage(Message.getString("wip.can_not_schedule_mo"));
					}
				}
			} catch(Exception e) {
				logger.error("SubMOLinePage : updateLocalPageContent() ", e);
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			} finally {
				((MoViewDialog)wizard.getContainer()).setButtonEnabled(IDialogConstants.NEXT_ID, false);
				((FlowWizardDialog)wizard.getContainer())
					.updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.finish"));
			}			
		}
	}
	
	private List<DocumentationLine> creatTreeDocLine(List<ManufactureOrderBom> boms,
			List<DocumentationLine> dtLines, ManufactureOrder mo) {
		List<DocumentationLine> treeDocLines = new ArrayList<DocumentationLine>();
		jitBoms = new ArrayList<ManufactureOrderBom>();
		DocumentationLine docLine;
		for(ManufactureOrderBom moBom : boms) {
			docLine = null;
			for(DocumentationLine line : dtLines) {
				if(line.getMaterialRrn().equals(moBom.getMaterialRrn())) {
					if(moBom.getPath() != null) {
						if(moBom.getPath().equals(getPath(line))) {
							docLine = line;
							break;
						}
					}
					// 否则若为根物料，既是MO对应的line
					else if(getPath(line) == null) {
						docLine = line;
						break;
					}
				}
			}
			if(docLine != null) {
				treeDocLines.add(docLine);
			}
			// Bom为无限供料时(即无需采购也无需生成子工作令)
			// if(moBom.getMoLineRrn() == null && moBom.getRequsitionLineRrn() == null)
			else {
				// 将其开始和结束时间都设为MO的开始时间
				jitBoms.add(moBom);
				treeDocLines.add(moBom);
			}
		}
		return treeDocLines;
	}
	
	public String doPrevious() {
		this.setErrorMessage(null);
		((MoViewDialog)wizard.getContainer()).setButtonEnabled(IDialogConstants.NEXT_ID, true);
		((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.next"));
		return PREVIOUS;
	}
	
	public IWizardPage getPreviousPage() {
		return wizard.getPage(PREVIOUS);
	}
	
	@Override
	public String doNext() {
		return "finish";
	}
	
	// 判断DocumentationLine对应的物料是否为无限供应物料
	protected boolean isJitMaterial(DocumentationLine docLine) {
		if(jitBoms != null) {
			for(ManufactureOrderBom moBom : jitBoms) {
				if(docLine.getMaterialRrn().equals(moBom.getMaterialRrn())) {
					if(moBom.getPath() != null) {
						if(moBom.getPath().equals(getPath(docLine))) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
    }
	
	private String getPath(DocumentationLine line) {
		if(line instanceof ManufactureOrderLine) {
			return ((ManufactureOrderLine)line).getPath();				
		}
		if(line instanceof RequisitionLine) {
			return ((RequisitionLine)line).getPath();				
		}
		if(line instanceof ManufactureOrderBom) {
			return ((ManufactureOrderBom)line).getPath();				
		}
		return null;
	}
}
