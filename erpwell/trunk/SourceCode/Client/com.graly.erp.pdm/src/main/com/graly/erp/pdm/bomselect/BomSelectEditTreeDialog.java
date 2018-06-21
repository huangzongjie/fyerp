package com.graly.erp.pdm.bomselect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.BomConstant;
import com.graly.erp.pdm.bomtype.BomTypeSelectDialog;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.MaterialActual;
import com.graly.erp.pdm.model.MaterialUnSelected;
import com.graly.erp.pdm.model.VBomType;
import com.graly.erp.pdm.optional.OptionalDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomSelectEditTreeDialog extends BomSelectTreeDialog {
	private static Logger logger = Logger.getLogger(BomSelectEditTreeDialog.class);
	protected static final String TABLE_NAME_VBOMTYPE = "VPDMBomType";
	
	protected ADTable bomTable;
	protected ToolItem itemEdit;
	protected ToolItem itemBomType;
	protected ToolItem itemUndoBom;
	protected ToolItem itemActual;
	protected ToolItem itemSave;
	protected ToolItem itemDelAll;
	protected ADManager adManager;
	private BomSelectTreeForm selectTreeForm;
	private Material preBomTypeMaterial;	// �Ѿ����浽DB�е�Bom Type
//	private Material selectBomTypeMaterial; // �û��ڽ������ѡ���Bom Type
	private HashMap<Bom, MaterialActual> bomMap;
	private HashMap<Bom, BomMemo> memoMap;
	
	protected boolean isSaved = false; // �Ƿ�����˱��涯��
	protected boolean isDid = false;   // �Ƿ�Խ�������˲���

	public BomSelectEditTreeDialog(Shell parent, IManagedForm form, Material material) {
		super(parent, form, material);
		initPreBomTypeMaterial();
		initBomMap();
	}

	@Override
	protected void createSectionContent(Composite section) {
		final IMessageManager mmng = form.getMessageManager();
		GridLayout gl = new GridLayout(1, false);
		section.setLayout(gl);
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		toolkit.paintBordersFor(section);
		
		bomTreeForm = selectTreeForm = new BomSelectTreeForm(section, SWT.NULL, material, mmng, this);
		bomTreeForm.setLayoutData(gd);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemActual(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemBomType(tBar);
//		createToolItemUndoBom(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDeleteAll(tBar);
		section.setTextClient(tBar);
		// ��ʼ����ť״̬�Ƿ����
		setStatusChanged();
	}
	
	protected void createToolItemActual(ToolBar tBar) {
		itemActual = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_EDIT_ACTUAL);
		itemActual.setText(Message.getString("pdm.actual"));
		itemActual.setImage(SWTResourceCache.getImage("optional"));
		itemActual.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				optionalAdapter(event);
			}
		});
	}
	
	protected void createToolItemBomType(ToolBar tBar) {
		itemBomType = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_EDIT_BOMTYPE);
		itemBomType.setText(Message.getString("pdm.bomtype"));
		itemBomType.setImage(SWTResourceCache.getImage("alternate"));
		itemBomType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				bomTypeAdapter(event);
			}
		});
	}
	
	protected void createToolItemUndoBom(ToolBar tBar) {
		itemUndoBom = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_UNDOBOM);
		itemUndoBom.setText(Message.getString("pdm.undo_bom"));
		itemUndoBom.setImage(SWTResourceCache.getImage("close"));
		itemUndoBom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				undoBomAdapter(event);
			}
		});
	}
	
	protected void createToolItemDeleteAll(ToolBar tBar) {
		itemDelAll = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_EDIT_DELETEALL);
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

	protected void bomTypeAdapter(SelectionEvent event) {
		try {
			TableListManager listTableManager = new TableListManager(getAdTableOfBom());
			int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;			
			SingleEntityQueryDialog singleDialog = new BomTypeSelectDialog(
					listTableManager, null, null, style);
			singleDialog.setTempSearchCondition(null);
			if(singleDialog.open() == IDialogConstants.OK_ID) {
				if(singleDialog.getSelectionEntity() instanceof VBomType) {
					VBomType vbt = (VBomType)singleDialog.getSelectionEntity();
					Material mater = new Material();
					mater.setObjectRrn(vbt.getObjectRrn());	//vbt.objectRrn�����д�BOM�ṹ�����ϵ�objectRrn
					mater.setMaterialId(material.getMaterialId());
					mater.setName(material.getName());
					mater.setInventoryUom(material.getInventoryUom());
					mater.setDescription(material.getDescription());
					mater.setBomId(vbt.getMaterialId());
					mater.setBomRrn(vbt.getObjectRrn());
					selectTreeForm.setSelectedBomType(mater);
					
					selectTreeForm.setUnSelectBomManager(null);
					selectTreeForm.refreshByOtherUnSelectBomManager(mater);
					initBomMap();
					this.setStatusChanged();
					this.setDoOprationsTrue();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
	
	protected void undoBomAdapter(SelectionEvent event) {
		try {
			if(material.getBomRrn() != null) {
				boolean isSureUndo = UI.showConfirm(String.format(Message.getString("pdm.sure_undo_bom_setup"),
						material.getMaterialId()));
				if(isSureUndo) {
					if(preBomTypeMaterial != null && preBomTypeMaterial.getObjectRrn() != null) {
						PDMManager pdmManager = Framework.getService(PDMManager.class);
						// ����Bom�����������Ƚ�material��moRrn��moId��Ϊnull, Ȼ��ʵ��ѡ�ϱ���materialRrnΪmaterial.objectRrn
						// �ļ�¼ɾ��(��ɾ��Ϊmaterial���õ�ʵ��ѡ�ϣ���Ϊʵ��ѡ��ֻ�����õ������ϵ���һ�����ϣ����԰������Ϸ���ɾ������)
						material = pdmManager.revokeBomTypeSetup(Env.getOrgRrn(), material.getObjectRrn(), Env.getUserRrn());
						preBomTypeMaterial = null;
					} else {
						material.setBomId(null);
						material.setBomRrn(null);
					}
					selectTreeForm.setSelectedBomType(null);
					bomTreeForm.refresh(material);
					setStatusChanged();
					this.setIsSaved(true);
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
	
	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_EDIT_SAVE);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter(event);
			}
		});
	}
	
	protected void saveAdapter(SelectionEvent event) {
		try {
			BomSelectTreeForm selectForm = (BomSelectTreeForm)bomTreeForm;
			List<MaterialActual> ams = selectForm.getActualMaterials(bomMap);
			List<MaterialUnSelected> uns = selectForm.getUnSelectedList(new ArrayList<MaterialUnSelected>(), null);
//			if(ams != null && ams.size() > 0) {
				
				Material bomMaterial = this.preBomTypeMaterial;
				if(selectTreeForm.getSelectedBomType() != null ) {
					bomMaterial = selectTreeForm.getSelectedBomType();
					//������BOM����ʱ��bomMaterial.materailIdΪԭ���ϵ�materialId���ڴ˱��뽫�任��bomId
					bomMaterial.setMaterialId(bomMaterial.getBomId());
					if(bomMaterial.getObjectRrn().equals(material.getBomRrn())) {
						material.setBomRrn(null);
					}
				}
				if(pdmManager == null)
					pdmManager = Framework.getService(PDMManager.class);
				pdmManager.saveMaterialBom(material, bomMaterial, ams, uns);
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				material = (Material)adManager.getEntity(material);
				UI.showInfo(Message.getString("common.save_successed"));
				this.preBomTypeMaterial = bomMaterial;
				selectTreeForm.setSelectedBomType(null);
				initBomMap();
				selectTreeForm.refresh(material);
				this.setStatusChanged();
				this.setIsSaved(true);
//			}
		} catch (Exception e1) {
			setIsSaved(true);
			ExceptionHandlerManager.asyncHandleException(e1);
			logger.error("Error at : ", e1);
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
					pdmManager.deleteAllBOM(material, 2, Env.getOrgRrn());
					if(adManager == null)
						adManager = Framework.getService(ADManager.class);
					material = (Material)adManager.getEntity(material);
					
					selectTreeForm.setSelectedBomType(null);
					preBomTypeMaterial = null;
					initBomMap();
					selectTreeForm.refresh(material);
					this.setStatusChanged();
					this.setIsSaved(true);
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}

	public void setEnable(boolean isEnableFromExternal) {
		itemActual.setEnabled(isEnableFromExternal);
	}
	
	public void refreshEditorBom(Bom editorBom) {
		super.refreshEditorBom(editorBom);
		setDoOprationsTrue();
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
					boolean isNeedReback = false;
					if(Bom.CATEGORY_OPTIONAL.equals(enableOptionalBom.getCategory())
							|| (memoMap != null && memoMap.get(enableOptionalBom) != null)) {
						isNeedReback = true;
					}
					OptionalDialog od = new OptionalDialog(UI.getActiveShell(), material,
							adTable, enableOptionalBom, rootMaterialRrn, isNeedReback);
					int code = od.open();
					if(code == Dialog.OK) {
						if(memoMap != null && memoMap.get(enableOptionalBom) == null) {
							BomMemo bm = new BomMemo();
							bm.setMoRrn(enableOptionalBom.getObjectRrn());
							bm.setUnitQty(enableOptionalBom.getUnitQty());
							bm.setCategory(bm.getCategory());
							bm.setDescription(enableOptionalBom.getDescription());
							bm.setChildMaterial(enableOptionalBom.getChildMaterial());
							memoMap.put(enableOptionalBom, bm);
						}
						enableOptionalBom.setUnitQty(od.getMaterialActual().getUnitQty());
						enableOptionalBom.setDescription(od.getMaterialActual().getDescription());
						enableOptionalBom.setCategory(Bom.CATEGORY_OPTIONAL);
						enableOptionalBom.setChildMaterial(od.getOptionalMaterial());
						bomMap.put(enableOptionalBom, od.getMaterialActual());
						bomTreeForm.refreshEditorBom(enableOptionalBom);
						this.setDoOprationsTrue();
					} else if(code == OptionalDialog.REBACK_ID && memoMap != null) {
						rebackOptionalBom();
					}
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("BomSelectEditTreeDialog : optionalAdapter()", e);
		}
	}
	
	protected void rebackOptionalBom() throws Exception {
		BomMemo bm = memoMap.get(enableOptionalBom);
		if(bm != null) {
			enableOptionalBom.setUnitQty(bm.getUnitQty());
			enableOptionalBom.setDescription(bm.getDescription());
			enableOptionalBom.setCategory(bm.getCategory());
			enableOptionalBom.setChildMaterial(bm.getChildMaterial());
			memoMap.remove(enableOptionalBom);
			bomTreeForm.refreshEditorBom(enableOptionalBom);
		}
		// ��ʾ�û���ѡ��ʵ��ѡ���Ѿ����浽DB�У������DB�еõ���Ӧ��ԭBom
		else if(Bom.CATEGORY_OPTIONAL.equals(enableOptionalBom.getCategory())) {
			Bom bom = getPreviousBom(selectTreeForm.getBomTypeParentRrn(enableOptionalBom),
					enableOptionalBom.getBomTypeChildRrn(), enableOptionalBom.getParentVersion());
			if(bom != null) {
				//��ʱ���ܽ�bom����enableOptionalBom��1.�ᵼ��ˢ��ʱ�޷���ȷ��ʾbom��Ϣ;
				//2.���û���������ʱ�����ڴ������List<MaterialActual>�����Բ���Ӱ��������ȷ��
				enableOptionalBom.setUnitQty(bom.getUnitQty());
				enableOptionalBom.setDescription(bom.getDescription());
				enableOptionalBom.setCategory(bom.getCategory());
				enableOptionalBom.setChildMaterial(bom.getChildMaterial());
				memoMap.remove(enableOptionalBom);
				bomTreeForm.refreshEditorBom(enableOptionalBom);
				setDoOprationsTrue();
			}
		}
	}
	
	private Bom getPreviousBom(Long parentRrn, Long childRrn, Long parenVersion) throws Exception {
		if(adManager == null)
			adManager = Framework.getService(ADManager.class);
		List<Bom> boms = adManager.getEntityList(Env.getOrgRrn(), Bom.class, 1,
				" parentRrn = " + parentRrn + " AND childRrn = " + childRrn + " AND parentVersion = " + parenVersion, null);
		if(boms != null && boms.size() > 0)
			return boms.get(0);
		return null;
	}
	
	protected ADTable getAdTableOfBom() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			if(bomTable == null)
				bomTable = entityManager.getADTable(0L, TABLE_NAME_VBOMTYPE);
		} catch(Exception e) {
			logger.error("OptionalDialog : initAdTableOfBom()", e);
		}
		return bomTable;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == Dialog.CANCEL) {
			if(isSureExit()) {
				cancelPressed();
			}
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
	// ��������˱��涯�� �� û�н��н�������򷵻���
	protected boolean isSureExit() {
		if(isSaved || !isDid)
			return true;
		return UI.showConfirm(Message.getString("inv.confirm_save_before_exit"));
	}
	
	public void setIsSaved(boolean isSaved) {
		this.isSaved = isSaved;
		// �������˱���(��isSaved = true)����isDid��Ϊfalse����ʾ��ǰ�Ĳ����Ѿ�������
		// ���½�isDid��Ϊfalse, ��ʾ�����ڿ�ʼû�н����κβ���
		if(isSaved)
			this.setDoOprationsFalse();
	}
	
	protected void setDoOprationsTrue() {
		if(!isDid) this.isDid = true;
		//��������˲���(��isDid = true)����isSavedΪ�棬������Ϊfalse����ʾ��ǰ�ı����Ѿ���Ч
		if(isSaved) {
			setIsSaved(false);
		}
	}
	
	protected void setDoOprationsFalse() {
		if(isDid) this.isDid = false;
	}
	
	protected void initPreBomTypeMaterial() {
		try {
			if(material != null
					&& material.getBomRrn() != null && material.getBomRrn().longValue() != 0) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				preBomTypeMaterial = new Material();
				preBomTypeMaterial.setObjectRrn(material.getBomRrn());
				preBomTypeMaterial = (Material)adManager.getEntity(preBomTypeMaterial);				
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			logger.error("Error at : ", e1);
		}
	}
	
	protected void initBomMap() {
		bomMap = new LinkedHashMap<Bom, MaterialActual>();
		memoMap = new LinkedHashMap<Bom, BomMemo>();
	}
}
