package com.graly.erp.wip.partlydisassemble;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.LotStorage;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.wip.disassemblelot.DisAssembleLotSection;
import com.graly.erp.wip.disassemblelot.LotComponentListManager;
import com.graly.erp.wip.workcenter.receive.LotTableManager;
import com.graly.erp.wip.workcenter.receive.MoLineReceiveDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;

public class PartlyDisAssembleLotSection extends EntitySection{
	private static final Logger logger = Logger.getLogger(PartlyDisAssembleLotSection.class);
	
	protected LotTableManager lotManager;
	protected StructuredViewer viewer;
	protected Section section;
	protected IFormPart spart;
	private ToolItem itemDisAssembleLot;
	protected Lot lot;
	protected List<Lot> lots = new ArrayList<Lot>();
	protected BigDecimal qtyDisassemble = BigDecimal.ZERO;
	private Text qtyDisassembleField;
	
	public PartlyDisAssembleLotSection(ADTable table) {
		super(table);
		this.table = table;
		this.lotManager = new LotTableManager(table, SWT.CHECK);
	}
	
	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		super.form = form;
		final FormToolkit toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(String.format(Message.getString("common.detail"),
				I18nUtil.getI18nMessage(getTable(), "label")));
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

		createSectionTitle(client);
		createSectionContent(client);
		
		Composite comp = toolkit.createComposite(client);
		GridLayout compLayout = new GridLayout();
		compLayout.numColumns = 2;
		comp.setLayout(compLayout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label label2 = toolkit.createLabel(comp, Message.getString("wip.lot_qty"));//数量
		label2.setForeground(SWTResourceCache.getColor("Folder"));
		GridData gLabel = new GridData();
		gLabel.horizontalAlignment = GridData.FILL;
		label2.setLayoutData(gLabel);
		qtyDisassembleField = toolkit.createText(comp, "", SWT.BORDER | SWT.READ_ONLY);
		GridData gText = new GridData();
		gText.widthHint = 200;
		qtyDisassembleField.setLayoutData(gText);
//		qtyDisassembleField.setEnabled(false);
		qtyDisassembleField.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		
		Label label1 = toolkit.createLabel(comp, "批次组件");
		GridData lblGd = new GridData(GridData.FILL_HORIZONTAL);
		lblGd.verticalIndent = 10;
		lblGd.horizontalSpan = 2;
		label1.setLayoutData(lblGd);
		
		createTableViewer(client,toolkit);
		
		
		//创建添加删除按钮
		Composite buttonBar = toolkit.createComposite(client);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		buttonBar.setLayout(gl);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.END;
		buttonBar.setLayoutData(gd);
		Button addButton = toolkit.createButton(buttonBar, "添加", SWT.NONE);
		
		addButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(lot != null){
					AttachSubLotDialog asd = new AttachSubLotDialog(UI.getActiveShell(), lot);
					if(asd.open() == Dialog.OK){
						lots = asd.getInputLots();
						viewer.setInput(lots);
						lotManager.updateView(viewer);
					}
				}
			}
			
		});
		Button removeButton = toolkit.createButton(buttonBar, "删除", SWT.NONE);
		
		toolkit.paintBordersFor(section);
		section.setClient(client);
		
		refresh();
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
	}
	
	protected void createSectionTitle(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(3, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, Message.getString("inv.lotid"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		Text text = toolkit.createText(top, "", SWT.BORDER);

		GridData gText = new GridData();
		gText.widthHint = 200;
		text.setLayoutData(gText);
		text.setTextLimit(32);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				Text tLotId = ((Text) event.widget);
				tLotId.setForeground(SWTResourceCache.getColor("Black"));
				switch (event.keyCode) {
				case SWT.CR:
					String lotId = tLotId.getText();
					lot = searchLot(lotId);
					tLotId.selectAll();
					if (lot == null || lot.getObjectRrn() == null) {
						tLotId.setForeground(SWTResourceCache.getColor("Red"));
						lot = new Lot();
						lot.setOrgRrn(Env.getOrgRrn());
					}else{
						QtySetupDialog qsd = new QtySetupDialog(UI.getActiveShell(), lot);
						if(qsd.open() == Dialog.OK){
							qtyDisassemble = qsd.getQtyDisassemble();
							lot.setQtyTransaction(qtyDisassemble);
							if(qtyDisassembleField != null){
								qtyDisassembleField.setText(qtyDisassemble.toString());
							}
						}
					}
					setAdObject(lot);
					refresh();
					
					break;
				}
			}
		});
		
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Text tLotId = ((Text) e.widget);
				tLotId.setText(tLotId.getText());
				tLotId.selectAll();
			}
		});
	}
	//从核销仓库中查找
	public Lot searchLot(String lotId) {
		String writeoffHouseName = "";
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			//获得当前区域的核销仓库
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(Env.getOrgRrn());
			assert writeOffWarehouse != null;
			writeoffHouseName = writeOffWarehouse.getWarehouseId();
//			return invManager.getLotByLotId(Env.getOrgRrn(), lotId, writeOffWarehouse.getObjectRrn());
			Lot lot =invManager.getLotByLotId(Env.getOrgRrn(), lotId);
			if (Lot.POSITION_WIP.equals(lot.getPosition())) {
				//WIP表示未入库
			} else {
				LotStorage lotStorage = invManager.getLotStorage(Env.getOrgRrn(), lot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), 1);
				lot.setQtyCurrent(lotStorage.getQtyOnhand());
			}
			return lot;
		} catch (Exception e) {
			logger.error("PartlyDisAssembleLotSection searchLot(): Lot isn' t exsited in " + writeoffHouseName + "!");
			ExceptionHandlerManager.asyncHandleException(e);
			return null;
		}
	}
	
//	public List<LotComponent> getAssembleLot(Lot lot) {
//		try {
//			if(lot == null || lot.getObjectRrn() == null){
//				return null;
//			}
//			WipManager wipManager = Framework.getService(WipManager.class);
//			return wipManager.getLotComponent(lot.getObjectRrn());
//		} catch (Exception e) {
//			logger.error("SearchByLotSection searchLot(): Lot isn' t exsited!");
//			ExceptionHandlerManager.asyncHandleException(e);
//			return null;
//		}
//	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemDisAssembleLot(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	private void createToolItemDisAssembleLot(ToolBar tBar) {
		itemDisAssembleLot = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_DISASSEMBLELOT_DISASSEMBLE);
		itemDisAssembleLot.setText(Message.getString("wip.disassemblelot"));
		itemDisAssembleLot.setImage(SWTResourceCache.getImage("split"));
		itemDisAssembleLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				disAssembleLotAdapter();
			}
		});
	}
	
	protected void refreshAdapter() {
		viewer.setInput(new ArrayList<Lot>());
	}
	
	private void disAssembleLotAdapter() {
		try {
			WipManager manager = Framework.getService(WipManager.class);
			lot = manager.partlyDisassembleLot(Env.getOrgRrn(), lot, lots, Env.getUserRrn());
			setAdObject(lot);
			UI.showInfo(Message.getString("common.operation_successful"));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
}
