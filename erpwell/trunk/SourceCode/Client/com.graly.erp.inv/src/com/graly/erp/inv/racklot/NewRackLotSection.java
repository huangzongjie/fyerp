package com.graly.erp.inv.racklot;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.util.ADFieldUtil;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class NewRackLotSection{
	private static final Logger logger = Logger.getLogger(NewRackLotSection.class);
	
	protected LinkedHashMap<String, ADField> adFields = new LinkedHashMap<String, ADField>(10, (float)0.75, false);
	protected LinkedHashMap<String, IField> fields = new LinkedHashMap<String, IField>(10, (float)0.75, false);
	
	private int gridY = 2;
    protected int mLeftPadding = 5;
    protected int mTopPadding = 0;
    protected int mRightPadding = 5;
    protected int mBottomPadding = 0;

    protected int mHorizSpacing = 5;
    protected int mVertSpacing = 5;
    
    protected List<RacKMovementLot> rLots = new ArrayList<RacKMovementLot>();
    
	protected TableViewerManager lotManager;
	protected TableViewer viewer;
	protected CheckboxTableViewer checkViewer;
	protected IMessageManager mmng;
	protected ADTable adTable;
	protected Section section;
	protected IFormPart spart;
	protected IManagedForm form;
	protected NewRackLotDialog2 parentDialog;
	protected INVManager invManager;

	protected ToolItem itemSave;
	protected ToolItem itemDelete;
	
	protected Text txtLotId;
	protected Lot lot = null;
	
	protected boolean isSaved = false; // 是否进行了保存动作
	protected boolean isDid = false;   // 是否对界面进行了操作


	public NewRackLotSection() {
	}

	public NewRackLotSection(ADTable adTable, NewRackLotDialog2 parentDialog) {
		this.adTable = adTable;
		this.parentDialog = parentDialog;

	}
	
	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		mmng = form.getMessageManager();
		section = toolkit.createSection(parent, sectionStyle);
		setSectionTitle();
		section.marginWidth = 3;
	    section.marginHeight = 4;
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
	    this.form.addPart(spart);
	    
	    createParentContent(client, toolkit);
	    createLotInfoComposite(client, toolkit);
	    createTableContent(client, toolkit);
	    section.setClient(client);
	}
	
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
		Composite comp = toolkit.createComposite(client, SWT.BORDER);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = toolkit.createLabel(comp, Message.getString("inv.lotid"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		label.setFont(SWTResourceCache.getFont("Verdana"));
		txtLotId = toolkit.createText(comp, "", SWT.BORDER);
		txtLotId.setTextLimit(48);
		GridData gd = new GridData();//GridData.FILL_HORIZONTAL
		gd.heightHint = 13;
		gd.widthHint = 340;
		txtLotId.setLayoutData(gd);
		txtLotId.addKeyListener(getKeyListener());
		txtLotId.setFocus();
	}
	
	protected void createTableContent(Composite client, FormToolkit toolkit) {
		createTableViewer(client, toolkit);
		if(viewer instanceof CheckboxTableViewer) {
			checkViewer = (CheckboxTableViewer)viewer;
		}
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new TableListManager(adTable);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		lotManager.updateView(viewer);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new ToolItem(tBar, SWT.PUSH);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new ToolItem(tBar, SWT.PUSH);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}

	/* 删除Lot列表中选中的Lot*/
	protected void deleteAdapter() {
		try {
			TableItem[] items = viewer.getTable().getSelection();
        	if (items != null && items.length > 0){
        		TableItem item = items[0];
        		Object obj = item.getData();
        		if(obj instanceof RacKMovementLot) {
        			boolean confirmDelete = UI.showConfirm(Message
        					.getString("common.confirm_delete"));
        			if (confirmDelete) {
        				delete((RacKMovementLot)obj);
        			}
        		}
        	}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	protected void delete(RacKMovementLot rLot) {
		getrLots().remove(rLot);
		refresh();
		setDoOprationsTrue();
	}
	
	//保存到数据库之前将仓库和货架信息保存到rLot中去
	protected void saveAdapter() {
		try {
			if(invManager == null){
				invManager = Framework.getService(INVManager.class);
			}
			refresh();
			RacKMovementLot parentRlot = getRackInfo();
			if(parentRlot != null){
				for(RacKMovementLot rlot : getrLots()){
					rlot.setWarehouseRrn(parentRlot.getWarehouseRrn());
					rlot.setRackRrn(parentRlot.getRackRrn());
					
					rlot = invManager.saveRacKMovementLot(Env.getOrgRrn(), rlot, Env.getUserRrn(),true);
					UI.showInfo(Message.getString("common.save_successed"));
					this.setIsSaved(true);
					this.setEnable(false);
				}
				setIsSaved(true);
			}
		} catch (Exception e) {
			logger.error("NewRackLotSection : saveAdapter()", e);
		}
	}
	
	private boolean validate() {
		boolean validFlag = true;
		for (IField f : fields.values()){
			ADField adField = adFields.get(f.getId());
			if(adField != null){
				if (adField.getIsMandatory()){
					Object value = f.getValue();
					boolean isMandatory = false;
					if (value == null){
						isMandatory = true;
					} else {
						if (value instanceof String){
							if ("".equalsIgnoreCase(value.toString().trim())){
								isMandatory = true;
							}
						} else if(value instanceof Map) {
							Map<String, Date> map = (Map<String, Date>)value;
							if(map.values().contains(null)) {
								isMandatory = true;
							}
						}
					}
					if (isMandatory){
						validFlag = false;
						mmng.addMessage(adField.getName() + "common.ismandatory", 
								String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")), null,
								IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
					}
				}
				if (adField.getDataType() != null && !"".equalsIgnoreCase(adField.getDataType().trim())){
					if (!(f.getValue() instanceof String)){
						continue;
					}
					String value = (String)f.getValue();
					if (value != null && !"".equalsIgnoreCase(value.trim())){
						if (!ValidatorFactory.isValid(adField.getDataType(), value)){
							validFlag = false;
							mmng.addMessage(adField.getName() + "common.isvalid", 
									String.format(Message.getString("common.isvalid"), I18nUtil.getI18nMessage(adField, "label"), adField.getDataType()), null,
									IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
						} else if (!ValidatorFactory.isInRange(adField.getDataType(), value, adField.getMinValue(), adField.getMaxValue())){
							validFlag = false;
							if ((adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim()))
							 && (adField.getMaxValue() != null && !"".equalsIgnoreCase(adField.getMaxValue().trim()))){
								mmng.addMessage(adField.getName() + "common.between", 
										String.format(Message.getString("common.between"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue(), adField.getMaxValue()), null,
											IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
							} else if (adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim())){
								mmng.addMessage(adField.getName() + "common.largerthan", String.format(Message.getString("common.largerthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue()), null,
										IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);						
							} else {
								mmng.addMessage(adField.getName() + "common.lessthan", String.format(Message.getString("common.lessthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMaxValue()), null,
										IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);												
							}
						}
					}
				}
				if (adField.getNamingRule() != null && !"".equalsIgnoreCase(adField.getNamingRule().trim())){
					Object value = f.getValue();
					if (value == null){
						continue;
					}
					if (value instanceof String){
						if (!Pattern.matches(adField.getNamingRule(), value.toString())) {
							validFlag = false;
							mmng.addMessage(adField.getName() + "common.namingrule_error", 
									String.format(Message.getString("common.namingrule_error"), I18nUtil.getI18nMessage(adField, "label")), null,
									IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
						}
					}
				}
			}
			
		}
		return validFlag;
	}

	private RacKMovementLot getRackInfo() {
		if(!validate()){
			return null;
		}
		
		RacKMovementLot rLot = new RacKMovementLot();
		for (IField f : fields.values()){
			if (!(f instanceof SeparatorField)){
				PropertyUtil.setProperty(rLot, f.getId(), f.getValue());
			}
		}
		return rLot;
	}

	protected void setSectionTitle() {
		section.setText(Message.getString("inv.lot_list"));
	}

	protected void createParentContent(Composite client, FormToolkit toolkit) {
		try {
			Composite comp = toolkit.createComposite(client, SWT.BORDER);
			GridLayout gl = new GridLayout();
			gl.numColumns = 1;
			comp.setLayout(gl);
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Composite whComp = toolkit.createComposite(comp, SWT.NONE);
			GridLayout gl2 = new GridLayout();
			gl2.numColumns = 2;
			whComp.setLayout(gl2);
			whComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Composite bodyHeader = toolkit.createComposite(comp, SWT.NONE);
			bodyHeader.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			createFieldContent(bodyHeader, toolkit);
			
			Label label = toolkit.createLabel(client, "", SWT.HORIZONTAL | SWT.SEPARATOR);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void createFieldContent(Composite body, FormToolkit toolkit) throws Exception {
		List<ADField> allAdFields = adTable.getFields();
		if (allAdFields != null && allAdFields.size() > 0){
			for(ADField adField : allAdFields){
				if (adField.getIsDisplay()) {
	    			IField field = ADFieldUtil.getField(adField, fields);
	    			if (field == null) {
	    				continue;
	    			}
	    			adFields.put(adField.getName(), adField);
					if (field != null) {
						field.setADField(adField);
					}
	    		}
			}
			registeValueChangeListener();

	        GridLayout layout = new GridLayout();
	        layout.verticalSpacing = mVertSpacing;
	        layout.horizontalSpacing = mHorizSpacing;
	        layout.marginLeft = mLeftPadding;
	        layout.marginRight = mRightPadding;
	        layout.marginTop = mTopPadding;
	        layout.marginBottom = mBottomPadding;
	        body.setLayout(layout);
	        
	        // first: create the children controls and compute the
	        // number of columns to be used by the grid layout
	        int cols = 1;
	        for (IField f : fields.values()){
	            f.createContent(body, toolkit);
	            
	            Control[] ctrls = f.getControls();
	            int c = f.getColumnsCount();
	            c = c > ctrls.length ? c : ctrls.length;
	            if (cols < c) {
	                cols = c;
	            }
	        }
	        layout.numColumns = cols * this.getGridY();
	        
	        // second: place the created controls inside the grid layout cells
	        int i = 0;
	        for (IField f : fields.values()) {
	            Control[] ctrls = f.getControls();
	            if (ctrls.length == 0) {
	            	continue;
	            }
	            i++;
	            if (i % getGridY() == 0 && getGridY() != 1){
		            GridData gd = (GridData)ctrls[0].getLayoutData();
		            if (gd == null) {
		                gd = new GridData();
		                ctrls[0].setLayoutData(gd);
		            }
		            gd.horizontalIndent = 10;
	            }
	            // get the last r controls that should be spanned horizontally
	            // to fit into the grid
	            int r = ctrls.length % cols;
	            if (r > 0) {
	                GridData gd = (GridData)ctrls[ctrls.length-1].getLayoutData();
	                if (gd == null) {
	                    gd = new GridData();
	                    ctrls[ctrls.length-1].setLayoutData(gd);
	                }
	                gd.horizontalSpan = cols - r + 1;
	            }
	            if (f.getADField() != null){
	            	ADField field = (ADField)f.getADField();
	            	if (field.getIsSameline()){
	            		int num = (i - 1) % this.getGridY();
	            		if (num != 0){
	            			Label l = toolkit.createLabel(ctrls[0].getParent(), "");
	            			GridData gd = new GridData();
	            			gd.horizontalSpan = cols * (this.getGridY() - num);
	            			l.setLayoutData(gd);
	            			l.moveAbove(ctrls[0]);
	            		}
	                	GridData gd = (GridData)ctrls[ctrls.length-1].getLayoutData();
	                	if (gd == null) {
	                        gd = new GridData();
	                        ctrls[ctrls.length-1].setLayoutData(gd);
	                    }
	            		gd.horizontalSpan = cols * this.getGridY() - (r == 0 ? 1 : r - 1);
	            		gd = (GridData)ctrls[0].getLayoutData();
	    	            if (gd == null) {
	    	                gd = new GridData();
	    	                ctrls[0].setLayoutData(gd);
	    	            }
	    	            gd.horizontalIndent = 0;
	            		i = 0;
	            	}
	            }
	        }
	    
		}
	}
	
	protected ADTable getADTableBy(String tableName) throws Exception {
		ADTable adTable = null;
		ADManager adManager = Framework.getService(ADManager.class);
		adTable = adManager.getADTable(0L, tableName);
		return adTable;
	}
	
	public void registeValueChangeListener(){
		for (IField f : fields.values()){
			if (f instanceof RefTableField){
				RefTableField refField = (RefTableField)f;
				ADRefTable  refTable = (refField).getRefTable();
				if (refTable.getWhereClause() != null && !"".equalsIgnoreCase(refTable.getWhereClause().trim())
						&& StringUtil.parseClauseParam(refTable.getWhereClause()).size() > 0){
					List<String> paramList = StringUtil.parseClauseParam(refTable.getWhereClause());
					for (String param : paramList){
						IField listenField = fields.get(param);
						if(listenField != null) {
							listenField.addValueChangeListener(refField);
						}
					}
				}
			}
			if (f instanceof RefTextField) {
				try {
					ADField adField = (ADField)f.getADField();
					String refName = adField.getReferenceRule();
					String firstName = refName.substring(0, refName.indexOf("."));
					if (fields.containsKey(firstName)) {
						IField listenField = fields.get(firstName);
						if (listenField instanceof SearchField) {
							listenField.addValueChangeListener((RefTextField)f);
						} else if (listenField instanceof RefTableField) {
							listenField.addValueChangeListener((RefTextField)f);
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}
	
    protected KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					txtLotId.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR :
						addRackLot();
						break;
					case SWT.TRAVERSE_RETURN :
						addRackLot();
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at NewRackLotSection ：getKeyListener() ", e);
		}
		return null;
	}

	protected void addRackLot() {
		String lotId = txtLotId.getText();
		try {
			if(lotId != null && !"".equals(lotId)) {				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				
				
				QtySetupDialog qsd = new QtySetupDialog(UI.getActiveShell());
				if(qsd.open() == Window.OK){
					RacKMovementLot rLot = new RacKMovementLot();
					
					rLot.setIoType(RacKMovementLot.IO_TYPE_IN);
					rLot.setLotRrn(lot.getObjectRrn());
					rLot.setLotId(lot.getLotId());
					rLot.setMaterialRrn(lot.getMaterialRrn());
					rLot.setMaterialId(lot.getMaterialId());
					rLot.setMaterialName(lot.getMaterialName());
					rLot.setQty(qsd.getInputQty());
					getrLots().add(rLot);					
					refresh();
					setDoOprationsTrue();
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at NewRackLotSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	public void setIsSaved(boolean isSaved) {
		this.isSaved = isSaved;
		// 如果完成了保存(即isSaved = true)，则将isDid置为false，表示以前的操作已经保存了
		// 重新将isDid置为false, 表示从现在开始没有进行任何操作
		if(isSaved)
			this.setDoOprationsFalse();
	}
	
	protected void setDoOprationsTrue() {
		if(!isDid) this.isDid = true;
		//如果进行了操作(即isDid = true)，若isSaved为真，则将其置为false，表示以前的保存已经无效
		if(isSaved()) {
			setIsSaved(false);
		}
	}
	
	public boolean isSaved() {
		return isSaved;
	}

	public boolean isDid() {
		return isDid;
	}

	public void setDid(boolean isDid) {
		this.isDid = isDid;
	}
	
	protected void setDoOprationsFalse() {
		if(isDid) this.isDid = false;
	}
	
	protected List<?> getInput() {
		return getrLots();
	}

	public int getGridY() {
		return gridY;
	}

	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	public List<RacKMovementLot> getrLots() {
		return rLots;
	}

	public void setrLots(List<RacKMovementLot> rLots) {
		this.rLots = rLots;
	}
	
	
	public void refresh() {
		mmng.removeAllMessages();
		for (IField field : fields.values()) {
            field.refresh();
        }
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getInput());
			lotManager.updateView(viewer);
		}
	}
	
	// 如果进行了保存动作 或 没有进行界面操作则返回真
	protected boolean isSureExit() {
		if(isSaved() || !isDid())
			return true;
		return false;
	}
	
	protected void setEnable(boolean enabled) {
		itemSave.setEnabled(enabled);
		itemDelete.setEnabled(enabled);
	}
}
