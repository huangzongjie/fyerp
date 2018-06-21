package com.graly.erp.ppm.mpsline;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.ppm.model.MpsLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MpsExcelPersistDialog extends InClosableTitleAreaDialog {
	private static Logger logger = Logger.getLogger(MpsExcelPersistDialog.class);
	protected static int MIN_DIALOG_WIDTH = 700;
	protected static int MIN_DIALOG_HEIGHT = 450;
	protected IManagedForm form;
	protected Section section;
	protected ADTable adTable;
	protected List<MpsLine> mpsLines;
	protected TableViewer viewer;
	
	protected ToolItem itemExport;
	
	
	public MpsExcelPersistDialog(Shell parent) {
        super(parent);
    }
	
	public MpsExcelPersistDialog(Shell parent, IManagedForm form,ADTable adTable, List<MpsLine> lines){
		this(parent);
		this.form = form;
		this.adTable = adTable;
		this.mpsLines = lines;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
        setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle("EXCEL导入数据中,在系统中不存在的计划数据清单");
        
        FormToolkit toolkit = form.getToolkit();
        Composite content = toolkit.createComposite(composite, SWT.NULL);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        content.setLayout(new GridLayout(1, false));

		section = toolkit.createSection(content, Section.TITLE_BAR);
		section.setText("新增计划清单");
		section.marginWidth = 2;
		section.marginHeight = 2;
		toolkit.createCompositeSeparator(section);
		createToolBar(section);
		

		section.setLayout(new GridLayout(1, false));
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
//		section.setBackground(new Color(null,new RGB(111,255,243)));
		
		Composite client = toolkit.createComposite(section, SWT.NULL);
//		client.setBackground(new Color(null,new RGB(122,168,243)));
		GridLayout gridLayout = new GridLayout();
		client.setLayout(gridLayout);
		GridData g = new GridData(GridData.VERTICAL_ALIGN_END);
		client.setLayoutData(g);

		createSectionContent(client);
		
		toolkit.paintBordersFor(section);
		section.setClient(client);
		
        return composite;
	}
	
	 
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
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
	
 
 
	protected void createSectionContent(Composite section) {
		try {
			EntityTableManager tableManager = new EntityTableManager(adTable);
			viewer = (TableViewer)tableManager.createViewer(section, form.getToolkit());
			viewer.setInput(mpsLines);
			tableManager.updateView(viewer);
		} catch(Exception e) {
			logger.error("PoSelectSection : createSectionContent() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}
 
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
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
 
}
