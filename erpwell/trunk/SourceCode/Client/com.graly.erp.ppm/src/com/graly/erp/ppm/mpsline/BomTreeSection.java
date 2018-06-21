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
			// ���ܶԸ�MPSLineBom���л�ԭ��ɾ�������
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
				// ��Bom���࣬������ѡ����,��ʵ��ѡ��
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
					
					//ɾ���б��б�����ϼ���������BOM
//					mpsBoms.remove(cloneBom);
					List<MpsLineBom> delBoms = getAllChildren(mpsBoms, cloneBom, new ArrayList<MpsLineBom>());
					if(delBoms != null) {
						if(bomListMap != null)
							bomListMap.put(cloneBom, delBoms);
						alternateBoms.add(cloneBom);
					}
					mpsBoms.removeAll(delBoms);
					
					//�������ϵ�����BOM�ṹ
					MpsLine tempMpsLine = new MpsLine();
					tempMpsLine.setObjectRrn(0L);
					tempMpsLine.setMaterialRrn(alterBom.getMaterialRrn());
					List<MpsLineBom> childBoms = ppmManager.getMpsLineBom(tempMpsLine);					
					//������ϼӵ�������ϵ�λ��
//					mpsBoms.add(alterBom);
					//��boms�м�������ϵ���BOM
					mpsBoms.addAll(getRemoveParentMoBoms(childBoms, selectedBom));
					// ���浽���ݿ�
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
				// ��������alterRrn���丸���ϵ�parentRrn,
				// ��parentRrnΪ�ձ�ʾΪ��Bom,���ܻ�ԭ,ֱ�ӷ���
				Long parentRrn = selectedBom.getMaterialParentRrn();
				Long alterRrn = selectedBom.getMaterialRrn();
				String path = selectedBom.getPath();
				if(parentRrn == null) return;
				
				// �ӿ�����ϱ����ҵ�������ΪparentRrn,�����ΪalterRrn����PathΪpath������
				String whereClause = " materialRrn = " + rootMaterialRrn
					+ " AND alternateRrn = " + alterRrn
					+ " AND path = '" + path + "' ";
				ADManager adManager = Framework.getService(ADManager.class);
				List<MaterialAlternate> list = adManager.getEntityList(Env.getOrgRrn(),
						MaterialAlternate.class, 1, whereClause, null);
				// ���List����ֵ,��ʾ�����ϲ��ǿ������,��ʾ���軹ԭ������
				if(list == null || list.size() == 0) {
					UI.showError(String.format(Message.getString("ppm.is_not_alter_material"), selectedBom.getMaterialId()));
					return;
				}
				Long sourceMaterialRrn = list.get(0).getChildRrn();
				// ����mpsLine.materialRrn�õ������ϵ�BomDetails, Ȼ���BomDetails����·��Ϊ
				// path,������ΪparentRrn,����ΪsourceMaterialRrn��BomDetail,��תΪMpsLineBom
				if(pdmManager == null)
					pdmManager = Framework.getService(PDMManager.class);
				List<BomDetail> children = pdmManager.getActualLastBomDetails(mpsLine.getMaterialRrn());
				boolean falg = false;
				for(BomDetail bd : children) {
					if(bd.getPath().equals(path) && bd.getParentRrn().equals(parentRrn)
							&& bd.getChildRrn().equals(sourceMaterialRrn)) {
						//��materialRrn,unitQty,description����Ϊԭ����ֵ
						selectedBom.setMaterialRrn(bd.getChildRrn());
						selectedBom.setUnitQty(bd.getUnitQty());
						selectedBom.setDescription(bd.getDescription());
						selectedBom.setMaterial(bd.getChildMaterial());
						falg = true;
						break;
					}
				}
				// ��ֹû���ҵ�ԭ����ʱ,�ὫselectedBom���浽DB��
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
					// ���³�ʼ���洢��ԭ��BOM�б�
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
					// ��boms��ɾ��selectedBom������children, Ȼ�����±���mpsLine��boms
					// ������´����ݿ���ȡ�ò�ˢ��
					List<MpsLineBom> preList = this.getMpsLineBoms();
					List<MpsLineBom> deletes = new ArrayList<MpsLineBom>();
					deletes.add(selectedBom);
					deletes.addAll(BomTreeItemAdapter.getChildMpsLineBom(selectedBom));
					for(MpsLineBom mlBom : deletes) {
						preList.remove(mlBom);
					}
					
					PPMManager ppmManager = Framework.getService(PPMManager.class);
					// ��̨�����ڱ���ʱ��ɾ��mpsLine��Ӧ������mpsLine,Ȼ���ٱ���preList
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
