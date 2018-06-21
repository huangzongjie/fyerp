package com.graly.erp.wip.lotpackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.WarehouseRack;
import com.graly.erp.wip.model.LargeLot;
import com.graly.erp.wip.model.LargeWipLot;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class LotPackageProperties extends EntityProperties {
	private static final Logger logger = Logger.getLogger(LotPackageProperties.class);
	
	public static final String tableName = "LargeWIPLot";
	private static final String SEARCH_TABLE_NAME = "INVLot";
	private static final String COLUMN_ID = "qty";
	
	protected Lot lot;
	
	private TableListManager tableViewerManager;
	private CheckboxTableViewer viewer;
	private ADTable tableManagerTable;
	private List<ADBase> input = new ArrayList<ADBase>();
	protected ToolItem itemPackage;
	protected ToolItem itemPrint;
	protected Button btnAdd;
	protected Button btnRemove;
	

	protected ADTable lotTable;
	
	public LotPackageProperties() {
		super();
	}

	public LotPackageProperties(EntityBlock masterParent, ADTable table) {
		super(masterParent, table);
	}

	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		createTableViewerContent(form, client);
	}
	

	protected void createSectionTitle(Composite client) {
	}
	
	protected void createTableViewerContent(IManagedForm form, Composite parent) {
		try {
			FormToolkit toolkit = form.getToolkit();
			Composite viewerBody = toolkit.createComposite(parent);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			viewerBody.setLayout(layout);
			viewerBody.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			ADManager adManager = Framework.getService(ADManager.class);
			if(tableManagerTable == null) {
				tableManagerTable = adManager.getADTable(0L, tableName);
			}
			tableViewerManager = new TableListManager(tableManagerTable, SWT.CHECK | SWT.FULL_SELECTION);
			viewer = (CheckboxTableViewer) tableViewerManager.createViewer(viewerBody, form.getToolkit(), 400);
			ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
				protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
					return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
							|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
							|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
				}
			};
			TableViewerEditor.create(viewer, actSupport,
					ColumnViewerEditor.TABBING_HORIZONTAL
							| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
							| ColumnViewerEditor.TABBING_VERTICAL
							| ColumnViewerEditor.KEYBOARD_ACTIVATION);
			
			setCellEditor(viewer);
			
			Composite buttonBar = toolkit.createComposite(viewerBody);
			buttonBar.setLayout(new GridLayout());
			
			
			lotTable = adManager.getADTable(0L, SEARCH_TABLE_NAME);
			
			btnAdd = toolkit.createButton(buttonBar, "添加", SWT.PUSH);
			btnAdd.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					LargeLot ll = (LargeLot) getAdObject();
					StringBuffer whereClause = new StringBuffer();
					if(ll != null && ll.getMaterialRrn() != null){
						whereClause.append(" materialRrn = " + ll.getMaterialRrn());
					}
					LotQueryDialog lotQueryDialog = new LotQueryDialog(new TableListManager(lotTable), whereClause.toString());
					if(Window.OK == lotQueryDialog.open()){
						List<ADBase> selectList = lotQueryDialog.getSelectionList();
						if(selectList==null || selectList.size()==0){
							return;
						}
						input = (List<ADBase>) viewer.getInput();
						Set materialRrns = validateInput(input, selectList);
						if(materialRrns.size() > 1){
							UI.showError("添加了不同物料的批次");
							return;
						}
						ll.setMaterialRrn((Long) materialRrns.iterator().next());
						for(ADBase base : selectList){
							Lot lot = (Lot)base;
							Boolean isp2 = validateDel(lot);
							if(isp2){
							LargeWipLot lwl = new LargeWipLot(lot);
							input.add(lwl);}
						}
						viewer.setInput(input);
						tableViewerManager.updateView(viewer);
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
			btnRemove = toolkit.createButton(buttonBar, "删除", SWT.PUSH);
			btnRemove.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					Object[] checkedElements = viewer.getCheckedElements();
					List input = (List) viewer.getInput();
					for(Object element : checkedElements){
						input.remove(element);
					}
					viewer.refresh();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
		} catch(Exception e) {
			logger.error(e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	
	protected void setCellEditor(TableViewer tableViewer) {
		int columnCout = tableViewer.getTable().getColumnCount();
		CellEditor[] cellEditor = new CellEditor[columnCout];
		String[] properties = new String[columnCout];
		String validateType = "String";
		
		for (int i = 0; i < columnCout; i++) {
				TableColumn tc = tableViewer.getTable().getColumn(i);
				if(COLUMN_ID.equals(tc.getData("id"))){
					properties[i] = (String) tc.getData("id");
					cellEditor[i] = new ModifyTextCellEditor(tableViewer, COLUMN_ID, validateType, i);				
				}
		}
		
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		EntityPropertyCellModify tcm = new EntityPropertyCellModify(tableViewer);
		tableViewer.setCellModifier(tcm);
	}

	
	protected Boolean validateDel(Lot lot) {
			try {
				WipManager wipManager = Framework.getService(WipManager.class);
				Boolean isp = wipManager.validateDelete(lot, Env.getOrgRrn(), Env.getUserRrn());
				if(isp == true){
					UI.showInfo("批次已经包装");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
	}
	
	protected Set validateInput(List<ADBase> input1, List<ADBase> input2) {
		List<ADBase> input3 = new ArrayList<ADBase>();
		input3.addAll(input1);
		input3.addAll(input2);
		Set materialRrns = new HashSet();
		for(ADBase obj : input3){
			if(obj instanceof Lot){
				Lot l = (Lot)obj;
				materialRrns.add(l.getMaterialRrn());
			}else if(obj instanceof LargeWipLot){
				LargeWipLot lwl = (LargeWipLot)obj;
				materialRrns.add(lwl.getMaterialRrn());
			}
		}
		
		return materialRrns;
	}

	public void initAdObject() {
		setAdObject(new LargeLot());
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemPackage(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}
	
	@Override
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new ToolItem(tBar, SWT.PUSH);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}
	
	protected void createToolItemPackage(ToolBar tBar) {
		itemPackage = new ToolItem(tBar, SWT.PUSH);
		itemPackage.setText("包装");
		itemPackage.setImage(SWTResourceCache.getImage("save"));
		itemPackage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				packageAdapter();
			}
		});
	}
	
	private void createToolItemPrint(ToolBar tBar) {
		itemPrint = new ToolItem(tBar, SWT.PUSH);
		itemPrint.setText(Message.getString("common.print"));
		itemPrint.setImage(SWTResourceCache.getImage("print"));
		itemPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printAdapter();
			}
		});
	}



	protected void printAdapter() {
		ADBase object = this.getAdObject();
		if(object != null && object instanceof LargeLot){
			LargeLot lLot = (LargeLot)object;
			if(lLot.getObjectRrn() != null){
				PrintDialog pd = new PrintDialog(lLot);
				pd.open();
			}
		}
	}
	
	protected void newAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			initAdObject();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void packageAdapter() {
		try {
			WipManager wipManager = Framework.getService(WipManager.class);
			wipManager.packageLots((LargeLot)getAdObject(), (List)viewer.getInput(), Env.getOrgRrn(), Env.getUserRrn());
			UI.showInfo("包装成功");
			getMasterParent().refresh();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}	
	}
	
	public void refresh() {
		try {
			reloadAdObject();
			super.refresh();
			refreshViewer();
		} catch (Exception e) {
			logger.error("LotPackageProperties : refresh()", e);
		}
	}

	private void reloadAdObject() throws Exception, ClientException {
		ADBase object = getAdObject();
		if(object != null && object.getObjectRrn() != null){
			ADManager manager = Framework.getService(ADManager.class);
			object = manager.getEntity(object);
			setAdObject(object);
		}
	}
	
	public void refreshViewer(){
		ADBase obj = getAdObject();
		if(obj != null){
			try {
				LargeLot ll = (LargeLot)obj;
				StringBuffer whereClause = new StringBuffer();
				whereClause.append(" largeLotRrn = " + ll.getObjectRrn());
				ADManager manager = Framework.getService(ADManager.class);
				input = manager.getEntityList(Env.getOrgRrn(), tableViewerManager.getADTable().getObjectRrn(), Env.getMaxResult(), whereClause.toString(), "");
				tableViewerManager.setInput(input);
				tableViewerManager.updateView(viewer);
			} catch (Exception e) {
				logger.error("LotPackageProperties : refreshViewer()",e);
			}
		}
	}
}

class ModifyTextCellEditor extends TextCellEditor {
	private Logger logger = Logger.getLogger(ModifyTextCellEditor.class);
	
	private TableViewer tableViewer;
	private String propertyName;
	private String validateType;
	private int colIndex;
	
	public ModifyTextCellEditor(TableViewer tableViewer,
			String propertyName, String validateType, int colIndex) {
        super(tableViewer.getTable());
        this.tableViewer = tableViewer;
        this.propertyName = propertyName;
        this.validateType = validateType;
        this.colIndex = colIndex;
    }

	@Override
	protected Control createControl(Composite parent) {
		super.createControl(parent);
		text.setTextLimit(32);
		text.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
            	TableItem[] items = tableViewer.getTable().getSelection();
            	if (items != null && items.length > 0){
            		TableItem item = items[0];
            		String value = text.getText();
            		
            		if (value != null && value.equals("")) {
            			// do nothing
					} else {
						if(validate(validateType, value)) { 
							setValue(item.getData(), value);
						} else {
							tableViewer.editElement(item.getData(), colIndex);
						}
					}
            	}
            }
        });
		return text;
	}

	public boolean validate(String type, String value) {
		if (value != null) {
//			if (value.startsWith(" ")) {
//    			UI.showError(Message.getString("inv.char_cannot_null"));
//    			return false;
//			}
			if (!ValidatorFactory.isValid(type, value)) {
				UI.showError(Message.getString("common.input_error"),
						Message.getString("common.inputerror_title"));
				return false;
			}
		}
		return true;
	}
	
	public void setValue(Object obj, String value) {
		if(obj != null && propertyName != null) {
			try {
				PropertyUtil.setProperty(obj, propertyName, value);
			} catch(Exception e) {
				logger.error(e);
			}
		}
	}
}


class EntityPropertyCellModify implements ICellModifier {
	private Logger logger = Logger.getLogger(EntityPropertyCellModify.class);

	private TableViewer tableViewer;

	public EntityPropertyCellModify(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		if(element != null && property != null) {
			try{
				Object obj = PropertyUtil.getPropertyForString(element, property);
				if(obj == null)
					return "";
				return (String)obj;
			} catch (Exception e){
				logger.error("Error: ", e);
			}
		}
		return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		tableViewer.refresh();
	}
}

