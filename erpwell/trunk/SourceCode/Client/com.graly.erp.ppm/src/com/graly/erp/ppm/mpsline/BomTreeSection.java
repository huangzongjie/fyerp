package com.graly.erp.ppm.mpsline;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.erp.pdm.model.MaterialAlternate;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.MpsLineBom;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class BomTreeSection {
	private static final Logger logger = Logger.getLogger(BomTreeSection.class);
	private BomTreeManager treeManager;
	private StructuredViewer viewer;
	private ADTable table;
	private Object input;

	protected Section section;
	protected IFormPart spart;
	protected IManagedForm form;
	protected ToolItem alterItem;
	protected ToolItem revertItem;
	protected ToolItem revertAllItem;
	protected ToolItem deleteItem;
	
	private Material rootMaterial;
	protected MpsLineBom selectedBom;
	private MpsLine mpsLine;
	
	protected PPMManager ppmManager;
	protected PDMManager pdmManager;
	protected HashMap<MpsLineBom, List<MpsLineBom>> bomListMap;
	protected List<MpsLineBom> alternateBoms;

	public BomTreeSection(ADTable adTable, List<MpsLineBom> listBoms, MpsLine mpsLine) {
		this.table = adTable;
		this.input = listBoms;
		this.mpsLine = mpsLine;
		bomListMap = new LinkedHashMap<MpsLineBom, List<MpsLineBom>>();
		alternateBoms = new ArrayList<MpsLineBom>();
		BomTreeItemAdapter.setMoBoms(listBoms);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemAlter(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRevert(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRevertAll(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemDelete(tBar);
		section.setTextClient(tBar);
	}

	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();

		section = toolkit.createSection(parent, sectionStyle);
		section.setText(Message.getString("pdm.bom_list_detail_info"));
		section.marginWidth = 2;
		section.marginHeight = 2;
		toolkit.createCompositeSeparator(section);

		createToolBar(section);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite client = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(gridLayout);

		spart = new SectionPart(section);
		form.addPart(spart);
		section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));

		treeManager = new BomTreeManager(table);
		viewer = treeManager.createViewer(client, toolkit);
		section.setClient(client);
		createViewAction(viewer);
		refresh();
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionMpsLineBom(ss.getFirstElement());
				} catch (Exception e) {
					logger.error("Error MOSection : createViewAction() " + e);
				}
			}
		});
	}

	protected void setSelectionMpsLineBom(Object obj) {
		if (obj instanceof MpsLineBom) {
			this.selectedBom = (MpsLineBom) obj;
			TreeItem item = ((TreeViewer) viewer).getTree().getSelection()[0];
			TreeItem parent = item.getParentItem();
			// 不能对根MPSLineBom进行还原、删除和替代
			while (true) {
				if (parent != null && parent.getParentItem() == null) {
					MpsLineBom root = (MpsLineBom) parent.getData();
					this.rootMaterial = root.getMaterial();
					break;
				}
				if (parent != null)
					parent = parent.getParentItem();
				else {
					this.rootMaterial = null;
					break;
				}
			}
		}
	}

	protected void createToolItemAlter(ToolBar tBar) {
		alterItem = new ToolItem(tBar, SWT.PUSH);
		alterItem.setText(Message.getString("wip.alternate"));
		alterItem.setImage(SWTResourceCache.getImage("alternate"));
		alterItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				alterAdapter();
			}
		});
	}
	
	protected void createToolItemRevert(ToolBar tBar) {
		revertItem = new ToolItem(tBar, SWT.PUSH);
		revertItem.setText(Message.getString("ppm.revert"));
		revertItem.setImage(SWTResourceCache.getImage("optional"));
		revertItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				revertAdapter();
			}
		});
	}
	
	protected void createToolItemRevertAll(ToolBar tBar) {
		revertAllItem = new ToolItem(tBar, SWT.PUSH);
		revertAllItem.setText(Message.getString("ppm.revert_all"));
		revertAllItem.setImage(SWTResourceCache.getImage("optional"));
		revertAllItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				revertAllAdapter();
			}
		});
	}
	
	protected void createToolItemDelete(ToolBar tBar) {
		deleteItem = new ToolItem(tBar, SWT.PUSH);
		deleteItem.setText(Message.getString("common.delete"));
		deleteItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		deleteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	public void refresh() {
		viewer.setInput(input);
		((TreeViewer) viewer).expandAll();
	}

	public List<MpsLineBom> getMpsLineBoms() {
		return (List<MpsLineBom>)viewer.getInput();
	}
	
	protected void alterAdapter() {
		try {
			if (selectedBom != null && rootMaterial != null) {
				// 有Bom大类，可设置选择料,即实际选料
				Long rootMaterialRrn = rootMaterial.getObjectRrn();
				if(rootMaterial.getBomRrn() != null) {
					rootMaterialRrn = rootMaterial.getBomRrn();
				}
				List<MpsLineBom> mpsBoms = getMpsLineBoms();
				MpsLineBom cloneBom = (MpsLineBom)selectedBom.clone();
				cloneBom.setObjectRrn(selectedBom.getObjectRrn());
				AlternateDialog dialog = new AlternateDialog(UI.getActiveShell(), rootMaterial, mpsLine,
						selectedBom, rootMaterialRrn, mpsBoms);
				if (dialog.open() == Dialog.OK) {
					if(ppmManager == null) 
						ppmManager = Framework.getService(PPMManager.class);
//					int index = mpsBoms.indexOf(cloneBom);
					MpsLineBom alterBom = dialog.getUpdateBom();
					
					//删除列表中被替代料及其所有子BOM
//					mpsBoms.remove(cloneBom);
					List<MpsLineBom> delBoms = getAllChildren(mpsBoms, cloneBom, new ArrayList<MpsLineBom>());
					if(delBoms != null) {
						if(bomListMap != null)
							bomListMap.put(cloneBom, delBoms);
						alternateBoms.add(cloneBom);
					}
					mpsBoms.removeAll(delBoms);
					
					//获得替代料的所有BOM结构
					MpsLine tempMpsLine = new MpsLine();
					tempMpsLine.setObjectRrn(0L);
					tempMpsLine.setMaterialRrn(alterBom.getMaterialRrn());
					List<MpsLineBom> childBoms = ppmManager.getMpsLineBom(tempMpsLine);					
					//将替代料加到被替代料的位置
//					mpsBoms.add(alterBom);
					//在boms中加上替代料的子BOM
					mpsBoms.addAll(getRemoveParentMoBoms(childBoms, selectedBom));
					// 保存到数据库
					ppmManager.saveMpsLineBom(mpsLine, mpsBoms);
					UI.showInfo(Message.getString("common.save_successed"));
					
					if(ppmManager == null)
						ppmManager = Framework.getService(PPMManager.class);
					List<MpsLineBom> boms = ppmManager.getMpsLineBom(mpsLine);
					BomTreeItemAdapter.setMoBoms(boms);
					this.setInput(boms);
					this.refresh();
					selectedBom = null;
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at BomTreeSection : alterAdapter() ", e);
			return;
		}
	}
	
	public List<MpsLineBom> getRemoveParentMoBoms(List<MpsLineBom> childBoms, MpsLineBom mpsBom) {
		List<MpsLineBom> boms = new ArrayList<MpsLineBom>();
		BigDecimal parentQtyBom = mpsBom.getUnitQty();
		if(childBoms != null && childBoms.size() > 0) {
			for(MpsLineBom currentMoBom : childBoms) {
				if(currentMoBom.getMaterialParentRrn() == null && mpsBom.getMaterialRrn().equals(currentMoBom.getMaterialRrn())) {
					childBoms.remove(mpsBom);
				} else {
					currentMoBom.setQtyBom(currentMoBom.getQtyBom().multiply(parentQtyBom));
					if(currentMoBom.getPath() != null) {
						currentMoBom.setPath(mpsBom.getPath() + currentMoBom.getPath());						
					}
					currentMoBom.setPathLevel(mpsBom.getPathLevel() + currentMoBom.getPathLevel());
					currentMoBom.setMpsLineRrn(mpsBom.getMpsLineRrn());
					boms.add(currentMoBom);
				}
			}
		}
		return boms;
	}
	
	protected List<MpsLineBom> getAllChildren(
			List<MpsLineBom> boms, MpsLineBom parentBom, List<MpsLineBom> childBoms) {
		if(boms != null) {
			long parentRrn = parentBom.getMaterialRrn();
			long childLevel = parentBom.getPathLevel() + 1;
			
			for (MpsLineBom childBom : boms) {
				if (childBom.getMaterialParentRrn() != null
						&& childBom.getMaterialParentRrn() == parentRrn
						&& childBom.getPath().equals((parentBom.getPath() != null ? parentBom.getPath() : "") + parentRrn + "/")
						&& childBom.getPathLevel() == childLevel) {
					childBoms.add(childBom);
					childBoms = getAllChildren(boms, childBom, childBoms);
				}
			}
		}
		return childBoms;
	}
	
	protected void revertAdapter() {
		try {
			if (selectedBom != null && rootMaterial != null && mpsLine != null) {
				Long rootMaterialRrn = rootMaterial.getObjectRrn();
				if(rootMaterial.getBomRrn() != null) {
					rootMaterialRrn = rootMaterial.getBomRrn();
				}
				MpsLineBom cloneBom = (MpsLineBom)selectedBom.clone();
				List<MpsLineBom> mpsBoms = getMpsLineBoms();
				int index = mpsBoms.indexOf(selectedBom);
				// 获得替代料alterRrn和其父物料的parentRrn,
				// 若parentRrn为空表示为根Bom,不能还原,直接返回
				Long parentRrn = selectedBom.getMaterialParentRrn();
				Long alterRrn = selectedBom.getMaterialRrn();
				String path = selectedBom.getPath();
				if(parentRrn == null) return;
				
				// 从可替代料表中找到父物料为parentRrn,替代料为alterRrn并且Path为path的物料
				String whereClause = " materialRrn = " + rootMaterialRrn
					+ " AND alternateRrn = " + alterRrn
					+ " AND path = '" + path + "' ";
				ADManager adManager = Framework.getService(ADManager.class);
				List<MaterialAlternate> list = adManager.getEntityList(Env.getOrgRrn(),
						MaterialAlternate.class, 1, whereClause, null);
				// 如果List中无值,表示该物料不是可替代料,提示无需还原并返回
				if(list == null || list.size() == 0) {
					UI.showError(String.format(Message.getString("ppm.is_not_alter_material"), selectedBom.getMaterialId()));
					return;
				}
				Long sourceMaterialRrn = list.get(0).getChildRrn();
				// 根据mpsLine.materialRrn得到根物料的BomDetails, 然后从BomDetails查找路径为
				// path,父物料为parentRrn,物料为sourceMaterialRrn的BomDetail,并转为MpsLineBom
				if(pdmManager == null)
					pdmManager = Framework.getService(PDMManager.class);
				List<BomDetail> children = pdmManager.getActualLastBomDetails(mpsLine.getMaterialRrn());
				boolean falg = false;
				for(BomDetail bd : children) {
					if(bd.getPath().equals(path) && bd.getParentRrn().equals(parentRrn)
							&& bd.getChildRrn().equals(sourceMaterialRrn)) {
						//将materialRrn,unitQty,description等设为原来的值
						selectedBom.setMaterialRrn(bd.getChildRrn());
						selectedBom.setUnitQty(bd.getUnitQty());
						selectedBom.setDescription(bd.getDescription());
						selectedBom.setMaterial(bd.getChildMaterial());
						falg = true;
						break;
					}
				}
				// 防止没有找到原物料时,会将selectedBom保存到DB中
				if(falg) {
//					mpsBoms.remove(cloneBom);
//					mpsBoms.add(index, selectedBom);
					mpsBoms.removeAll(getAllChildren(mpsBoms, cloneBom, new ArrayList<MpsLineBom>()));
					MpsLineBom preBom = getPreBom(selectedBom);
					if(bomListMap != null && bomListMap.get(preBom) != null) {
						mpsBoms.addAll(bomListMap.get(preBom));
						bomListMap.remove(selectedBom);
					}
					
					if(ppmManager == null)
						ppmManager = Framework.getService(PPMManager.class);
					ppmManager.saveMpsLineBom(mpsLine, mpsBoms);
					List<MpsLineBom> boms = ppmManager.getMpsLineBom(mpsLine);
					BomTreeItemAdapter.setMoBoms(boms);
					this.setInput(boms);
					this.refresh();
					selectedBom = null;
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at BomTreeSection : revertAdapter() ", e);
			return;
		}
	}
	
	protected MpsLineBom getPreBom(MpsLineBom alterBom) {
		if(alternateBoms != null) {
			for(MpsLineBom bom : this.alternateBoms) {
				if(bom.getMaterialRrn().equals(alterBom.getMaterialRrn())) {
					if(bom.getPath() != null && alterBom.getPath() != null && bom.getPath().equals(alterBom.getPath())) {
						return bom;
					}
				}
			}
		}
		return alterBom;
	}
	
	protected void revertAllAdapter() {
		try {
			if (mpsLine != null) {
				boolean isRevertAll = UI.showConfirm(Message
						.getString("common.confirm_revert_all"));
				if (isRevertAll) {
					PPMManager ppmManager = Framework.getService(PPMManager.class);
					ppmManager.deleteMpsLineBom(mpsLine);
					
					List<MpsLineBom> boms = ppmManager.getMpsLineBom(mpsLine);
					BomTreeItemAdapter.setMoBoms(boms);
					this.setInput(boms);
					this.refresh();
					// 重新初始化存储还原的BOM列表
					bomListMap = new LinkedHashMap<MpsLineBom, List<MpsLineBom>>();
					alternateBoms = new ArrayList<MpsLineBom>();
					selectedBom = null;
				}
			}
		} catch (Exception e) {
			logger.error("Error at BomTreeSection : revertAllAdapter() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void deleteAdapter() {
		try {
			if (selectedBom != null && rootMaterial != null && mpsLine != null) {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					// 从boms中删除selectedBom及其子children, 然后重新保存mpsLine和boms
					// 最后重新从数据库中取得并刷新
					List<MpsLineBom> preList = this.getMpsLineBoms();
					List<MpsLineBom> deletes = new ArrayList<MpsLineBom>();
					deletes.add(selectedBom);
					deletes.addAll(BomTreeItemAdapter.getChildMpsLineBom(selectedBom));
					for(MpsLineBom mlBom : deletes) {
						preList.remove(mlBom);
					}
					
					PPMManager ppmManager = Framework.getService(PPMManager.class);
					// 后台方法在保存时先删除mpsLine对应的所有mpsLine,然后再保存preList
					ppmManager.deleteMpsLineBom(mpsLine, preList, deletes);
					List<MpsLineBom> boms = ppmManager.getMpsLineBom(mpsLine);
					BomTreeItemAdapter.setMoBoms(boms);
					this.setInput(boms);
					this.refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at BomTreeSection : deleteAdapter() ", e);
		}
	}

	public void setInput(Object input) {
		this.input = input;
	}
}
