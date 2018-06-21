package com.graly.erp.wip.mo.create;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
/**
 * @author Administrator
 * 生成MO向导页最后一个页面，如果为Drafted状态则可以手工编辑开始和结束日期
 */
public class SubMOLinePage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(SubMOLinePage.class);
	private static String PREVIOUS = "moBomList";
	private SubMOLineSection section;
	private MOGenerateWizard wizard;
	// 无限供应物料对应的Bom
	private List<ManufactureOrderBom> jitBoms;
	private Map<RequisitionLine, ManufactureOrderBom> bomMap;
	public static Map<ManufactureOrderLine, ManufactureOrderBom> moLineBomMap;
	
	public SubMOLinePage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (MOGenerateWizard)wizard;
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
		section = new SubMOLineSection(adTable, this);
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
					List<DocumentationLine> doLines= null;
					doLines = wipManager.getMoLine(mo, boms, Env.getUserRrn());
					if(mo.getIsPrepareMo()){
						for(ManufactureOrderBom prepareBom : boms){
							if(prepareBom.getIsPrepareMoLine()){
								prepareBom.setDateStart(null);
								prepareBom.setDateEnd(null);
								prepareBom.setDuration(null);
								prepareBom.setStandTime(null);
							}
						}
					}
					for(DocumentationLine doLine : doLines) {
						if(doLine != null) {
							if(doLine.getDateEnd() == null || doLine.getDateStart() == null) {
								isSchedule = false;
								//童庆飞新增如果开始和完成日期为空，那么完成日期就是MO的完成日期，开始日期就是MO的开始日期
								if(doLine instanceof ManufactureOrderLine  && doLine.getMaterialRrn().equals(mo.getMaterialRrn())){
									ManufactureOrderLine moline= (ManufactureOrderLine) doLine;
									if(moline.getPathLevel()==0L){
										Date end = mo.getDateEnd();
										Date start = mo.getDateStart();
										if(end != null) {
											end.setHours(17);
											end.setMinutes(30);
											end.setSeconds(0);
										}
										if(start != null){
											Date date = Env.getSysDate();
											start.setHours(date.getHours());
											start.setMinutes(date.getMinutes());
											start.setSeconds(date.getSeconds());
										}
										if(Env.getOrgRrn() ==139420L && moline.getMaterial().getMeter()!=null){//节拍处理
										}else{
											doLine.setDateStart(start);
											doLine.setDateEnd(end);
										}
										
									}
								}
							}
							dtLines.add(doLine);
						}
					}
					if(wizard.isCanEdit() && dtLines != null ) {
						setPageComplete(true);
					}
					// 将无需采购和生产的Bom加到dtLines中,并按树状顺序存储到treeDocLines中
					List<DocumentationLine> treeDocLines = creatTreeDocLine(boms, dtLines, mo);
					
					MOLineItemAdapter.setMOLines(treeDocLines);
					MOLineItemAdapter.setBomMap(bomMap);
					MOLineItemAdapter.setMoLineBomMap(moLineBomMap);
					section.refreshTreeContent(treeDocLines);
					section.initGanttChartContent(mo, treeDocLines);
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
				((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.finish"));
			}			
		}
	}
	
	private List<DocumentationLine> creatTreeDocLine(List<ManufactureOrderBom> boms,
			List<DocumentationLine> dtLines, ManufactureOrder mo) {
		List<DocumentationLine> treeDocLines = new ArrayList<DocumentationLine>();
		jitBoms = new ArrayList<ManufactureOrderBom>();
		bomMap = new LinkedHashMap<RequisitionLine, ManufactureOrderBom>();
		moLineBomMap = new LinkedHashMap<ManufactureOrderLine, ManufactureOrderBom>();
		boolean isEdit = false;
		if(mo != null && mo.getObjectRrn() != null) {
			isEdit = true;
		}
		DocumentationLine docLine;
		for(ManufactureOrderBom moBom : boms) {
			docLine = null;
			for(DocumentationLine line : dtLines) {
				if(line instanceof RequisitionLine) {
					if(isEdit) {
						if(mo.getIsPrepareMo()){
							if(line.getObjectRrn()!=null){
								if(line.getObjectRrn().equals(moBom.getRequsitionLineRrn())) {
									docLine = line;
									dtLines.remove(line);
									break;
								}
							}else if(moBom.getObjectRrn().equals(((RequisitionLine)line).getMoBomRrn())) {
								docLine = line;
								dtLines.remove(line);
								break;
							}
						}else{
							if(line.getObjectRrn().equals(moBom.getRequsitionLineRrn())) {
								docLine = line;
								dtLines.remove(line);
								break;
							}
						}
					} else {
						if(moBom.getObjectRrn().equals(((RequisitionLine)line).getMoBomRrn())) {
							docLine = line;
							dtLines.remove(line);
							break;
						}
					}
				} else if(line instanceof ManufactureOrderLine) {
					if(isEdit) {
						if(mo.getIsPrepareMo()){
							if(line.getObjectRrn()!=null){
								if(line.getObjectRrn().equals(moBom.getMoLineRrn())) {
									docLine = line;
									dtLines.remove(line);
									break;
								}
							}else{
								if(moBom.getObjectRrn().equals(((ManufactureOrderLine)line).getMoBomRrn())) {
									docLine = line;
									dtLines.remove(line);
									break;
								}
							}
						}else{
							if(line.getObjectRrn().equals(moBom.getMoLineRrn())) {
								docLine = line;
								dtLines.remove(line);
								break;
							}
						}
					} else {
						if(moBom.getObjectRrn().equals(((ManufactureOrderLine)line).getMoBomRrn())) {
							docLine = line;
							dtLines.remove(line);
							break;
						}
					}
				}				
//				if(line.getMaterialRrn().equals(moBom.getMaterialRrn())) {
//					if(moBom.getPath() != null) {
//						if(moBom.getPath().equals(getPath(line))) {
//							docLine = line;
//							dtLines.remove(line);
//							break;
//						}
//					}
//					// 否则若为根物料，既是MO对应的line
//					else if(getPath(line) == null) {
//						docLine = line;
//						break;
//					}
//				}
			}
			
			if(docLine instanceof RequisitionLine) {
				bomMap.put((RequisitionLine)docLine, moBom);
			} else if(docLine instanceof ManufactureOrderLine) {
				moLineBomMap.put((ManufactureOrderLine)docLine, moBom);
			}
			
			if(docLine != null) {
				treeDocLines.add(docLine);
			}
			// Bom为无限供料时(即无需采购也无需生成子工作令)
			// if(moBom.getMoLineRrn() == null && moBom.getRequsitionLineRrn() == null)
			else {
				// 将其开始和结束时间都设为MO的开始时间
				jitBoms.add(moBom);
//				moBom.setDateStart(mo.getDateStart());
//				moBom.setDateEnd(mo.getDateStart());
				treeDocLines.add(moBom);
			}
		}
		return treeDocLines;
	}
	
	public String doPrevious() {
		this.setErrorMessage(null);
		((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.next"));
		return PREVIOUS;
	}
	
	public IWizardPage getPreviousPage() {
		return wizard.getPage(PREVIOUS);
	}
	
	@Override
	public String doNext() {
		List<DocumentationLine> doLines = section.getMOLines();
		ManufactureOrder mo = wizard.getContext().getManufactureOrder();
		// 如果MoLine的dateStart或dateEnd为null时,则不保存此MoLine
		List<DocumentationLine> filterLines = new ArrayList<DocumentationLine>();
		for(DocumentationLine docLine : doLines) {
			if(docLine instanceof ManufactureOrderLine) {
				if(docLine.getDateStart() != null && docLine.getDateEnd() != null) {
					filterLines.add(docLine);
				} else {
//					if(mo.getMaterialRrn().equals(docLine.getMaterialRrn())) {
					// 若新建的MO或MoLine的开始或完成日期为null，并且对应的物料不是无限供应物料，则提示不能保存
					if(!isJitMaterial(docLine)) {
						this.setErrorMessage(String.format(Message.getString("wip.mo_date_is_null"), docLine.getMaterialName()));
						return "";						
					}
				}
			} else {
				filterLines.add(docLine);
			}
		}
		if(filterLines == null || filterLines.size() == 0) return "";
		wizard.getContext().setDoLines(filterLines);
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
