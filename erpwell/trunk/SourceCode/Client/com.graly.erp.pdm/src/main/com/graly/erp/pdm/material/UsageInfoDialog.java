package com.graly.erp.pdm.material;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.VPdmBom;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class UsageInfoDialog extends InClosableTitleAreaDialog {
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 320;
	protected Section section;
	protected ADTable adTable;
	protected TableViewerManager manager;
	protected TableViewer viewer;
	protected Material material;
	protected ToolItem itemBomTree;
	protected ToolItem itemExport;
	protected IManagedForm form;

	public UsageInfoDialog(Shell parentShell, Material material, ADTable adTable) {
		super(parentShell);
		this.material = material;
		this.adTable = adTable;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		setTitle(Message.getString("pdm.bom_usage_info"));

		FormToolkit toolkit = new FormToolkit(composite.getDisplay());		
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		form = new ManagedForm(toolkit, sForm);
		configureBody(body);
		
		Composite content = toolkit.createComposite(body, SWT.NULL);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new GridLayout(1, false));

		section = toolkit.createSection(content, Section.TITLE_BAR);
		section.setText(Message.getString("pdm.bom_usage_list"));
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
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
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

		return composite;
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemBOMTree(tBar);
		createToolItemExport(tBar);
		section.setTextClient(tBar);
	}

	private void createToolItemExport(ToolBar tBar) {
		itemExport = new ToolItem(tBar, SWT.PUSH);
		itemExport.setText(Message.getString("common.export"));
		itemExport.setImage(SWTResourceCache.getImage("export"));
		itemExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter(event);
			}
		});
	}

	protected void exportAdapter(SelectionEvent event) {
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

	protected void createSectionContent(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(gd);
		form.getToolkit().paintBordersFor(parent);
		createTableViewer(parent, form.getToolkit());
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		manager = new TableListManager(adTable);
		viewer = (TableViewer)manager.createViewer(client, toolkit, 300);
		viewer.addDoubleClickListener(getDoubleClickListener());
		manager.setInput(getUsage());
		manager.updateView(viewer);
	}
	
	protected IDoubleClickListener getDoubleClickListener() {
		return new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		bomTreeomAdapter(null);
	    	}
	    };
	}
	
	protected void createToolItemBOMTree(ToolBar tBar) {
		itemBomTree = new ToolItem(tBar, SWT.PUSH);
		itemBomTree.setText(Message.getString("pdm.edit_bom"));
		itemBomTree.setImage(SWTResourceCache.getImage("bomtree"));
		itemBomTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				bomTreeomAdapter(event);
			}
		});
	}
	
	protected void bomTreeomAdapter(SelectionEvent event) {
		try {
			if(viewer.getTable().getSelection().length > 0) {
				TableItem ti = viewer.getTable().getSelection()[0];
				if(ti.getData() instanceof VPdmBom) {
					VPdmBom vPdmBom = (VPdmBom)ti.getData();
					Material material = new Material();
					material.setObjectRrn(vPdmBom.getMaterialParentRrn());
					material.setMaterialId(vPdmBom.getMaterialParentId());
					material.setName(vPdmBom.getMaterialParentName());
					UsageInfoBomTreeDialog btd = new UsageInfoBomTreeDialog(UI.getActiveShell(),
							form, (Material) material);
					btd.open();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	private List<VPdmBom> getUsage(){
		try {
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			List<VPdmBom> list = pdmManager.getFullParentBomTree(material.getObjectRrn());
			return list;
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return new ArrayList<VPdmBom>();
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}
}
