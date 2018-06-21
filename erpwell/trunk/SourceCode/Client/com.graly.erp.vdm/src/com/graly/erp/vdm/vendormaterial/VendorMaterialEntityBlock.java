package com.graly.erp.vdm.vendormaterial;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class VendorMaterialEntityBlock extends EntityBlock {
	private boolean isQueryMainVendorOnly = false;
	private MainVendorOnlyInput itemInput;
	protected ToolItem itemExport;

	public VendorMaterialEntityBlock(EntityTableManager tableManager) {
		super(tableManager);
		this.setWhereClause("1 <> 1");
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
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
	
	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new VendorMaterialQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}

	public void refresh() {
		try {
			if(isQueryMainVendorOnly) {
				itemInput = new MainVendorOnlyInput(isQueryMainVendorOnly);
				viewer.setInput(itemInput);		
				tableManager.updateView(viewer);
				createSectionDesc(section);
			} else {
				super.refresh();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	public boolean isQueryMainVendorOnly() {
		return isQueryMainVendorOnly;
	}

	public void setQueryMainVendorOnly(boolean isQueryMainVendorOnly) {
		this.isQueryMainVendorOnly = isQueryMainVendorOnly;
	}

	@Override
	protected long createSectionDesc(Section section) {
		if(this.isQueryMainVendorOnly) {
			String text = Message.getString("common.totalshow");
			long count = 0L;
			if(itemInput != null)
				count = itemInput.getDisplaCount();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
			return count;
		} else {
			return super.createSectionDesc(section);			
		}
	}
}
