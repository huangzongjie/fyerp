package com.graly.erp.wip.workcenter.schedule.pmczzj;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredViewer;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.wip.model.PmcZzjResultDetail;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PmcZzjDetailDialog extends ExtendDialog {
	public static final int INPUT_ID = 10001;
	private static final Logger logger = Logger.getLogger(PmcZzjDetailDialog.class);
	protected IManagedForm form;
	private static int MIN_DIALOG_WIDTH = 550;
	private static int MIN_DIALOG_HEIGHT = 300;

	protected ManagedForm managedForm;
	protected ADTable table;
	private Section section;
	private IFormPart spart;
	protected ToolItem itemExport;
	protected TableListManager tableManager;
	protected StructuredViewer viewer;
	
	public PmcZzjDetailDialog() {
		super();
		initAdTableByTableId();
		this.tableManager = new TableListManager(table);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle(String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(table, "label")));
		createFormContent(composite);
		return composite;
	}

	protected void initAdTableByTableId() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			table = entityManager.getADTable(0L, "PmcZzjResultDetail");
		} catch (Exception e) {
			logger.error("GenerateLotDialog : initAdTableByTableId()", e);
		}
	}
	
	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		createContents(managedForm, body);

	}

	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();

		section = toolkit.createSection(parent, sectionStyle);
		toolkit.createCompositeSeparator(section);
		createToolBar(section);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite client = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(gridLayout);

		spart = new SectionPart(section);
		form.addPart(spart);
		section.setText(String.format(Message.getString("common.detail"), I18nUtil.getI18nMessage(table, "label")));

	    createTableViewer(client, toolkit);
		section.setClient(client);
	}


	private void createToolBar(Section section2) {
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
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		viewer = (TableViewer)tableManager.createViewer(client, toolkit);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			List<PmcZzjResultDetail> results = adManager.getEntityList(Env.getOrgRrn(), PmcZzjResultDetail.class,Integer.MAX_VALUE,"showType= 'ZZJ'",null);
			viewer.setInput(results);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
//	@Override
//	protected void buttonPressed(int buttonId) {
//	}
//
//  

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
	
	public ADTable getTable() {
		return table;
	}
}
