package com.graly.erp.pdm.batchbom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BatchBomSection extends EntitySection {
	private static final Logger logger = Logger.getLogger(BatchBomSection.class);

	private final String TABLE_NAME_BOM = "BATCH_MODIFY_BOM";//PDM_BOM
	private ADTable pdmBomTable;
	private TableViewerManager tableManager;
	private TableViewer viewer;
	private Text text;
	private Material material;
	private List<Bom> input;
	protected ToolItem itemBatchRemove;
	protected ToolItem itemBatchUpdate;
	protected ToolItem itemBatchAddChildMaterial;
	protected ToolItem itemBatchUpdateChildMaterial;
	protected BigDecimal newUnitQty = BigDecimal.ZERO;
	
	protected static String URL = "http://192.168.0.235:81/products/import_materials.jsp?user_name="+Env.getUserName();
	protected static String URL2 = "http://192.168.0.235:81/products/import_update_materials.jsp?user_name="+Env.getUserName();
	
	public BatchBomSection() {
		super();
	}

	public BatchBomSection(ADTable table) {
		super(table);
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemBatchAddChildMaterial(tBar);
		createToolItemBatchUpdateChildMaterial(tBar);
		createToolItemBatchUpdate(tBar);
		createToolItemBatchRemove(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemBatchAddChildMaterial(ToolBar tBar) {
		if (table == null || table.getAuthorityKey() == null || table.getAuthorityKey().trim().length() == 0) {
			itemBatchAddChildMaterial = new ToolItem(tBar, SWT.PUSH);
		} else {
			itemBatchAddChildMaterial = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_BATCHBOM_UPDATECHILDMATERIAL);	
		}
		itemBatchAddChildMaterial.setText(Message.getString("common.batchUpdateChildMaterial"));
		itemBatchAddChildMaterial.setImage(SWTResourceCache.getImage("export"));
		itemBatchAddChildMaterial.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				importAdapter();
			}
		});
	}
	
	protected void createToolItemBatchUpdateChildMaterial(ToolBar tBar) {
		if (table == null || table.getAuthorityKey() == null || table.getAuthorityKey().trim().length() == 0) {
			itemBatchUpdateChildMaterial = new ToolItem(tBar, SWT.PUSH);
		} else {
			itemBatchUpdateChildMaterial = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_BATCHBOM_ADDCHILDMATERIAL);	
		}
		itemBatchUpdateChildMaterial.setText(Message.getString("common.batchAddChildMaterial"));
		itemBatchUpdateChildMaterial.setImage(SWTResourceCache.getImage("export"));
		itemBatchUpdateChildMaterial.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				importAdapter2();
			}
		});
	}
	
	protected void importAdapter() {
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), URL);
		bd.open();
	}
	
	protected void importAdapter2() {
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), URL2);
		bd.open();
	}
	
	protected void createToolItemBatchRemove(ToolBar tBar) {
		if (table == null || table.getAuthorityKey() == null || table.getAuthorityKey().trim().length() == 0) {
			itemBatchRemove = new ToolItem(tBar, SWT.PUSH);
		} else {
			itemBatchRemove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_BATCHBOM_DELETE);	
		}
		itemBatchRemove.setText(Message.getString("common.delete"));
		itemBatchRemove.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemBatchRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				batchRemoveAdapter();
			}
		});
	}
	
	protected void batchRemoveAdapter() {
		try {
			boolean flag = UI.showConfirm(Message.getString("common.confirm_delete"));
			if(flag){
				PDMManager manager = Framework.getService(PDMManager.class);
				manager.batchRemoveBom(input, material);
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void createToolItemBatchUpdate(ToolBar tBar) {
		if (table == null || table.getAuthorityKey() == null || table.getAuthorityKey().trim().length() == 0) {
			itemBatchUpdate = new ToolItem(tBar, SWT.PUSH);
		} else {
			itemBatchUpdate = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_BATCHBOM_EDIT);	
		}
		itemBatchUpdate.setText(Message.getString("common.edit"));
		itemBatchUpdate.setImage(SWTResourceCache.getImage("edit"));
		itemBatchUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				batchUpdateAdapter();
			}
		});
	}
	
	protected void batchUpdateAdapter() {
		BatchUpdateDialog bd = new BatchUpdateDialog(Display.getCurrent().getActiveShell(), this);
		if(bd.open() == Window.OK){
			try {
				PDMManager manager = Framework.getService(PDMManager.class);
				manager.batchUpdateBomUnitQty(input, getNewUnitQty());
				UI.showInfo(Message.getString("common.save_successed"));
				updateViewerContent();
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}
	}

	protected void createSectionTitle(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(3, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, Message.getString("pdm.material_id"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		text = toolkit.createText(top, "", SWT.BORDER);
		GridData gLabel = new GridData();
		gLabel.horizontalAlignment = GridData.FILL;
		gLabel.grabExcessHorizontalSpace = true;

		GridData gText = new GridData();
		gText.widthHint = 200;
		text.setLayoutData(gText);
		text.setTextLimit(32);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				Text txtMaterialId = ((Text) event.widget);
				txtMaterialId.setForeground(SWTResourceCache.getColor("Black"));
				switch (event.keyCode) {
				case SWT.CR:
					String materialId = txtMaterialId.getText();
					material = findMaterial(materialId);
					txtMaterialId.selectAll();
					if (material == null) {
						txtMaterialId.setForeground(SWTResourceCache.getColor("Red"));
						initAdObject();
						break;
					} else {
						updateMaterial(material);
						refresh();
					}
					break;
				}
			}

			public Material findMaterial(String materialId) {
				try {
					PDMManager pdmManager = Framework.getService(PDMManager.class);
					List<Material> list = pdmManager.getMaterialById(materialId,Env.getOrgRrn());
					if(list.size() > 1){
						UI.showError("More than one material of this materialId");
						return null;
					}
					return list.size() == 0 ? null : list.get(0);
				} catch (Exception e) {
					logger.error("BatchBomSection findMaterial(): Material isn' t exsited!");
					ExceptionHandlerManager.asyncHandleException(e);
					return null;
				}
			}
		});

		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Text txtMaterialId = ((Text) e.widget);
				txtMaterialId.setText(txtMaterialId.getText());
				txtMaterialId.selectAll();
			}
		});
	}
	
	protected void initAdObject() {
		Material m = new Material();
		m.setOrgRrn(Env.getOrgRrn());
		setAdObject(m);
		input = null;
		refresh();
	}

	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		createViewer(client,form.getToolkit());
	}

	private void createViewer(Composite client, FormToolkit toolkit) {
		try {
			tableManager = new TableListManager(getPdmBomTable());
//			tableManager.addStyle(SWT.CHECK);

			viewer = (TableViewer)tableManager.createViewer(client, toolkit);
//			viewer.addDoubleClickListener(getDoubleClickListener());
			
		} catch(Exception e) {
			logger.error("BatchBomSection : createViewer() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}
	
	private ADTable getPdmBomTable() {
		try {
			if (pdmBomTable != null) {
				return pdmBomTable;
			} else {
				ADManager entityManager = Framework.getService(ADManager.class);
				pdmBomTable = entityManager.getADTable(0L, TABLE_NAME_BOM);
				return pdmBomTable;
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	protected void updateMaterial(Material m) {
		try {
			setAdObject(m);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	private void updateViewerContent(){
		try {
			input = getParentBoms();
			viewer.setInput(input);
			tableManager.updateView(viewer);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	private List<Bom> getParentBoms() {
		List<Bom> boms = new ArrayList<Bom>();
		try {
			PDMManager manager = Framework.getService(PDMManager.class);
			if(material != null){
				boms = manager.getParentBomsOnlyUsefulInfos(material.getObjectRrn());
			}else{
				boms = null;
			}
		} catch (Exception e) {
			ExceptionHandlerManager.syncHandleException(e);
		}
		return boms;
	}

	@Override
	public void refresh() {
		super.refresh();
		updateViewerContent();
	}

	public List<Bom> getInput() {
		return input;
	}

	public void setInput(List<Bom> input) {
		this.input = input;
	}

	public BigDecimal getNewUnitQty() {
		return newUnitQty;
	}

	public void setNewUnitQty(BigDecimal newUnitQty) {
		this.newUnitQty = newUnitQty;
	}
}

class BrowserDialog extends TrayDialog{
	protected String url;
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 500;
	
	public BrowserDialog(Shell parentShell, String url) {
		super(parentShell);
		this.url = url;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Browser browser = new Browser(parent,SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
        gd.minimumWidth = MIN_DIALOG_WIDTH ;
        gd.minimumHeight = MIN_DIALOG_HEIGHT ;
        browser.setUrl(url);
        browser.setLayoutData(gd);
		browser.setUrl(url);
		return browser;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {}
	
}
