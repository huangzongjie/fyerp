package com.graly.erp.wip.querychart;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.chart.builder.AbstractChartBuilder;
import com.graly.erp.chart.model.ChartCanvas;
import com.graly.erp.chart.model.ChartWithToolTipCanvas;
import com.graly.erp.wip.model.DailyMoMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.client.WipManager;

public class ChartSection implements IRefresh {
	private static final Logger logger = Logger.getLogger(MasterSection.class);

	private String whereClause = " 1 <> 1 ";
	protected Section section;
	protected EntityTableManager tableManager;
	protected ChartCanvas canvas;
	protected ToolItem itemQuery;
	protected ToolItem itemRefresh;
	protected String chartTitle = "工作负荷图", chartXAxisLabel = "工作日期", chartYAxisLabel = "工作负荷";
	
	protected EntityQueryDialog queryDialog;

	private Chart chart;
	
	public ChartSection(EntityTableManager tableManager) {
		super();
		this.tableManager = tableManager;
	}
	
	public ChartSection(EntityTableManager tableManager, String whereClause) {
		super();
		this.tableManager = tableManager;
		this.whereClause = whereClause;
	}

	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION|Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		final ADTable table = tableManager.getADTable();
		
		section = toolkit.createSection(parent, sectionStyle);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    form.addPart(new SectionPart(section));
	    toolkit.createCompositeSeparator(section);
	    
	    createToolBar(section);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
		Composite client = toolkit.createComposite(section, SWT.NONE);    
		configureBody(client);
	    
	    chart = createChart(chartTitle,chartXAxisLabel,chartYAxisLabel);
		canvas = new ChartWithToolTipCanvas(client, SWT.BORDER);
	    configureBody(canvas);
        canvas.setChart(chart);
        
	    section.setClient(client);
	    refresh();
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemQuery(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemQuery(ToolBar tBar) {
		itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText(Message.getString("common.search_Title"));
		itemQuery.setImage(SWTResourceCache.getImage("search"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
			}
		});
	}
	
	protected void createToolItemRefresh(ToolBar tBar) {
		itemRefresh = new ToolItem(tBar, SWT.PUSH);
		itemRefresh.setText(Message.getString("common.refresh"));
		itemRefresh.setImage(SWTResourceCache.getImage("refresh"));
		itemRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreshAdapter();
			}
		});
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	protected void refreshAdapter(){
		if(canvas != null){
			canvas.redraw();
		}
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		body.setLayoutData(gd);
	}
	
	private Chart createChart(String title, String xAixsLabel, String yAxisLabel) {
		String workCenter = getWorkcenterFromWhereClause();
		AbstractChartBuilder builder = new StackedChartBuilder(new DailyMoMaterialSetFactory(getQueryRslt()), workCenter + title, xAixsLabel, yAxisLabel);
		builder.build();
		return builder.getChart();
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	
	protected List<DailyMoMaterial> getQueryRslt(){
		try {
			WipManager wipManager = Framework.getService(WipManager.class);
			List<DailyMoMaterial> list = wipManager.getDailyMoMaterials(Env.getOrgRrn(),whereClause);
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}

	public String getWhereClause() {
		return whereClause;
	}
	
	public EntityQueryDialog getQueryDialog() {
		return queryDialog;
	}

	public void setQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = queryDialog;
	}

	@Override
	public void refresh() {
		if(canvas != null){
			chart = createChart(chartTitle,chartXAxisLabel,chartYAxisLabel);
			canvas.setChart(chart);
			canvas.redraw();//刷新
		}
	}
	
	private String getWorkcenterFromWhereClause(){
		try {
			if(whereClause.contains("workcenterRrn")){
				String[] strs = whereClause.split("workcenterRrn LIKE ");
				String str1 = strs[1];
				String str2 = str1.split("AND")[0].trim();
				String str3 = str2.substring(1, str2.length()-1);//截去首尾的单引号
				ADManager manager = Framework.getService(ADManager.class);
				WorkCenter wc = new WorkCenter();
				wc.setObjectRrn(Long.parseLong(str3));
				wc = (WorkCenter) manager.getEntity(wc);
				return wc.getName();
			}
		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		
		
		return "";
	}
}
