package com.graly.erp.wip.mo.material_standtime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.ConditionItem;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

import com.graly.mes.prd.workflow.graph.def.Process;;

public class StandTimeSection {
	private static final Logger logger = Logger.getLogger(StandTimeSection.class);


	protected ADTable adTable;
	protected Section section;
	protected IFormPart spart;
	protected IManagedForm form;

	protected Material material = new Material();
	protected Text txtMaterialId;
	
	protected boolean isSaved = false;
	protected StandTimeBomTreeForm bomTreeForm;
	protected ToolItem itemRefresh;
	
	protected Label workCenterTime;//显示每个车间的详细统计
	
	protected Map<String, Text> workCenterMap = new LinkedHashMap<String,Text>();
	
	protected Map<String, BigDecimal> workCenterHashMap = new HashMap<String,BigDecimal>();

	public StandTimeSection(ADTable adTable) {
		this.adTable = adTable;
	}
	
	public StandTimeSection(ADTable adTable, String lotType) {
		this(adTable);
	}

	public StandTimeSection(ADTable adTable, ConditionItem conditionItem) {
		this.adTable = adTable;
	}

	public void createContents(IManagedForm form, Composite parent) {
		this.form = form;
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(String.format(Message.getString("common.detail"),
				"工时统计"));
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
		
		TableWrapData td = new TableWrapData(TableWrapData.FILL,TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);	 
//
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);
		TableWrapData td2 = new TableWrapData(TableWrapData.FILL,TableWrapData.FILL);
		section.setLayoutData(td);
		client.setLayoutData(td2);
		
		client.setLayoutData(new GridData(GridData.FILL_BOTH));

//		createSectionDesc(section);
		createSectionTitle(client);
		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	protected void createToolItemRefresh(ToolBar tBar) {
		itemRefresh = new ToolItem(tBar, SWT.PUSH);
		itemRefresh.setText(Message.getString("common.refresh"));
		itemRefresh.setImage(SWTResourceCache.getImage("refresh"));
		itemRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
//				refresh();
			}
		});
	}
	
	protected void createSectionTitle(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(5, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, "物料编号");
		label.setForeground(SWTResourceCache.getColor("Folder"));
		txtMaterialId = toolkit.createText(top, "", SWT.BORDER);
		GridData gLabel = new GridData();
		gLabel.horizontalAlignment = GridData.FILL;
		gLabel.grabExcessHorizontalSpace = true;

		Label labelStandTime = toolkit.createLabel(top, "总标准工时");
		labelStandTime.setForeground(SWTResourceCache.getColor("Folder"));
		final Text txtStandTime = toolkit.createText(top, "", SWT.NULL);
		txtStandTime.setEditable(false);
		txtStandTime.setEnabled(false);
		
		
		GridData gText = new GridData();
		gText.widthHint = 200;
		txtMaterialId.setLayoutData(gText);
		txtMaterialId.setTextLimit(32);
		txtStandTime.setLayoutData(gText);
		txtStandTime.setTextLimit(32);
		txtMaterialId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				Text tMaterialId = ((Text) event.widget);
				tMaterialId.setForeground(SWTResourceCache.getColor("Black"));
				switch (event.keyCode) {
				case SWT.CR:
					Material material = null;
					String materialId = tMaterialId.getText();
					material = searchMaterial(materialId);
					tMaterialId.selectAll();
					if (material == null) {
						tMaterialId.setForeground(SWTResourceCache.getColor("Red"));
//						bomTreeForm.setObject(null);
//						bomTreeForm.refresh();
//						initAdObject();
					} else {
						bomTreeForm.setObject(material);
						bomTreeForm.getViewer().getInput();
						cleanMapAllValue();
						try{
							PDMManager pdmManager = Framework.getService(PDMManager.class);
							List<BomDetail> bomDetails = pdmManager.getLastBomDetails(material.getObjectRrn());
							BigDecimal countStandTime = BigDecimal.ZERO;//总共工时
							if(material.getStandTime()!=null){
								countStandTime = countStandTime.add(material.getStandTime());
								BigDecimal workStandTime = workCenterHashMap.get(material.getProcessName());
								workStandTime = workStandTime.add(material.getStandTime()==null?BigDecimal.ZERO : material.getStandTime());
								workCenterHashMap.put(material.getProcessName(), workStandTime);
							}
							for(BomDetail tmpBomDetail : bomDetails){
								if(tmpBomDetail.getChildMaterial()!=null){
									Material childMaterial = tmpBomDetail.getChildMaterial();
									if(childMaterial.getStandTime()!=null){
										countStandTime = countStandTime.add(childMaterial.getStandTime());
									}
									BigDecimal workStandTime = workCenterHashMap.get(childMaterial.getProcessName());
									if(workStandTime!=null){
										workStandTime = workStandTime.add(childMaterial.getStandTime()==null?BigDecimal.ZERO : childMaterial.getStandTime());
										workCenterHashMap.put(childMaterial.getProcessName(), workStandTime);
									}else{
										//如果物料没有工艺那么肯定是采购件，采购件工时算到他的父物料的工艺上
										Material parentMaterial = tmpBomDetail.getParentMaterial();
										if(parentMaterial!=null){
											workStandTime = workCenterHashMap.get(parentMaterial.getProcessName());
											workStandTime = workStandTime.add(childMaterial.getStandTime()==null?BigDecimal.ZERO : childMaterial.getStandTime());
											workCenterHashMap.put(parentMaterial.getProcessName(), workStandTime);
										}
									}
									
								}
							}
							txtStandTime.setText(countStandTime+"");
							Iterator iter = workCenterHashMap.keySet().iterator();
							StringBuffer sf = new StringBuffer();
							while (iter.hasNext()) {
								String key = (String) iter.next();
								BigDecimal val = workCenterHashMap.get(key);
								if(val.compareTo(BigDecimal.ZERO) >0){
									sf.append(key);
									sf.append(" : ");
									sf.append(val);
									sf.append("    ");
								}
							}
							workCenterTime.setText(sf.toString());
						}catch(Exception e ){
							e.printStackTrace();
						}
						bomTreeForm.refresh();
					}
					break;
				}
			}
			public Material searchMaterial(String materialId) {
				try {
					PDMManager pdmManager = Framework.getService(PDMManager.class);
					List<Material> searchMaterials = pdmManager.getMaterialById(materialId, Env.getOrgRrn());
					Material returnMaterial = null;
					if(searchMaterials!=null && searchMaterials.size() >0){
						returnMaterial = searchMaterials.get(0);
					}
					return returnMaterial;
				} catch (Exception e) {
					ExceptionHandlerManager.asyncHandleException(e);
					return null;
				}
			}
		});
		//显示各个车间的工时
		final Composite center = toolkit.createComposite(client);
		center.setLayout(new GridLayout(1, false));
		center.setLayoutData(gd);
		GridData gText2 = new GridData();
		gText2.widthHint = 800;
		workCenterTime = toolkit.createLabel(center, "   ");
		workCenterTime.setForeground(SWTResourceCache.getColor("Folder"));
		workCenterTime.setLayoutData(gText2);
		createWCStandTime(toolkit,client);
		
	}
	
	protected void createSectionContent(Composite client) {
//		client.setBackground(SWTResourceCache.getColor("Red"));
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		GridData gd = new GridData(GridData.FILL_BOTH);
		
		try {
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			List<Material> materials = pdmManager.getMaterialById("13010004M", Env.getOrgRrn());
			if(materials!=null && materials.size() > 0){
				material = materials.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		bomTreeForm = new StandTimeBomTreeForm(client, SWT.NULL, material, mmng, this);
		bomTreeForm.setLayoutData(gd);
	}
 
	/**
	 * 创建车间工时统计，代码较长，你也可缩短代码采用动态实现
	 * */
	
	public void createWCStandTime(FormToolkit toolkit,Composite parentComposite){
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			List<Process>  processes = adManager.getEntityList(Env.getOrgRrn(), 
					Process.class,Integer.MAX_VALUE,null," name desc");
			for(Process process : processes){
				workCenterHashMap.put(process.getName(),BigDecimal.ZERO );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//清除所有文本的值
	@SuppressWarnings("unchecked")
	public void cleanMapAllValue() {
		Iterator iter = workCenterHashMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			workCenterHashMap.put(key, BigDecimal.ZERO);
		}
	}
}
