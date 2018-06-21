package com.graly.erp.pdm.bomhistory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.BomTreeDialog;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.MaterialOptional;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomHisTreeDialog extends BomTreeDialog {
	protected Long version;
	protected BomHisTreeForm bomHisTreeForm;
	protected Material material;
	public BomHisTreeDialog(Shell parent, IManagedForm form, Material material,
			boolean editable, Long version) {
		super(parent, form, material, editable);
		this.version=version;
		this.material=material;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)createBaseDialogArea(parent);
        setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(Message.getString("pdm.bom_list"));
        
        FormToolkit toolkit = form.getToolkit();
        Composite content = toolkit.createComposite(composite, SWT.NULL);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        content.setLayout(new GridLayout(1, false));

		section = toolkit.createSection(content, Section.TITLE_BAR);
		section.setText(Message.getString("pdm.bom_list_detail_info"));
		section.marginWidth = 2;
		section.marginHeight = 2;
		toolkit.createCompositeSeparator(section);
		createToolBar(section);
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 2;
		layout.leftMargin = 2;
		layout.rightMargin = 2;
		layout.bottomMargin = 2;
		content.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = true;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);
		GridData g = new GridData(GridData.FILL_BOTH);
		client.setLayoutData(g);

		createSectionContent(client);
		
		toolkit.paintBordersFor(section);
		section.setClient(client);
		createViewAction(bomHisTreeForm.getViewer());
		
        return composite;
	}
	
	protected Control createBaseDialogArea(Composite parent) {
		// create the top level composite for the dialog area
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		// Build the separator line
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
				| SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}
	
	@Override
	protected void createSectionContent(Composite section) {
		final IMessageManager mmng = form.getMessageManager();
		GridLayout gl = new GridLayout(1, false);
		section.setLayout(gl);
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		toolkit.paintBordersFor(section);
		
		bomHisTreeForm = new BomHisTreeForm(section, SWT.NULL, material, mmng, this, version);
		bomHisTreeForm.setLayoutData(gd);
	}
	
	@Override
	protected void expendAllAdapter(SelectionEvent event) {
		try {
			bomHisTreeForm.setObject(material);
			bomHisTreeForm.refreshAll();
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
	
	@Override
	protected void exportAdapter() {
		try {
			FileDialog dialog = new FileDialog(UI.getActiveShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "CSV (*.csv)" });
			dialog.setFilterExtensions(new String[] { "*.csv" }); 
			String fn = dialog.open();
			if (fn != null) {
				TreeViewer viewer = bomHisTreeForm.getViewer();
				Tree tree = viewer.getTree();
				int columnCount = tree.getColumnCount();
				List<String[]> ls = new LinkedList<String[]>();
				String[] headers = new String[columnCount+10];
				for(int i=0;i<10;i++){
					headers[i] = String.valueOf(i+1);
				}
				for(int i=0; i<tree.getColumnCount(); i++){
					headers[i+10] = tree.getColumn(i).getText();
				}
				ls.add(headers);
				for(TreeItem ti: tree.getItems()){
					int level = 0;
					String[] dt = new String[columnCount+10];
					for(int i=0; i<columnCount+10; i++){
						if(i<10){
							if(i==level){
								dt[i] = String.valueOf(i+1);
							}else{
								dt[i] = "";
							}
						}else{
							dt[i] = ti.getText(i-10);
						}
					}
					List<String[]> l = stepInTreeItems(ti,level,tree.getColumnCount());
					ls.add(dt);
					ls.addAll(l);
				}
				
				File file = new File(fn);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				CSVWriter writer = new CSVWriter(new FileWriter(file));
		        for (String[] strs : ls) {
		            writer.writeNext(strs);
		        }
		        writer.close();

			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	

	
	

}
