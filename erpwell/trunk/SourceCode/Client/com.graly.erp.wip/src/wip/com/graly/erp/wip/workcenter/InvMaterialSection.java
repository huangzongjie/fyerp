package com.graly.erp.wip.workcenter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.wip.model.MaterialSum;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.ListItemAdapter;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.client.WipManager;

public class InvMaterialSection {
	
	private static final Logger logger = Logger.getLogger(InvMaterialSection.class);
	protected static final String TABLE_NAME = "INVMaterialSum";
	protected ADTable adTable;
	protected IManagedForm form;
	protected ToolItem itemExport;
	protected ToolItem itemStats; // 统计物料相关数量
	protected Section section;
	
	private int displayCount = 0;
	private static String PREFIX = "workCenterRrn = ";
	private TableListManager tableManager;
	private StructuredViewer viewer;
	private WorkCenter workCenter;

	public InvMaterialSection() {
		super();
		getAdTableOfInvMaterial();
    }
	
	public void createContent(IManagedForm form, Composite parent) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(adTable, "label")));
		section.marginWidth = 3;
		section.marginHeight = 4;
		toolkit.createCompositeSeparator(section);

		createToolBar(section);

		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);	 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);

		createSectionDesc(section);
//		createSectionTitle(client);
		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);

	}
	
	protected void createSectionContent(Composite client) {
		FormToolkit toolkit = form.getToolkit();
		tableManager = new MaterialSumTableListManager(adTable);
		viewer = tableManager.createViewer(client, toolkit);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
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
	
	
	protected void createToolItemRefresh(ToolBar tBar) {
		itemStats = new ToolItem(tBar, SWT.PUSH);
		itemStats.setText(Message.getString("wip.material_statistic"));
		itemStats.setImage(SWTResourceCache.getImage("refresh"));
		itemStats.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				statsAdapter();
			}
		});
	}
	
	protected void getAdTableOfInvMaterial() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0, TABLE_NAME);				
		} catch(Exception e) {
			logger.error("InvMaterialSection : getAdTableOfInvMaterial()", e);
		}
	}
	
	public String getWhereClause() {
		if(workCenter != null && workCenter.getObjectRrn() != 0) {
			return PREFIX + workCenter.getObjectRrn();
		}
		return "1 != 1";
	}
	
	public WorkCenter getWorkCenter() {
		return workCenter;
	}
	
	public void setWorkCenter(WorkCenter workCenter) {
		this.workCenter = workCenter;
	}
	
	protected void statsAdapter() {
		try {
			statsMaterial();			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("InvMaterialSection : refreshAdapter() ", e);
		}
	}
	
	public void refresh() {
		form.getMessageManager().removeAllMessages();
		List<MaterialSum> mss = new ArrayList<MaterialSum>();
		viewer.setInput(mss);
		displayCount = mss.size();
		tableManager.updateView(viewer);
	}
	
	// 统计需要在该工作中心下加工或生产的所有物料库存等相关数据
	public void statsMaterial() throws Exception {
    	form.getMessageManager().removeAllMessages();
    	if(workCenter != null && workCenter.getObjectRrn() != 0) {
    		WipManager wipManager = Framework.getService(WipManager.class);
    		List<MaterialSum> list = (List<MaterialSum>)wipManager.getInvMaterialByWorkCenter(Env.getOrgRrn(), workCenter.getObjectRrn());
    		List<MaterialSum> input = new ArrayList<MaterialSum>();
    		List<MaterialSum> afters = new ArrayList<MaterialSum>();
    		for(MaterialSum ms : list) {
    			if(ms != null) {
    				if(isSmallerMin(ms))
    					input.add(ms);
    				else
    					afters.add(ms);
    			}
    		}
    		input.addAll(afters);
    		viewer.setInput(input);
    		displayCount = input.size();
    		tableManager.updateView(viewer);
    		createSectionDesc(section);
    	}
	}
	
	// 判断当前库存是否小于最小库存
	private boolean isSmallerMin(MaterialSum ms) {
		if(ms.getQtyOnHand().compareTo(ms.getQtyMin()) < 0)
			return true;
		return false;
	}
	
	protected void createSectionDesc(Section section) {
		try{ 
			String text = Message.getString("common.totalshow");
			if (displayCount > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(displayCount), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(displayCount), String.valueOf(displayCount));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}

	// 继承TableListManager，实现重载createAdapterFactory()方法
	class MaterialSumTableListManager extends TableListManager {
		public MaterialSumTableListManager(ADTable adTable) {
			super(adTable);
		}
		
		@Override
		protected ItemAdapterFactory createAdapterFactory() {
			ItemAdapterFactory factory = new ItemAdapterFactory();
	        try{
		        factory.registerAdapter(Object.class, new MaterialSumItemAdapter<ADBase>());
	        } catch (Exception e){
	        	e.printStackTrace();
	        }
	        return factory;
		}		
	}
	
	// 继承TableListManager，实现重载getForeground()方法
	class MaterialSumItemAdapter<T> extends ListItemAdapter<T> {
		@Override
		public Color getForeground(Object element, String id) {
			if(element instanceof MaterialSum) {
				if(isSmallerMin((MaterialSum)element))
					return SWTResourceCache.getColor("Red");
			}
			return super.getForeground(element, id);
		}
	}
}
