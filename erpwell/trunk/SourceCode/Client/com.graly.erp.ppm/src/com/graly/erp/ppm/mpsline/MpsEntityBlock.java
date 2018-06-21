package com.graly.erp.ppm.mpsline;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.ChildEntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MpsEntityBlock extends ChildEntityBlock {
	private static final Logger logger = Logger.getLogger(MpsEntityBlock.class);
	private static String TABLE_NAME_MPSLINEBOM = "PPMMpsLineBom";
	protected ToolItem itemDelSelect;
	private ToolItem itemBomTree;
	protected ToolItem generatePp;
	protected ToolItem generateMps; // 生成主生产计划
	protected ToolItem itemUpload;
	protected boolean flag = true;
	protected CheckboxTableViewer tViewer;
	
	private MpsLine selectedMpsLine;
	private ADTable adTable;
	
	enum GenType {MpsLine_Gen, PP_Gen}

	public MpsEntityBlock(EntityTableManager tableManager) {
		this(tableManager, " 1 <> 1 ", null);
	}

	public MpsEntityBlock(EntityTableManager tableManager, String whereClause, Object parentObject) {
		super(tableManager, whereClause, parentObject);
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
	//	tableManager.setPrLineBlock(this);
		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
	//	setParenObjectStatusChanged();
		changedLineCheckBox();
	}

	private void changedLineCheckBox() {
		if (viewer instanceof CheckboxTableViewer) {
			tViewer = (CheckboxTableViewer) viewer;
			tViewer.addCheckStateListener(new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					if(tViewer.getCheckedElements().length == 0){
						itemDelSelect.setEnabled(false);
					}else{
						itemDelSelect.setEnabled(true);
					}
				}
			});
		}
	}
	
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void setSelectionRequisition(Object firstElement) {
		if (firstElement instanceof MpsLine) {
			selectedMpsLine = (MpsLine) firstElement;
		} else {
			selectedMpsLine = null;
		}
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemUpload(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemGenerateMps(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExecuteMps(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelSelect(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemBomTree(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemUpload(ToolBar tBar) {
		String KEY_MPS_IMPORT = "PPM.MPS.Import";
		itemUpload = new AuthorityToolItem(tBar, SWT.PUSH, KEY_MPS_IMPORT);
		itemUpload.setText(Message.getString("ppm.upload"));
		itemUpload.setImage(SWTResourceCache.getImage("receive"));
		itemUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				uploadAdapter();
			}
		});
	}
	protected void createToolItemDelSelect(ToolBar tBar) {
		itemDelSelect = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PPM_DELETESELECTMPSLINE);
		itemDelSelect.setText(Message.getString("common.delselect"));
		itemDelSelect.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteSelectAdapter();
			}
		});
	}
	
	protected void createToolItemBomTree(ToolBar tBar) {
		itemBomTree = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MPS_BOM);
		itemBomTree.setText(Message.getString("pdm.bom"));
		itemBomTree.setImage(SWTResourceCache.getImage("bomtree"));
		itemBomTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				bomTreeAdapter();
			}
		});
	}

	protected void createToolItemGenerateMps(ToolBar tBar) {
		generateMps = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MPS_EXECUTE);
		generateMps.setText(Message.getString("inv.gen_mps"));
		generateMps.setImage(SWTResourceCache.getImage("feature"));
		generateMps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				generateMpsAdapter();
			}
		});
	}

	protected void createToolItemExecuteMps(ToolBar tBar) {
		generatePp = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MPS_TOMO);
		generatePp.setText(Message.getString("common.execute_productplan"));
		generatePp.setImage(SWTResourceCache.getImage("feature"));
		generatePp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				generatePpAdapter();
			}
		});
	}

	protected void bomTreeAdapter() {
		if (selectedMpsLine != null) {
			adTable = getMpsLineBomTable(TABLE_NAME_MPSLINEBOM);
			BomTreeDialog bomTreeDialog = new BomTreeDialog(UI.getActiveShell(), adTable, selectedMpsLine);
			if (bomTreeDialog.open() == Dialog.OK) {
				this.refresh();
			}
		}
	}
	
	protected void deleteSelectAdapter() {	
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				Table table =tViewer.getTable();
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					if(item.getChecked()){
						Object obj = item.getData();
						if (obj instanceof MpsLine) {
							MpsLine mpsLine = (MpsLine)obj;
							ADManager entityManager = Framework.getService(ADManager.class);
							entityManager.deleteEntity(mpsLine);
						}
					}
				}
				refresh();
				itemDelSelect.setEnabled(false);
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}		
	}
	
	protected void generateMpsAdapter() {
		try {
			if(this.isGenerateMps()) {
				MpsGenerateProgress mpsProgress = new MpsGenerateProgress((Mps)getParentObject(), this);
				MpsProgressDialog progressDialog = new MpsProgressDialog(UI.getActiveShell());
				progressDialog.run(true, true, mpsProgress);
				if(mpsProgress.isFinished()) {
					ADManager manager = Framework.getService(ADManager.class);
					Object obj = manager.getEntity((Mps)this.getParentObject());
					this.setParentObject(obj);
//					MpsProperties page = (MpsProperties) this.detailsPart.getCurrentPage();
//					page.setParentObject(obj);
					if(mpsProgress.isSuccess()) {
						UI.showInfo(Message.getString("wip.generate_mps_success"));						
					} else {
						List<PasErrorLog> errlogs = mpsProgress.getErrlogs();
						boolean viewErr = UI.showConfirm(String.format(Message.getString("ppm.gen_mpsline_has_error"), errlogs.size()));
						if(viewErr) {
							ErrorLogDisplayDialog dialog = new ErrorLogDisplayDialog(errlogs, UI.getActiveShell());
							dialog.open();
						}
					}
					refresh();
				}
			}
		} catch(Exception e) {
			logger.error("Error at MpsEntityBlock : generateMpsAdapter", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected void generatePpAdapter() {
		try {
			if(this.isGenerateMpsLine()) {
				MpsLineGenerateProgress mpsProgress = new MpsLineGenerateProgress((Mps)getParentObject(), this);
				MpsProgressDialog progressDialog = new MpsProgressDialog(UI.getActiveShell());
				progressDialog.run(true, true, mpsProgress);
				if(!mpsProgress.isVerify()) {
					UI.showError(mpsProgress.getVerifyErrorInfo());
					return;
				}
				if(mpsProgress.isFinished()) {
					ADManager manager = Framework.getService(ADManager.class);
					Object obj = manager.getEntity((Mps)this.getParentObject());
					this.setParentObject(obj);
//					MpsProperties page = (MpsProperties) this.detailsPart.getCurrentPage();
//					page.setParentObject(obj);
					if(mpsProgress.isSuccess()) {
						UI.showInfo(Message.getString("wip.generate_mo_success"));					
					} else {
						List<PasErrorLog> errlogs = mpsProgress.getErrlogs();
						boolean viewErr = UI.showConfirm(String.format(Message.getString("ppm.gen_mo_has_error"), errlogs.size()));
						if(viewErr) {
							ErrorLogDisplayDialog dialog = new ErrorLogDisplayDialog(errlogs, UI.getActiveShell());
							dialog.open();
						}
					}
					refresh();
				}
			}
		} catch(Exception e) {
			logger.error("Error at MpsEntityBlock : generatePpAdapter", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected ADTable getMpsLineBomTable(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			return adTable;
		} catch (Exception e) {
			logger.error("ActionViewerSection : initAdTableOfAction()", e);
			return null;
		}
	}
	
	/*
	 * 将更新后的mps重新保存到数据库中
	 */
	private void saveMpsToDB(Mps mps) {
		try {
			ADManager manager = Framework.getService(ADManager.class);
			manager.saveEntity(mps, Env.getUserRrn());
			mps = (Mps)manager.getEntity(mps);
			this.setParentObject(mps);
		} catch (Exception e) {
			logger.error("Error at MpsEntityBlock : setSaveMps", e);
		}
	}
	
	/* 
	 * 判断是否正在生成主计划和生产计划
	 * @Param: mpsORmpsLine为1设置正在生成MPS, 为2设置正在生成MpsLine
	 */
	private boolean isProgressing(GenType type) {
		try {
			Mps mps = (Mps) getParentObject();
			if (mps.getObjectRrn() != null) {
				ADManager manager = Framework.getService(ADManager.class);
				mps = (Mps)manager.getEntity(mps);
				// 判断此主计划和生产计划是否正在生成
				switch(type) {
					case MpsLine_Gen: {
						if(mps.getIsProcessingMps() || mps.getIsProcessingPp()) {
							return true;
						} else {
							mps.setIsProcessingMps(true);
							break;
						}
					}
					case PP_Gen: {
						if(mps.getIsProcessingMps() || mps.getIsProcessingPp()) {
							return true;
						} else {
							mps.setIsProcessingPp(true);
							break;
						}
					}
				}
				saveMpsToDB(mps);
				return false;
			}
		} catch (Exception e) {
			logger.error("Error at MpsEntityBlock : isProgressing", e);
		}
		return true;
	}
	
	/* 
	 * 返回与mps对应modelClass的实体列表,用于判断是否已生成MO或PR
	 */
	private List<?> isValue(Class<?> modelClass)  {
		try {
			Mps mps = (Mps) getParentObject();
			ADManager manager = Framework.getService(ADManager.class);
			String whereClause = " mpsRrn = " + mps.getObjectRrn()
				+ " AND (docStatus = '" + Documentation.STATUS_APPROVED + "' OR docStatus = '" + Documentation.STATUS_DRAFTED + "') ";
			List<?> mos = manager.getEntityList(Env.getOrgRrn(), modelClass, Env.getMaxResult(), whereClause, null);
			return mos;
		} catch (Exception e) {
			logger.error("Error at MpsEntityBlock : isValue", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}

	private boolean isGenerateMps() {
		try {
			if (!isProgressing(GenType.MpsLine_Gen)) {
				Mps mps = (Mps) getParentObject();
				ADManager manager = Framework.getService(ADManager.class);
				List<ADBase> list = manager.getEntityList(Env.getOrgRrn(), tableManager.getADTable().getObjectRrn(),
						Env.getMaxResult(), getWhereClause(), null);
				if (list != null && list.size() > 0) {
					// 提示已生成Mps,是否继续
					boolean confirmContinue = UI.showConfirm(Message.getString("ppm.mpsLine_exsit_is_continue"));
					if (confirmContinue) {
						// 判断是否已生此主计划的MO, 若存在这样的MO提示不能重新生成MPS
						List<ManufactureOrder> mos = (List<ManufactureOrder>)this.isValue(ManufactureOrder.class);
						if(mos != null && mos.size() > 0) {
							UI.showError(String.format(Message.getString("ppm.mps_has_trans_to_mo"), mps.getMpsId()));
							mps.setIsProcessingMps(false);
							this.saveMpsToDB(mps);
							return false;
						}
					} else {
						mps.setIsProcessingMps(false);
						this.saveMpsToDB(mps);
						return false;
					}
				}
				return true;
			} else {
				UI.showInfo(String.format(Message.getString("ppm.mpsline_is_generating"), ((Mps) getParentObject()).getMpsId()));
			}
		} catch (Exception e) {
			logger.error("Error at MpsEntityBlock : isGenerateMps", e);
			ExceptionHandlerManager.asyncHandleException(e);
			Mps mps = (Mps) getParentObject();
			mps.setIsProcessingMps(false);
			this.saveMpsToDB(mps);
		}
		return false;
	}
	
	private boolean isGenerateMpsLine() {
		try {
			if (!isProgressing(GenType.PP_Gen)) {
				Mps mps = (Mps) getParentObject();
				// 判断是否已生此主计划的PR, 若存在则只有在删除对应的PR后才能重新生成MpsLine
				List<Requisition> prs = (List<Requisition>)this.isValue(Requisition.class);
				if(prs != null && prs.size() > 0) {
					UI.showError(String.format(Message.getString("ppm.mps_has_trans_to_pr"), mps.getMpsId()));
					mps.setIsProcessingPp(false);
					this.saveMpsToDB(mps);
					return false;
				}
				
				// 判断是否已生此主计划的MO, 若存在则只有在删除对应的MO后才能重新生成MPS
				List<ManufactureOrder> mos = (List<ManufactureOrder>)this.isValue(ManufactureOrder.class);
				if(mos != null && mos.size() > 0) {
					UI.showError(String.format(Message.getString("ppm.mps_has_trans_to_mo"), mps.getMpsId()));
					mps.setIsProcessingPp(false);
					this.saveMpsToDB(mps);
					return false;
				}
				return true;
			} else {
				UI.showInfo(String.format(Message.getString("ppm.pp_is_generating"), ((Mps) getParentObject()).getMpsId()));
			}
		} catch (Exception e) {
			logger.error("Error at MpsEntityBlock : isGenerateMpsLine", e);
			ExceptionHandlerManager.asyncHandleException(e);
			Mps mps = (Mps) getParentObject();
			mps.setIsProcessingPp(false);
			this.saveMpsToDB(mps);
		}
		return false;
	}

	private void uploadAdapter() {
		try {
			final Mps mps =(Mps) getParentObject();
			if(reservedDateCompare(mps).intValue() >= 0){
				UI.showInfo(Message.getString("ppm.datereserve"));
				return;
			}
			if(mps.getIsProcessingPp()){
				UI.showInfo(String.format(Message.getString("ppm.pp_is_generating"),mps.getMpsId()));
				return;
			}
			FileDialog fileDialog = new FileDialog(UI.getActiveShell(), SWT.OPEN);
			// 设置初始路径
			fileDialog.setFilterPath("C:/");
			// 设置扩展名过滤
			String[] filterExt = { "*.xls"};
			fileDialog.setFilterExtensions(filterExt);
			// 打开文件对话框，返回选择的文件
			String selectedFile = fileDialog.open();
			
			if (selectedFile != null) {
				if (!selectedFile.contains(".xls")) {
					UI.showWarning(Message.getString("ppm.upload_file_type_not_support"));
					return;
				}
				MpsProgressDialog progressDialog = new MpsProgressDialog(UI.getActiveShell());
				final MpsChangeQtyImportProgress progress = new MpsChangeQtyImportProgress((Mps)getParentObject(),
						selectedFile, this.getTableManager().getADTable(), this);
				progressDialog.run(true, true, progress);
				
				if (progress.isFinished()) {
					if(progress.isSuccess()) {//后台方法1.主计划下的所有行的生产数量置为0。2.EXCEL中的生产数量替代主计划行的数量。3.计划中不存在予以添加
						MpsImportProgressDialog mpsImportDialog= new MpsImportProgressDialog(Display.getCurrent().getActiveShell());
						IRunnableWithProgress runable = new IRunnableWithProgress(){
							@Override
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {
								monitor.setTaskName("系统正在更新数据请稍作等待......");
								try{
									PPMManager ppmManager = Framework.getService(PPMManager.class);
									Map<Long,MpsLine> mpsLinesMap = new HashMap<Long,MpsLine>();
									for(MpsLine mpsLine : progress.getMpsLines()){
										mpsLinesMap.put(mpsLine.getObjectRrn(), mpsLine);
									}
									ppmManager.importMpsLine(Env.getOrgRrn(), Env.getUserRrn(), mps, mpsLinesMap, 
											progress.getPerMpsLines());
									monitor.done();
								}catch(Exception e){
									
								}

							}
						};
						mpsImportDialog.run(true, true, runable);
						//UI.showInfo(Message.getString("inv.import_successed"));
						UI.showInfo(Message.getString("ppm.upload_successful"));	
						if(progress.getPerMpsLines()!=null && progress.getPerMpsLines().size() > 0 ){
							MpsExcelPersistDialog excelDialog = new MpsExcelPersistDialog(UI.getActiveShell(),this.form,
									this.getTableManager().getADTable(),progress.getPerMpsLines());
							excelDialog.open();
						}
					} else {
						List<PasErrorLog> errlogs = progress.getErrLogs();
						boolean viewErr = UI.showConfirm(String.format(Message.getString("ppm.upload_data_has_error"), errlogs.size()));
						if(viewErr) {
							ErrorLogDisplayDialog dialog = new ErrorLogDisplayDialog(errlogs, UI.getActiveShell());
							dialog.open();
						}
					}
				}
				refreshAdapter();
			}
		} catch(Exception e) {
			logger.error("Error at MpsEntityBlock : uploadAdapter() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	public void setParentObject(Object parentObject) {
		super.setParentObject(parentObject);
		statusChanged(((Mps) getParentObject()).isFrozen());
	}

	protected void statusChanged(boolean isFrozen) {
		if (isFrozen) {
			generateMps.setEnabled(false);
			generatePp.setEnabled(false);
			itemBomTree.setEnabled(false);
			itemDelSelect.setEnabled(false);
//			itemUpload.setEnabled(false);
		} else {
			generateMps.setEnabled(true);
			generatePp.setEnabled(true);
			itemBomTree.setEnabled(true);
			itemDelSelect.setEnabled(true);
//			itemUpload.setEnabled(true);
		}
	}
	
	@Override
	public void refresh(){
		super.refresh();
		Table table = ((TableViewer)viewer).getTable();
		MpsLine totalMpsLine = new MpsLine();
		totalMpsLine.setMpsId(Message.getString("inv.total"));
		BigDecimal totalQty = BigDecimal.ZERO;
		BigDecimal totalQtySalePlan = BigDecimal.ZERO;
		BigDecimal totalQtyAvailable = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof MpsLine) {
				MpsLine inMpsLine = (MpsLine)obj;
				if (inMpsLine.getQtyMps() != null) {
					totalQty = totalQty.add(inMpsLine.getQtyMps());
				}
				if (inMpsLine.getQtySalePlan() != null) {
					totalQtySalePlan = totalQtySalePlan.add(inMpsLine.getQtySalePlan());
				}
				if (inMpsLine.getQtyAvailable() != null) {
					totalQtyAvailable = totalQtyAvailable.add(inMpsLine.getQtyAvailable());
				}
			}
		}
		totalMpsLine.setQtyMps(totalQty);
		totalMpsLine.setQtySalePlan(totalQtySalePlan);
		totalMpsLine.setQtyAvailable(totalQtyAvailable);
		TableViewer tv = (TableViewer)viewer;
		tv.insert(totalMpsLine, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}	
	
	public Integer reservedDateCompare(Mps planSetup) {
		Integer result = null;
		try {
			Date date = planSetup.getDateReserved();
			Date now = Env.getSysDate();
			return now.compareTo(date);
		} catch (Exception e) {
			logger.error("EntityBlock : createSectionDesc ", e);
			return result;
		}
	}
}