package com.graly.erp.ppm.saleplan;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Button;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.base.model.Constants;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.SalePlanLine;
import com.graly.erp.ppm.mpsline.ErrorLogDisplayDialog;
import com.graly.erp.ppm.mpsline.MpsDataImportProgress;
import com.graly.erp.ppm.mpsline.MpsProgressDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.ChildEntityBlock;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class SalePlanEntityBlock extends ChildEntityBlock {
	protected ToolItem itemUpload;
	protected ToolItem itemDelSelect;
	protected CheckboxTableViewer tViewer;
	protected Button btnSelectAll;
	protected Button btnInvertAll;
	
	public SalePlanEntityBlock(EntityTableManager tableManager) {
		this(tableManager, " 1 <> 1 ", null);
	}

	public SalePlanEntityBlock(EntityTableManager tableManager, String whereClause, Object parentObject) {
		super(tableManager, whereClause, parentObject);
	}

	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
	//	tableManager.setPrLineBlock(this);
		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
	//	setParenObjectStatusChanged();
		changedLineCheckBox();
	}
	
	@Override
	protected void createViewer(IManagedForm managedForm, ADTable table,
			Composite client, FormToolkit toolkit) {
		super.createViewer(managedForm, table, client, toolkit);
		Composite buttonBar = toolkit.createComposite(client);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		GridData gd = new GridData(GridData.FILL_BOTH);
		buttonBar.setLayout(gl);
		buttonBar.setLayoutData(gd);
		btnSelectAll = toolkit.createButton(buttonBar, "全部选择", SWT.PUSH);
		btnSelectAll.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (viewer instanceof CheckboxTableViewer) {
					tViewer = (CheckboxTableViewer) viewer;
					tViewer.setAllChecked(true);
					refreshItemDelSelect();
				}
			}
			
		});
		btnInvertAll = toolkit.createButton(buttonBar, "反相选择", SWT.PUSH);
		btnInvertAll.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (viewer instanceof CheckboxTableViewer) {
					tViewer = (CheckboxTableViewer) viewer;
					Object[] checkedObjects = tViewer.getCheckedElements();
					tViewer.setAllChecked(true);
					for(Object o : checkedObjects){
						tViewer.setChecked(o, false);
					}
					refreshItemDelSelect();
				}
			}
			
		});
	}

	protected void changedLineCheckBox() {
		if (viewer instanceof CheckboxTableViewer) {
			tViewer = (CheckboxTableViewer) viewer;
			tViewer.addCheckStateListener(new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					refreshItemDelSelect();
				}
			});
		}
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemUpload(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelSelect(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
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
	
	protected void createToolItemUpload(ToolBar tBar) {
		itemUpload = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SALEPLAN_IMPORT);
		itemUpload.setText(Message.getString("common.import"));
		itemUpload.setImage(SWTResourceCache.getImage("receive"));
		itemUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				uploadAdapter();
			}
		});
	}

	protected void createToolItemDelSelect(ToolBar tBar) {
		itemDelSelect = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PPM_DELETESELECTSALEPLAN);
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
				MpsDataImportProgress progress = new SaleDataImportProgress((Mps)getParentObject(),
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

	protected void deleteSelectAdapter() {	
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				Table table =tViewer.getTable();
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					if(item.getChecked()){
						Object obj = item.getData();
						if (obj instanceof SalePlanLine) {
							SalePlanLine salePlanLine = (SalePlanLine)obj;
							ADManager entityManager = Framework.getService(ADManager.class);
							entityManager.deleteEntity(salePlanLine);
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
	
	public void setParentObject(Object parentObject) {
		super.setParentObject(parentObject);
		statusChanged(((Mps) getParentObject()).isFrozen());
	}

	protected void statusChanged(boolean isFrozen) {
		if (isFrozen) {
			itemUpload.setEnabled(false);
			itemDelSelect.setEnabled(false);
		} else {
			itemUpload.setEnabled(true);
			itemDelSelect.setEnabled(true);
		}
	}

	private void refreshItemDelSelect() {
		Mps parentObject = (Mps) getParentObject();
		Boolean isFrozen = parentObject == null ? false : parentObject.isFrozen();
		if(tViewer.getCheckedElements().length > 0 && !isFrozen){
			itemDelSelect.setEnabled(true);
		}else{
			itemDelSelect.setEnabled(false);
		}
	}
}
