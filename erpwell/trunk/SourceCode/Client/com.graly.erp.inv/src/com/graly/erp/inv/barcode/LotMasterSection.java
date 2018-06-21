package com.graly.erp.inv.barcode;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotMasterSection {
	private static final Logger logger = Logger.getLogger(LotMasterSection.class);
	public static final String ORDERBY_LOT_ID = " lotId ";
	protected List<Lot> lots;

	protected TableViewerManager lotManager;
	protected TableViewer viewer;
	protected CheckboxTableViewer checkViewer;
	protected ADTable adTable;
	protected Section section;
	protected IFormPart spart;
	protected IManagedForm form;
	protected LotDialog parentDialog;

	protected ToolItem itemSave;
	protected ToolItem itemDelete;
	
	protected Text txtLotId;
	protected Lot lot = null;
	protected Lot selectLot;

	protected List<MovementLine> lines;
	
	protected boolean isSaved = false; // 是否进行了保存动作
	protected boolean isDid = false;   // 是否对界面进行了操作
	
	public LotMasterSection(){}
	
	public LotMasterSection(ADTable adTable) {
		this.adTable = adTable;
	}
	
	public LotMasterSection(ADTable adTable, LotDialog parentDialog) {
		this.adTable = adTable;
		this.parentDialog = parentDialog;
	}

	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		
		section = toolkit.createSection(parent, sectionStyle);
		setSectionTitle();
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);
		setItemInitStatus();
		
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
	    
	    createParentContent(client, toolkit);
	    createLotInfoComposite(client, toolkit);
	    createTableContent(client, toolkit);
	    section.setClient(client);
	    createViewAction(viewer);
	}
	
	protected void setSectionTitle() {
		section.setText(Message.getString("inv.lot_list"));
	}
	
	protected void createParentContent(Composite client, FormToolkit toolkit) {}
	
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
		initTableContent();
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new TableListManager(adTable);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		lotManager.updateView(viewer);
	}
	
	protected KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					txtLotId.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR :
						addLot();
						break;
					case SWT.TRAVERSE_RETURN :
						addLot();
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at LotMasterSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	protected void addLot() {
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
				if(validLot(lot)) {
					if(getLots().contains(lot)) {
						if(checkViewer != null) {
							checkViewer.setChecked(lot, true);
						}
					} else {
						getLots().add(lot);					
					}
					refresh();
					setDoOprationsTrue();
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	/* 子类可以重载该方法实现对Lot的验证，判断其是否符合要求*/
	protected boolean validLot(Lot lot) {
		return true;
	}

	protected void createViewAction(StructuredViewer viewer){
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setMovementLineSelect(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void setMovementLineSelect(Object obj) {
		if (obj instanceof Lot) {
			selectLot = (Lot) obj;
		} else {
			selectLot = null;
		}
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		section.setTextClient(tBar);
	}
	
	protected void setItemInitStatus() {}

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
        		if(obj instanceof Lot) {
        			boolean confirmDelete = UI.showConfirm(Message
        					.getString("common.confirm_delete"));
        			if (confirmDelete) {
        				delete((Lot)obj);
        			}
        		}
        	}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	protected void delete(Lot lot) {
		getLots().remove(lot);
		refresh();
		setDoOprationsTrue();
	}
	
	protected void saveAdapter() {
		setIsSaved(true);
	}
	
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getInput());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	protected void createSectionDesc(Section section){
		String text = Message.getString("common.totalshow");
		int count = viewer.getTable().getItemCount();
		if (count > Env.getMaxResult()) {
			text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
		} else {
			text = String.format(text, String.valueOf(count), String.valueOf(count));
		}
		section.setDescription("  " + text);
	}
	
	protected List<?> getInput() {
		return getLots();
	}

	protected List<Lot> getLots() {
		if(lots == null) {
			lots = new ArrayList<Lot>();
			return lots;
		}
		return lots;
	}

	/* 根据whereClause得到需要的Lot列表,
	 * 子类可重载getWhereClause()得到需要的Lots
	 */
	protected void initTableContent() {
		List<ADBase> list = null;
		try {
        	ADManager manager = Framework.getService(ADManager.class);
            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), getOrderByClause());
            List<Lot> l = new ArrayList<Lot>();
            for(ADBase ab : list) {
            	Lot lot = (Lot)ab;
            	l.add(lot);
            }
            setLots(l);
            refresh();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	logger.error(e.getMessage(), e);
        }
	}

	protected String getWhereClause() {
		return " 1 != 1 ";
	}
	
	protected String getOrderByClause() {
		return ORDERBY_LOT_ID;
	}
	
	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}
	
	// 如果进行了保存动作 或 没有进行界面操作则返回真
	protected boolean isSureExit() {
		if(isSaved() || !isDid())
			return true;
		return false;
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
	
	protected void setDoOprationsFalse() {
		if(isDid) this.isDid = false;
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
}
