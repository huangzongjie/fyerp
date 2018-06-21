package com.graly.erp.pdm.bomedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.material.refercedoc.ReferceDocDialog;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.MaterialOptional;
import com.graly.erp.pdm.optional.OptionalDialog;
import com.graly.erp.product.client.CANAManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ChildEntityBlockDialog;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomTreeDialog extends InClosableTitleAreaDialog {
	private static Logger logger = Logger.getLogger(BomTreeDialog.class);
	protected static final String PREFIX = " materialRrn = ";
	protected static int MIN_DIALOG_WIDTH = 700;
	protected static int MIN_DIALOG_HEIGHT = 450;
	protected IManagedForm form;
	protected Section section;
	protected ADTable adTable;
	
	protected ToolItem itemCRMimport;
	protected ToolItem itemNew;
	protected ToolItem itemCreateFrom;
	protected ToolItem itemEdit;
	protected ToolItem itemDelete;
	protected ToolItem itemUpload;
	protected ToolItem itemOptional;
	protected ToolItem itemAlternate;
	protected ToolItem itemExpend;
	protected ToolItem itemExport;
	protected ToolItem itemVerify;
	protected ToolItem itemReferenceDoc;
	protected ToolItem itemDelAll;
	protected ToolItem itemView;
	protected ToolItem itemPrepare;
	protected ToolItem itemClosePreare;
	
	protected BomTreeForm bomTreeForm;
	protected Material material;
	protected Bom newBom;
	protected Bom enableBom;  // �༭,ɾ���õ�Bom
	protected Bom enableOptionalBom;  // ��ѡ���õ�Bom
	protected Long parentRrn;   // ��parentRrn��������Bom����ʱ,ѡ���ѡ��ʱ�Ĵ��븸��
	protected Bom enableAlterBom; // ��������õ�Bom
	protected String path;        // ���������path, ��BomTreeForm�д���
	private String optionalMaterialId;
	protected PDMManager pdmManager;
	
	protected boolean isEnable = false;
	protected boolean isAlternate = false;
	protected String parentMaterialId;
	protected boolean flag = true;
	protected boolean editable = false;//Ĭ���ǲ鿴(���ɱ༭)
	private String childMaterialId;
	protected String upLoadError;
	private ADTable adTableDoc;
	private Material selectMaterial;
	private ADManager adManager;
	
	protected ADTable adTable_optional;
	protected ADTable adTable_alternate;
	
	protected boolean isChanged = false;
	
	public BomTreeDialog(Shell parent) {
        super(parent);
    }
	
	public BomTreeDialog(Shell parent, IManagedForm form, Material material){
		this(parent, form, material, false);
	}
	
	public BomTreeDialog(Shell parent, IManagedForm form, Material material, boolean editable){
		this(parent);
		this.form = form;
		this.material = material;
		this.editable = editable;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
//        setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(Message.getString("pdm.bom_list"));
        
        FormToolkit toolkit = form.getToolkit();
        Composite content = toolkit.createComposite(composite, SWT.NULL);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        content.setLayout(new GridLayout(1, false));

		section = toolkit.createSection(content, Section.TITLE_BAR);
		section.setText(Message.getString("pdm.bom_list_detail_info"));
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
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
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
		
		createViewAction(bomTreeForm.viewer);
		
        return composite;
	}
	
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void setSelectionRequisition(Object obj) {
		if (obj instanceof Material) {
			selectMaterial = (Material) obj;
		} else if (obj instanceof Bom){
			selectMaterial = ((Bom)obj).getChildMaterial();
		} else {
			selectMaterial = null;
		}
		
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		if(editable){
			createToolItemCRMimport(tBar);
			createToolItemNew(tBar);
			createToolItemCreateFrom(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemEdit(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemDelete(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemUpload(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemOptional(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemAlternate(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemVerify(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemExpendAll(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemExport(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemView(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemReferenceDoc(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemDeleteAll(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemPrepare(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemClosePrepare(tBar);
		}else{
			createToolItemExpendAll(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemExport(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemView(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemOptional(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemAlternate(tBar);
		}
		
		section.setTextClient(tBar);
		// ��ʼ����ť״̬�Ƿ����
		setStatusChanged();
	}
	
	protected void createToolItemView(ToolBar tBar) {
		itemView = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_PREVIEW);
		itemView.setText(Message.getString("common.print"));
		itemView.setImage(SWTResourceCache.getImage("preview"));
		itemView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				viewAdapter();
			}
		});
	}
	
	protected void createToolItemCreateFrom(ToolBar tBar) {
		itemCreateFrom = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_NEW);
		itemCreateFrom.setText(Message.getString("pur.copyfrom"));
		itemCreateFrom.setImage(SWTResourceCache.getImage("copy"));
		itemCreateFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				createFromAdapter(event);
			}
		});
	}

	protected void viewAdapter() {
		try {
			String report = "pdmBom_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			
			Long materialRrn = (material != null ? material.getObjectRrn() : null);
			
			if(materialRrn != null){
				userParams.put("MATERIAL_RRN", String.valueOf(materialRrn));
			}else{
				userParams.put("MATERIAL_RRN", null);
			}
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected ADTable getBomTable() {
		try {
			if(adTable != null) {
				return adTable;
			} else {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, BomConstant.TABLE_NAME_BOM);
				return adTable;				
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	private ADTable getRefenceDocTable() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTableDoc = entityManager.getADTable(0L, "BASMaterialDoc");
			adTableDoc = entityManager.getADTableDeep(adTableDoc.getObjectRrn());
			return adTableDoc;				
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	protected void createSectionContent(Composite section) {
		final IMessageManager mmng = form.getMessageManager();
		GridLayout gl = new GridLayout(1, false);
		section.setLayout(gl);
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		toolkit.paintBordersFor(section);
		
		bomTreeForm = new BomTreeForm(section, SWT.NULL, material, mmng, this);
		bomTreeForm.setLayoutData(gd);
	}
	
	protected void createToolItemCRMimport(ToolBar tBar) {
		itemCRMimport = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_NEW);
		itemCRMimport.setText("CRM����");
		itemCRMimport.setImage(SWTResourceCache.getImage("receive"));
		itemCRMimport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				crmImportAdapter();
			}
		});
	}
	
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter(event);
			}
		});
	}
	
	protected void createToolItemEdit(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editorwAdapter(event);
			}
		});
	}
	
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_IMPORT);
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
	
	private void createToolItemUpload(ToolBar tBar) {
		//���밴ť
		itemUpload = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_IMPORT);
		itemUpload.setText(Message.getString("ppm.upload"));
		itemUpload.setImage(SWTResourceCache.getImage("receive"));
		itemUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				uploadAdapter();
			}
		});
	}
	
	protected void createToolItemOptional(ToolBar tBar) {
		itemOptional = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_OPTIONAL);
		itemOptional.setText(Message.getString("pdm.optional"));
		itemOptional.setImage(SWTResourceCache.getImage("optional"));
		itemOptional.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				optionalAdapter(event);
			}
		});
	}

	protected void createToolItemAlternate(ToolBar tBar) {
		itemAlternate = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_ALTERNATE);
		itemAlternate.setText(Message.getString("pdm.alternate"));
		itemAlternate.setImage(SWTResourceCache.getImage("alternate"));
		itemAlternate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				alternateAdapter(event);
			}
		});
	}
	
	protected void createToolItemVerify(ToolBar tBar) {
		itemVerify = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_VERIFY);
		itemVerify.setText(Message.getString("pdm.bom_verify"));
		itemVerify.setImage(SWTResourceCache.getImage("approve"));
		itemVerify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				verifyAdapter(event);
			}
		});
	}
	
	protected void createToolItemExpendAll(ToolBar tBar) {
		itemExpend = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_EXPAND);
		itemExpend.setText(Message.getString("pdm.bom_expend_all"));
		itemExpend.setImage(SWTResourceCache.getImage("report"));
		itemExpend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				expendAllAdapter(event);
			}
		});
	}
	
	protected void createToolItemExport(ToolBar tBar) {
		if(Env.getOrgRrn() == 12644730L){
			//��̩Ҫ�󵼳����Ȩ��
			itemExport = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_VIEW_EXPORT);
		}else{
			itemExport = new ToolItem(tBar, SWT.PUSH);
		}
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
				TreeViewer viewer = bomTreeForm.viewer;
				Tree tree = viewer.getTree();
				int columnCount = tree.getColumnCount();
				List<String[]> ls = new LinkedList<String[]>();
				String[] headers = new String[columnCount+10];
				for(int i=0;i<10;i++){
					headers[i] = String.valueOf(i+1);
				}
				for(int i=0; i<tree.getColumnCount(); i++){
					headers[i+10] = tree.getColumn(i).getText();
				}
				ls.add(headers);
				for(TreeItem ti: tree.getItems()){
					int level = 0;
					String[] dt = new String[columnCount+10];
					for(int i=0; i<columnCount+10; i++){
						if(i<10){
							if(i==level){
								dt[i] = String.valueOf(i+1);
							}else{
								dt[i] = "";
							}
						}else{
							dt[i] = ti.getText(i-10);
						}
					}
					List<String[]> l = stepInTreeItems(ti,level,tree.getColumnCount());
					ls.add(dt);
					ls.addAll(l);
				}
				
				File file = new File(fn);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				CSVWriter writer = new CSVWriter(new FileWriter(file));
		        for (String[] strs : ls) {
		            writer.writeNext(strs);
		        }
		        writer.close();

			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected List<String[]> stepInTreeItems(TreeItem ti, int level, int columnCount) {
		int rootLevel = level;
		List<String[]> ls = new LinkedList<String[]>();
		
		for(TreeItem ti1 : ti.getItems()){
			level = rootLevel;
			level++;
			String[] dt = new String[columnCount+10];
			for(int i=0; i<columnCount+10; i++){
				if(i<10){
					if(i==level){
						dt[i] = String.valueOf(i+1);
					}else{
						dt[i] = "";
					}
				}else{
					dt[i] = ti1.getText(i-10);
				}
			}
			ls.add(dt);
			ls.addAll(stepInTreeItems(ti1, level, columnCount));
		}
		return ls;
	}


	protected void createToolItemReferenceDoc(ToolBar tBar) {
		itemReferenceDoc = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_REFERENCEDOC);
		itemReferenceDoc.setText(Message.getString("bas.refence_doc"));
		itemReferenceDoc.setImage(SWTResourceCache.getImage("search"));
		itemReferenceDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreenceDocAdapter();
			}
		});
	}
	
	protected void createToolItemDeleteAll(ToolBar tBar) {
		itemDelAll = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_DELETEALL);
		itemDelAll.setText(Message.getString("common.delete_all"));
		itemDelAll.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAllAdapter();
			}
		});
	}
	
	protected void newAdapter(SelectionEvent event) {
		if(material != null) {
			try {
				if(pdmManager == null)
					pdmManager = Framework.getService(PDMManager.class);
				newBom = pdmManager.newBom(material);
				ADTable table = getBomTable();
				BomEditDialog bed = new BomEditDialog(UI.getActiveShell(), table, newBom, this);
				bed.open(BomConstant.BOM_NEW);
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}
	}
	
	protected void createFromAdapter(SelectionEvent event) {
		try {
			if(material != null) {
				CreateFromDialog cfd = new CreateFromDialog(material, form.getToolkit());
				if(cfd.open() == Dialog.OK) {
					if(cfd.getBoms() == null || cfd.getBoms().size() == 0)
						return;
					if(pdmManager == null)
						pdmManager = Framework.getService(PDMManager.class);
					pdmManager.saveCategoryBom(cfd.getBoms(), new ArrayList<MaterialOptional>(), Env.getUserRrn());
					if(adManager == null)
						adManager = Framework.getService(ADManager.class);
					material = (Material)adManager.getEntity(material);
					this.bomTreeForm.setObject(material);
					this.bomTreeForm.refresh();
					setIsChanged(true);//���ò�����־
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void editorwAdapter(SelectionEvent event) {
		if(material != null && enableBom != null) {
			ADTable table = getBomTable();
			BomEditDialog bed = new BomEditDialog(UI.getActiveShell(), table, enableBom, this);
			bed.open(BomConstant.BOM_EDIT);
			/* enableBomֵ�ĺ󣬿�����Ҫˢ��enableBom��Bom��, ��enableBom��enableAlter
			 * ��enableOptionalΪͬһ����ʱ,Ӧ��enableBom��������
			 */
		}
	}
	
	protected void deleteAdapter() {
		boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
		if (confirmDelete) {
			try {
				if (enableBom != null && enableBom.getObjectRrn() != null) {
					if(pdmManager == null)
						pdmManager = Framework.getService(PDMManager.class);
					pdmManager.deleteBom(enableBom, Env.getUserRrn());
					bomTreeForm.removeBom(enableBom);
					if(enableBom.equals(enableOptionalBom)) {
						setEnableOptional(null, null);
					}
					if(enableBom.equals(this.enableAlterBom)) {
						setEnableAlter(null, null);
					}
					setActiveBom(null); // enableBom ɾ��֮�󣬽�enableBom��Ϊ��ֵ
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
			}
		}
	}
	
	protected void optionalAdapter(SelectionEvent event) {
		Long bomRrn = material.getBomRrn();
		try {
			ADTable adTable = initAdTableByName(BomConstant.TABLE_NAME_OPTIONAL);
			if(enableOptionalBom != null) {
				if(bomRrn != null && bomRrn.longValue() != 0) {
					// ��Bom���࣬������ѡ����,��ʵ��ѡ��
					Long rootMaterialRrn = parentRrn;
					if(material.getBomRrn() != null) {
						rootMaterialRrn = material.getBomRrn();
					}
					OptionalDialog od = new OptionalDialog(UI.getActiveShell(), material,
							adTable, enableOptionalBom, rootMaterialRrn, false);
					if(od.open() == Dialog.OK) {
						this.bomTreeForm.refresh(material);
					}
				} else {
					/* �򿪿�ѡ�϶Ի���, �ҳ�������Ϊmaterial.getObjectRrn(), ����ΪchildRrn�Ŀ�ѡ���б� */
					String whereClause = PREFIX + material.getObjectRrn()
					+ " AND childRrn = " + enableOptionalBom.getChildRrn();
					ChildEntityBlockDialog cd = new ChildEntityBlockDialog(UI.getActiveShell(), adTable, whereClause, material);
					OptionalProperties.setChildRrn(enableOptionalBom.getChildRrn());
					OptionalProperties.setEnableOptionalBom(enableOptionalBom);
					if(cd.open() == Dialog.CANCEL) {
						if(adManager == null) {
							adManager = Framework.getService(ADManager.class);
						}
						enableOptionalBom = (Bom)adManager.getEntity(enableOptionalBom);
						bomTreeForm.refreshBomOnly(enableOptionalBom);
					}
				}
			}
		} catch(Exception e) {
			logger.error("BomTreeDialog : bomTreeAdapter()", e);
		}
	}

	protected void alternateAdapter(SelectionEvent event) {
		form.getMessageManager().removeAllMessages();
		try {
			if (material != null && enableAlterBom != null) {
				ADTable adTable = initAdTableByName(BomConstant.TABLE_NAME_ALTERNAE);
				/* �ҳ�������Ϊmaterial.getObjectRrn(), ����ΪenableAlterBom.getChildRrn()�Ŀ�������б� */
				String whereClause = PREFIX + material.getObjectRrn()
				+ " AND path = '" + path
				+ "' AND childRrn = " + enableAlterBom.getChildRrn();
				ChildEntityBlockDialog cd = new ChildEntityBlockDialog(UI.getActiveShell(), adTable, whereClause, material);
				AlternateProperties.setChildRrn(enableAlterBom.getChildRrn(), this.path);
				AlternateProperties.setEnableAlterBom(enableAlterBom);
				if(cd.open() == Dialog.CANCEL) {
					// ˢ�¿�����enableAlterBom��
					if(adManager == null) {
						adManager = Framework.getService(ADManager.class);
					}
					enableAlterBom = (Bom)adManager.getEntity(enableAlterBom);
					bomTreeForm.refreshBomOnly(enableAlterBom);
				}
			}
		} catch(Exception e) {
			logger.error("BomTreeDialog : alternateAdapter()", e);
		}
	}
	
	protected void verifyAdapter(SelectionEvent event) {
		try {
			if(pdmManager == null)
				pdmManager = Framework.getService(PDMManager.class);
			pdmManager.verifyBOM(material.getObjectRrn());
			UI.showInfo(Message.getString("pdm.bom_verify_success"));
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
	
	protected void expendAllAdapter(SelectionEvent event) {
		try {
			bomTreeForm.setObject(material);
			bomTreeForm.refreshAll();
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
	
	protected void refreenceDocAdapter() {
		adTableDoc = getRefenceDocTable();
		ReferceDocDialog referceDocDialog = new ReferceDocDialog(UI.getActiveShell(), adTableDoc, selectMaterial);
		if(referceDocDialog.open() == Dialog.OK){
			
		}
	}
	
	protected void deleteAllAdapter() {
		try {
			if (material != null) {
				boolean confirmDelete = UI.showConfirm(String
						.format(Message.getString("pdm.configure.bom_all"), material.getMaterialId()));
				if (confirmDelete) {
					if(pdmManager == null)
						pdmManager = Framework.getService(PDMManager.class);
					pdmManager.deleteAllBOM(material, 1, Env.getOrgRrn());
					if(adManager == null)
						adManager = Framework.getService(ADManager.class);
					material = (Material)adManager.getEntity(material);
					
					bomTreeForm.refresh(material);
					setStatusChanged();
					this.setActiveBom(null);
					this.setEnableAlter(null, null);
					this.setEnableOptional(null, null);
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}

	protected void setStatusChanged() {		
		Long bomRrn = material.getBomRrn();
		if(bomRrn != null && bomRrn.longValue() != 0) {
			isAlternate = false;
			isEnable = false;
			if(itemNew != null) itemNew.setEnabled(false);
		} else {
			isEnable = true;
			isAlternate = true;
			if(itemNew != null) itemNew.setEnabled(true);
		}		
		setEnable(false);
		setIsAlternate(false);
	}
	
	public void setEnable(boolean isEnableFromExternal) {
		// itemOptional����������Bom����ʱ������,ֻ�Ǳ��������ϵ�ֱ��������,�����ڴ˿���
		if(itemOptional != null) itemOptional.setEnabled(isEnableFromExternal);
		if(isEnable) {
			if(itemEdit != null) itemEdit.setEnabled(isEnableFromExternal);
			if(itemDelete != null) itemDelete.setEnabled(isEnableFromExternal);
		} else {
			if(itemEdit != null) itemEdit.setEnabled(false);
			if(itemDelete != null) itemDelete.setEnabled(false);
		}
	}
	
	public void setIsAlternate(boolean isAlternateFromExternal) {
		if(itemAlternate != null && !itemAlternate.isDisposed()) {
			if(isAlternate) {
				itemAlternate.setEnabled(isAlternateFromExternal);
			} else {
				itemAlternate.setEnabled(false);
			}			
		}
	}
	
	public void setActiveBom(Bom bom) {
		this.enableBom = bom;
//		if(enableBom != null) {
			setEnable(true);
//		} else {
//			setEnable(false);
//		}
	}
	
	public void setEnableOptional(Bom bom, Long parentRrn) {
		enableOptionalBom = bom;
		this.parentRrn = parentRrn;
	}
	
	public void setEnableAlter(Bom bom, String path) {
		enableAlterBom = bom;
		this.path = path;
		if(enableAlterBom != null) {
			setIsAlternate(true);
		} else setIsAlternate(false);
	}
	
	public void addNewBom(Bom newBom) {
		bomTreeForm.refreshNewBom(newBom);
	}
	
	public void refreshEditorBom(Bom editorBom) {
		bomTreeForm.refreshEditorBom(editorBom);
	}
	
	protected ADTable initAdTableByName(String tableName) {
		try {
			if(adManager == null)
				adManager = Framework.getService(ADManager.class);
			if(BomConstant.TABLE_NAME_OPTIONAL.equals(tableName)) {
				if(adTable_optional == null) {
					adTable_optional = adManager.getADTable(0L, tableName);
					adTable_optional = adManager.getADTableDeep(adTable_optional.getObjectRrn());
				}
				return adTable_optional;
			} else if(BomConstant.TABLE_NAME_ALTERNAE.equals(tableName)) {
				if(adTable_alternate == null) {
					adTable_alternate = adManager.getADTable(0L, tableName);
					adTable_alternate = adManager.getADTableDeep(adTable_alternate.getObjectRrn());
				}
				return adTable_alternate;
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	protected void uploadAdapter() {
		if(material != null){
			FileInputStream fis = null;
			try {
				FileDialog fileDialog = new FileDialog(UI.getActiveShell(), SWT.OPEN);
				fileDialog.setFilterPath("C:/");
				String[] filterExt = { "*.xls"};
				fileDialog.setFilterExtensions(filterExt);
				String selectedFile = fileDialog.open();
				if (selectedFile != null) {
					fis = new FileInputStream(selectedFile);
					HSSFSheet[] sheets = initExcelread(fis, new int[]{0, 1});

					List<Bom> boms = new ArrayList<Bom>();
					for (int i = 1; i < sheets[0].getPhysicalNumberOfRows(); i++) {
						flag = true;
						HSSFRow row = sheets[0].getRow(i);
						// ִ�е�i������
						flag = executeBomWorked(row, i, boms);
						if(!flag){
							return;
						}
					}
					List<MaterialOptional> optionalBoms = new ArrayList<MaterialOptional>();
					for (int i = 1; i < sheets[1].getPhysicalNumberOfRows(); i++) {
						flag = true;
						HSSFRow row = sheets[1].getRow(i);
						// ִ�е�i������
						flag = executeOptionalWorked(row, i, optionalBoms);
						if(!flag){
							return;
						}
					}
					
					if(boms.size() != 0){
						PDMManager pdmManager = Framework.getService(PDMManager.class);
						pdmManager.saveCategoryBom(boms, optionalBoms, Env.getUserRrn());
					}
					// ��ʾ�ѳɹ������ʧ��
					UI.showInfo(Message.getString("ppm.upload_successful"));
					setIsChanged(true);//���ò�����־
					//refresh
					bomTreeForm.refresh(material);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (Exception e) {
					
				}
			}
		}
		}

	public HSSFSheet[] initExcelread(FileInputStream fis, int[] sheetIndex) {
		try {
			POIFSFileSystem fs = new POIFSFileSystem(fis);
			HSSFWorkbook wb = new HSSFWorkbook(fs); // ��ȡexcel������
			HSSFSheet[] sheets = new HSSFSheet[sheetIndex.length];
			for (int i = 0; i < sheetIndex.length; i++) {
				sheets[i] = wb.getSheetAt(sheetIndex[i]);
			}
			 // ��ȡexcel��sheet
			return sheets;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected boolean executeBomWorked(HSSFRow row, int index, List<Bom> boms) {
		Bom bom = new Bom();
		bom.setOrgRrn(Env.getOrgRrn());
		try {
			flag = readBomRow(row, bom);
			if (flag) {
				if (bom.getParentRrn() != null && bom.getChildRrn() != null
						&& bom.getUnitQty() != null) {
					boms.add(bom);
				}else{
					UI.showError(Message.getString("error.infomation_is_not_the_full"));
					return false;
				}
				return true;
			} else {
				if(upLoadError != null){
					UI.showError(upLoadError);
				}
				return false;
			}
		} catch(Exception e) {
			return false;
		}
	}
	
	protected boolean readBomRow(HSSFRow row, Bom bom) {
		int j = 0;
		try {
			if (row != null) {
				for (j = 0; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);
					//����BUG��������Ϊ�ճ����쳣���º���ĵ�Ԫ��ֵû�д���
					if(cell ==null && j==3){
						continue;
					}
					if(cell ==null && j==4){
						continue;
					}
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if (j == 0) {
							parentMaterialId = cell.getStringCellValue();
							if(parentMaterialId != null){
								if((material.getMaterialId()).equals(parentMaterialId)){
									bom.setParentRrn(material.getObjectRrn());
								} else {
									upLoadError = String.format(Message.getString("error.parentmaterial_is_must_same"), material.getMaterialId());
									return false;
								}
							}
						}else if(j == 1){
							childMaterialId = cell.getStringCellValue();
							if(childMaterialId != null){
								Material childMaterial = getMaterialById(childMaterialId);
								if(childMaterial != null){
									bom.setChildRrn(childMaterial.getObjectRrn());
								}else{
									upLoadError = String.format(Message.getString("error.childmaterial_is_not_exist"), row.getRowNum(), j+1);
									return false;
								}
							}
						}else if(j == 2){
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null){
									BigDecimal unitQty = BigDecimal.valueOf(l);
									bom.setUnitQty(unitQty);
								}else{
									upLoadError = String.format(Message.getString("error.unitqty_must_number"), row.getRowNum(), j+1);
									return false;
								}
							} catch (Exception e) {
								upLoadError = String.format(Message.getString("error.unitqty_must_number"), row.getRowNum(), j+1);
								return false;
							}
						}else  if(j == 3){
							String discription = cell.getStringCellValue();
							if(discription != null){
								bom.setDescription(discription);
							}
						}else if(j == 4){
							//�⹺�����ϡ����Ƽ��Ƿ���Ԥ����
							String isPrepareBomPurchase = cell.getStringCellValue();
							if(isPrepareBomPurchase != null && "Y".equals(isPrepareBomPurchase)){
								bom.setIsPrepareBomPurchase(true);
							}
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (j == 2) {
							BigDecimal unitQty = BigDecimal.valueOf(cell.getNumericCellValue());
							if (unitQty != null) {
								bom.setUnitQty(unitQty);
							}
						}else if (j == 1) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)����ʼλ�ÿ�ʼ������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							childMaterialId = sb.append(initId).toString();
							if(childMaterialId != null){
								Material childMaterial = getMaterialById(childMaterialId);
								if(childMaterial != null){
									bom.setChildRrn(childMaterial.getObjectRrn());
								}else{
									upLoadError = String.format(Message.getString("error.childmaterial_is_not_exist"), row.getRowNum(), j+1);
									return false;
								}
							}
						}else if (j == 0) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)����ʼλ�ÿ�ʼ������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							parentMaterialId = sb.append(initId).toString();
							if(parentMaterialId != null){
								if((material.getMaterialId()).equals(parentMaterialId)){
									bom.setParentRrn(material.getObjectRrn());
								}else{
									upLoadError = String.format(Message.getString("error.parentmaterial_is_must_same"), material.getMaterialId());
									return false;
								}
							}
						}else if(j == 3){
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String description = String.valueOf(parse.format(d));
							if(description != null){
								bom.setDescription(description);
							}
						}else if(j == 4){
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String isPrepareBomPurchase = String.valueOf(parse.format(d));
							if(isPrepareBomPurchase != null && "Y".equals(isPrepareBomPurchase)){
								bom.setIsPrepareBomPurchase(true);
							}
						}
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						break;
					default:
						break;
					}
				}
			}
			return true;
		} catch (Exception e) {
			if(j == 3){
				return true;
			}
			upLoadError = String.format(Message.getString("error.row_has_null_column"), row.getRowNum(), j+1);
			return false;
		}
	}
	
	protected boolean executeOptionalWorked(HSSFRow row, int index, List<MaterialOptional> optionalBoms) {
		MaterialOptional optionalBom = new MaterialOptional();
		optionalBom.setOrgRrn(Env.getOrgRrn());
		try {
			flag = readOptionalRow(row, optionalBom);
			if (flag) {
				if (optionalBom.getMaterialRrn() != null && optionalBom.getChildRrn() != null
						&& optionalBom.getOptionRrn() != null) {
					optionalBoms.add(optionalBom);
				} else {
					UI.showError(Message.getString("error.infomation_is_not_the_full"));
					return false;
				}
				return true;
			} else {
				if(upLoadError != null){
					UI.showError(upLoadError);
				}
				return false;
			}
		} catch(Exception e) {
			return false;
		}
	}
	
	protected boolean readOptionalRow(HSSFRow row, MaterialOptional optionalBom) {
		int j = 0;
		try {
			if (row != null) {
				for (j = 0; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);

					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if (j == 0) {
							parentMaterialId = cell.getStringCellValue();
							if(parentMaterialId != null){
								if((material.getMaterialId()).equals(parentMaterialId)){
									optionalBom.setMaterialRrn(material.getObjectRrn());
								} else {
									upLoadError = String.format(Message.getString("error.parentmaterial_is_must_same"), material.getMaterialId());
									return false;
								}
							}
						} else if(j == 1){
							childMaterialId = cell.getStringCellValue();
							if(childMaterialId != null){
								Material childMaterial = getMaterialById(childMaterialId);
								if(childMaterial != null){
									optionalBom.setChildRrn(childMaterial.getObjectRrn());
								}else{
									upLoadError = String.format(Message.getString("error.childmaterial_is_not_exist"), row.getRowNum(), j+1);
									return false;
								}
							}
						} else if(j == 2){
							optionalMaterialId = cell.getStringCellValue();
							if(optionalMaterialId != null){
								Material optionalMaterial = getMaterialById(optionalMaterialId);
								if(optionalMaterial != null){
									optionalBom.setOptionRrn(optionalMaterial.getObjectRrn());
								}else{
									upLoadError = String.format(Message.getString("error.optionalmaterial_is_not_exist"), row.getRowNum(), j+1);
									return false;
								}
							}
						} else if(j == 3){
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null){
									BigDecimal unitQty = BigDecimal.valueOf(l);
									optionalBom.setUnitQty(unitQty);
								}
							} catch (Exception e) {
								upLoadError = String.format(Message.getString("error.unitqty_must_number"), row.getRowNum(), j+1);
								return false;
							}
						}else  if(j == 4){
							String discription = cell.getStringCellValue();
							if(discription != null){
								optionalBom.setDescription(discription);
							}
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (j == 0) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)����ʼλ�ÿ�ʼ������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							parentMaterialId = sb.append(initId).toString();
							if(parentMaterialId != null){
								if((material.getMaterialId()).equals(parentMaterialId)){
									optionalBom.setMaterialRrn(material.getObjectRrn());
								}else{
									upLoadError = String.format(Message.getString("error.parentmaterial_is_must_same"), material.getMaterialId());
									return false;
								}
							} 
						} else if (j == 1) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)����ʼλ�ÿ�ʼ������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							childMaterialId = sb.append(initId).toString();
							if(childMaterialId != null){
								Material childMaterial = getMaterialById(childMaterialId);
								if(childMaterial != null){
									optionalBom.setChildRrn(childMaterial.getObjectRrn());
								}else{
									upLoadError = String.format(Message.getString("error.childmaterial_is_not_exist"), row.getRowNum(), j+1);
									return false;
								}
							}
						} else if (j == 2) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)����ʼλ�ÿ�ʼ������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							optionalMaterialId = sb.append(initId).toString();
							if(optionalMaterialId != null){
								Material optionalMaterial = getMaterialById(optionalMaterialId);
								if(optionalMaterial != null){
									optionalBom.setOptionRrn(optionalMaterial.getObjectRrn());
								}else{
									upLoadError = String.format(Message.getString("error.optionalmaterial_is_not_exist"), row.getRowNum(), j+1);
									return false;
								}
							}
						} else if (j == 3) {
							BigDecimal unitQty = BigDecimal.valueOf(cell.getNumericCellValue());
							if (unitQty != null) {
								optionalBom.setUnitQty(unitQty);
							}
						} else if(j == 4){
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String description = String.valueOf(parse.format(d));
							if(description != null){
								optionalBom.setDescription(description);
							}
						}
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						break;
					default:
						break;
					}
				}
			}
			return true;
		} catch (Exception e) {
			if(j == 3){
				return true;
			}
			upLoadError = String.format(Message.getString("error.row_has_null_column"), row.getRowNum(), j+1);
			return false;
		}
	}

	protected Material getMaterialById(String materialId) {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			List<Material> list = adManager.getEntityList(Env.getOrgRrn(), Material.class, 2, " materialId = '" + materialId + "' ", "");
			if (list != null || list.size() != 0) {
				Material material = (Material) list.get(0);
				return material;
			}
		} catch (Exception e) {
		}
		return null;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setIsChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	
	protected void createToolItemPrepare(ToolBar tBar) {
		String KEY_MATERIAL_PREPARE = "PDM.Material.Prepare";
		itemPrepare = new AuthorityToolItem(tBar, SWT.PUSH, KEY_MATERIAL_PREPARE);
		itemPrepare.setText("����Ԥ����");
		itemPrepare.setImage(SWTResourceCache.getImage("edit"));
		itemPrepare.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				prepareAdapter();
			}
		});
	}
	
	protected void createToolItemClosePrepare(ToolBar tBar) {
		String KEY_MATERIAL_CLOSEPREPARE = "PDM.Material.closePrepare";
		itemClosePreare = new AuthorityToolItem(tBar, SWT.PUSH, KEY_MATERIAL_CLOSEPREPARE);
		itemClosePreare.setText("ȡ��Ԥ����");
		itemClosePreare.setImage(SWTResourceCache.getImage("edit"));
		itemClosePreare.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closePrepareAdapter();
			}
		});
	}
	
	protected void crmImportAdapter() {
		try {
			if(material != null) {
			CANAManager canaManager;
//						ADManager	adManager = Framework.getService(ADManager.class);
//						List ab = adManager.getEntityList(Env.getOrgRrn(), CanaBomRequest.class,Integer.MAX_VALUE,null,null);
				canaManager = Framework.getService(CANAManager.class);
				canaManager.importBomFromCrm(material,Env.getOrgRrn(),Env.getUserRrn());
				UI.showInfo("����ɹ�");
				this.bomTreeForm.refresh();
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
	
	protected void prepareAdapter() {
		try {
			if(material != null && enableBom != null && !enableBom.getIsPrepareBomPurchase()) {
				enableBom.setIsPrepareBomPurchase(true);
				ADManager adManager = Framework.getService(ADManager.class);
				adManager.saveEntity(enableBom, Env.getUserRrn());
				this.bomTreeForm.refresh();
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
	
	protected void closePrepareAdapter() {
		try {
			if(material != null && enableBom != null && enableBom.getIsPrepareBomPurchase()) {
				enableBom.setIsPrepareBomPurchase(false);
				ADManager adManager = Framework.getService(ADManager.class);
				adManager.saveEntity(enableBom, Env.getUserRrn());
				this.bomTreeForm.refresh();
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
}
