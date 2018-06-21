package com.graly.erp.ppm.saleplan.temp.prepare2;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.base.model.Constants;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.TpsLine;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.erp.ppm.mpsline.ErrorLogDisplayDialog;
import com.graly.erp.ppm.mpsline.MpsDataImportProgress;
import com.graly.erp.ppm.mpsline.MpsProgressDialog;
import com.graly.erp.ppm.saleplan.SalePlanEntityBlock;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
/*
 * 继承父类主要取消查询和导入功能
 */
public class TpsPrepareEntityBlock2 extends SalePlanEntityBlock {
	protected ToolItem itemExport;
	protected ToolItem itemApprove;
	
	public TpsPrepareEntityBlock2(EntityTableManager tableManager, String whereClause,
			Object parentObject) {
		super(tableManager, whereClause, parentObject);
	}

	public TpsPrepareEntityBlock2(EntityTableManager tableManager) {
		super(tableManager);
	}
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
		// 第一次进页面时设置删除选中按钮不可用
		itemDelSelect.setEnabled(false);
		changedLineCheckBox();
	}

	// 取消查询和导入功能, 只保留刷新
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemApprove(tBar);
//		createToolItemUpload(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelSelect(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	protected void createToolItemApprove(ToolBar tBar) {
		String authorityToolItem = "PPM.Prepare.ApproveSelect";
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, authorityToolItem);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
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
	
	@Override
	protected void createToolItemDelSelect(ToolBar tBar) {
		itemDelSelect = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PPM_DELETESELECTSALEPLANTEMP);
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
	
	protected void createToolItemExport(ToolBar tBar) {
		itemExport = new ToolItem(tBar, SWT.PUSH);
		itemExport.setText(Message.getString("common.export"));
		itemExport.setImage(SWTResourceCache.getImage("export"));
		itemExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter();
			}
		});
	}
	
	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if (confirm) {
				Table table =tViewer.getTable();
				List<TpsLinePrepare> tpsLinePrepares = new ArrayList<TpsLinePrepare>();
				
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					if(item.getChecked()){
						Object obj = item.getData();
						if (obj instanceof TpsLinePrepare) {
							TpsLinePrepare tpsLine = (TpsLinePrepare)obj;
							tpsLinePrepares.add(tpsLine);
						}
					}
				}
				PPMManager ppmManager = Framework.getService(PPMManager.class);
				ppmManager.approveTpsLinePrepare(Env.getOrgRrn(),Env.getUserRrn(),tpsLinePrepares);
				UI.showInfo(Message.getString("common.approve_successed"));
				refresh();
				itemDelSelect.setEnabled(false);
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
		
		
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
				MpsDataImportProgress progress = new PrepareTempSaleDataImportProgress2((Mps)getParentObject(),
						selectedFile, this.getTableManager().getADTable(), this);
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
	
	protected void exportAdapter() {
		try {
			FileDialog dialog = new FileDialog(UI.getActiveShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "CSV (*.csv)" });
			dialog.setFilterExtensions(new String[] { "*.csv" }); 
			String fn = dialog.open();
			if (fn != null) {
				Table table = ((TableViewer)viewer).getTable();
				String[][] datas = new String[table.getItemCount() + 1][table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					TableColumn column = table.getColumn(i);
					datas[0][i] = column.getText();
				}
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					for (int j = 0; j < table.getColumnCount(); j++) {
						datas[i + 1][j] = item.getText(j);
					}
				}
				
				File file = new File(fn);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				CSVWriter writer = new CSVWriter(new FileWriter(file));
		        for (int i = 0; i < datas.length; i++) {
		            writer.writeNext(datas[i]);
		        }
		        writer.close();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
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
						if (obj instanceof TpsLinePrepare) {
							TpsLinePrepare tpsLine = (TpsLinePrepare)obj;
							ADManager entityManager = Framework.getService(ADManager.class);
							entityManager.deleteEntity(tpsLine);
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
	
}
