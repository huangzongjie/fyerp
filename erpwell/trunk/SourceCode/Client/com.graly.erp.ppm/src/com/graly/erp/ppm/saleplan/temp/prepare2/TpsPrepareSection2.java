package com.graly.erp.ppm.saleplan.temp.prepare2;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.MaterialQueryDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.erp.ppm.mpsline.ErrorLogDisplayDialog;
import com.graly.erp.ppm.mpsline.MpsDataImportProgress;
import com.graly.erp.ppm.mpsline.MpsProgressDialog;
import com.graly.erp.ppm.saleplan.temp.prepare.PrepareTempSaleDataImportProgress;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class TpsPrepareSection2 extends MasterSection {
	private static final Logger logger = Logger.getLogger(TpsPrepareSection2.class);
	protected ToolItem itemEdit;//编辑
	protected ToolItem itemDelete;//删除
	protected ToolItem itemUpload;//上传
	protected TpsLinePrepare selectedTpsLinePrepare;
	private long count = 0;
	
	public TpsPrepareSection2(EntityTableManager tableManager){
		super(tableManager);
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionRequisition(ss.getFirstElement());
	    		editAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
		    		setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	private void setSelectionRequisition(Object obj) {
		if (obj instanceof TpsLinePrepare) {
			selectedTpsLinePrepare = (TpsLinePrepare) obj;
		} else {
			selectedTpsLinePrepare = null;
		}
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemEditor(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemDelete(tBar);
		createToolItemUpload(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	


	
	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}
	
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
//				deleteAdapter();
			}
		});
	}
	
	protected void createToolItemUpload(ToolBar tBar) {
		String authorityToolItem = "PPM.Prepare.Import";
		itemUpload = new AuthorityToolItem(tBar, SWT.PUSH, authorityToolItem);
		itemUpload.setText(Message.getString("common.import"));
		itemUpload.setImage(SWTResourceCache.getImage("receive"));
		itemUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				uploadAdapter();
			}
		});
	}
	
	protected void uploadAdapter() {
		try {
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
				MpsDataImportProgress progress = new PrepareTempSaleDataImportProgress2(null,
						selectedFile, this.getTableManager().getADTable(), null);
				progressDialog.run(true, true, progress);
				// 提示已成功导入或失败
				if (progress.isFinished()) {
					if(progress.isSuccess()) {
						UI.showInfo(Message.getString("ppm.upload_successful"));
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
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void editAdapter() {
		try {
			String dialogTableName = "PPMTpsLinePrepare";
			if(selectedTpsLinePrepare != null && selectedTpsLinePrepare.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable dialogADTable = adManager.getADTable(Env.getOrgRrn(), dialogTableName);
				dialogADTable = adManager.getADTableDeep(dialogADTable.getObjectRrn());
				TpsPrepareDialog2 prepareDialog = new TpsPrepareDialog2(UI.getActiveShell(),dialogADTable,null, selectedTpsLinePrepare, dialogADTable);
				if(prepareDialog.open() ==Dialog.CANCEL){
					refresh();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at RequisitionSection : editAdapter() " + e);
		}
	}
	
//	@Override
//	protected void queryAdapter() {		
//		if (queryDialog != null) {
//			queryDialog.setVisible(true);
//		} else {
//			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_TPR);
//			queryDialog.open();
//		}
//	}

//	protected void deleteAdapter() {
//		if(selectedReq != null) {
//			try {
//				boolean confirmDelete = UI.showConfirm(Message
//						.getString("common.confirm_delete"));
//				if (confirmDelete) {
//					if (selectedReq.getObjectRrn() != null) {
//						PURManager purManager = Framework.getService(PURManager.class);
//						purManager.deletePR(selectedReq, Env.getUserRrn());
//						this.selectedReq = null;
//						refresh();
//					}
//				}
//			} catch (Exception e1) {
//				ExceptionHandlerManager.asyncHandleException(e1);
//				return;
//			}
//		}
//	}
	
	protected void refreshSection() {
		try {
			refresh();
		} catch(Exception e) {
			logger.error("Error at RequisitionSection : refreshSection() " + e);
		}
	}
	public void refresh(){
		List<TpsLinePrepare> tpsLinePrepares = new ArrayList<TpsLinePrepare>();
		try {
			//dialogWhereClause永远不为空
			StringBuffer sf = new StringBuffer();
			String dialogWhereClause = getWhereClause();
			if(dialogWhereClause !=null){
				dialogWhereClause = dialogWhereClause.replace("TpsLinePrepare.tpsId", "tps_id");
			}
			sf.append(dialogWhereClause);
//			String whereClause = getADTable().getWhereClause();
//			if(whereClause!=null && whereClause.length() > 0 ){
//				sf.append(" and ");
//				sf.append(whereClause);
//			}
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			tpsLinePrepares = pdmManager.getTpsLinePrepare(Env.getOrgRrn(),sf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewer.setInput(tpsLinePrepares);		
		tableManager.updateView(viewer);
		if(tpsLinePrepares!=null && tpsLinePrepares.size() >0 ){
			count = tpsLinePrepares.size();
		}
		createSectionDesc(section);
	}
	
	@Override
	protected void createSectionDesc(Section section){
		try{ 
			String text = Message.getString("common.totalshow");
//			ADManager entityManager = Framework.getService(ADManager.class);
//			long count = entityManager.getEntityCount(Env.getOrgRrn(), getTableManager().getADTable().getObjectRrn(), getWhereClause());
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("MasterSection : createSectionDesc ", e);
		}
	}
}
