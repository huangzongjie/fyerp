package com.graly.erp.wip.mo.material_standtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.materiallocate.MaterialLocateManager;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.AbstractField;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class StandTimeBomTreeForm extends Form {
	protected static boolean isExpendAll = false;
	protected static final String FIELD_ID = "bomTree";
	protected IMessageManager mmng;
	protected MaterialField field;
	protected StandTimeSection standTimeSection;
	protected MaterialTreeManager treeManager;
	protected TreeViewer viewer;
	protected Long version;
	
	public StandTimeBomTreeForm(Composite parent, int style, Object object) {
    	super(parent, style, object);
    }
	
	public StandTimeBomTreeForm(Composite parent, int style, ADBase adBase,
			IMessageManager mmng, StandTimeSection standTimeSection) {
    	this(parent, style, adBase);
    	this.mmng = mmng;
    	this.standTimeSection = standTimeSection;
    	createForm();
    }
	
	public StandTimeBomTreeForm(Composite parent, int style, ADBase adBase,
			IMessageManager mmng, StandTimeSection standTimeSection,Long version) {
    	this(parent, style, adBase);
    	this.mmng = mmng;
    	this.standTimeSection = standTimeSection;
    	this.version=version;
    	createForm();
    }
	
	@Override
	public void createForm(){		
		super.createForm();
	}

	@Override
	public void addFields() {
		field = new MaterialField(FIELD_ID);
		addField(FIELD_ID, field);
	}
	
	public void refreshNewBom(Bom newBom) {
		isExpendAll = false;
		viewer.setInput(getInput());
		viewer.expandToLevel(2);
	}
	
	public void refreshEditorBom(Bom editorBom) {
		isExpendAll = false;
		viewer.setInput(getInput());
		viewer.expandToLevel(2);
	}
	
	public void removeBom(Bom bom) {
		isExpendAll = false;
		viewer.setInput(getInput());
		viewer.expandToLevel(2);
	}
	
	public void refresh(Material material) {
		isExpendAll = false;
		setObject(material);
		viewer.setInput(getInput());
		viewer.expandToLevel(2);
	}
	
	// 展开所有
	public void refreshAll() {
		isExpendAll = true;
		viewer.setInput(getInput());
		viewer.expandAll();
	}
	
	public void refreshBomOnly(Bom bom) {
		isExpendAll = false;
		viewer.refresh(bom);
	}
	
	protected List<Material> getInput() {
		List<Material> list = new ArrayList<Material>();
		list.add((Material)getObject());
		return list;
	}

	@Override
	public boolean validate() {		
		return false;
	}
	
	protected class MaterialField extends AbstractField implements MaterialLocateManager {
		protected TreeItem selectedItem;
		
		List<Bom> queryBoms;
		Map<Bom, TreeItem> tiMap;
		TreeItem rootItem;
	    
		public MaterialField(String id) {
	        super(id);
	    }
		
		@Override
		public void createContent(Composite composite, FormToolkit toolkit) {
			GridLayout gl = new GridLayout();
			gl.marginHeight = 0;
			gl.marginWidth = 0;
			composite.setLayout(gl);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
	        mControls = new Control[1];
	        new StandTimeMaterialLocateCompent(this).createMaterialLocateComposite(composite, toolkit);
	        createTreeContent(composite);
//			refresh();
			mControls[0] = viewer.getControl();

			Tree tree = viewer.getTree();
//			viewer.addSelectionChangedListener(getSelectionChangedListener());
			tree.addSelectionListener(getTreeSelectionListener(tree));
		}
		
		protected void createTreeContent(Composite composite) {
			treeManager = new MaterialTreeManager(SWT.NULL, new BomExpendAll());
	        viewer = (TreeViewer)treeManager.createViewer(new Tree(composite,
	        		SWT.LINE_DASHDOTDOT | SWT.FULL_SELECTION | SWT.BORDER |SWT.H_SCROLL | SWT.V_SCROLL),
	        		toolkit);
		}

		protected SelectionListener getTreeSelectionListener(final Tree tree) {
			return new SelectionListener() {				
				public void widgetSelected(SelectionEvent e) {
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
				
			};
		}
		 

		protected String getPath(TreeItem item) {
			String path = "";
			for(TreeItem parent = item.getParentItem(); parent != null; parent = parent.getParentItem()) {
				Object obj = parent.getData();
				if(obj instanceof Bom) {
					Bom bom = (Bom)obj;
					path = bom.getChildRrn() + "/" + path;
				} else if(obj instanceof Material) {
					path = ((Material)obj).getObjectRrn() + "/" + path;
					return path;
				}
			}
			return path;
		}

		@Override
		public void refresh() {
			treeManager.setInput(getInput());
			viewer.expandToLevel(2);
		}

		@Override
		public void setValue(Object value){
			notifyValueChangeListeners(this, value);
			this.value = value;
		}
		
		// 获得第二级Bom的上一级实际物料的objectRrn,由于只可以对第二级Bom设置实际选料,所以其上一级是Material
		public Long getBomTypeParentRrn(Bom bom) {
			if(selectedItem == null || bom == null) return null;
			Long actualParentRrn = 0L;
			if(selectedItem.getData() instanceof Bom) {
				if(bom.equals(((Bom)selectedItem.getData()))) {
					TreeItem parent = selectedItem.getParentItem();
					if(parent != null && parent.getData() instanceof Material) {
						actualParentRrn = ((Material)parent.getData()).getBomRrn();
					}
				}
			}
			return actualParentRrn;
		}
		
		@Override
		public void locateLast(String materialId, int index) {
			if(materialId == null || "".equals(materialId.trim()))
				return;
			if(queryBoms == null || queryBoms.size() == 0 || index < 0 || index > queryBoms.size() - 1)
				return;
			TreeItem ti = null;
			if(tiMap.get(queryBoms.get(index)) != null) {
				ti = tiMap.get(queryBoms.get(index));
			} else if(rootItem != null && ((Material)rootItem.getData()).getMaterialId().indexOf(materialId) != -1) {
				ti = rootItem;
			}
			if(ti != null) {
				((TreeViewer)viewer).getTree().setSelection(ti);
				ti.setBackground(SWTResourceCache.getColor("Folder"));
			}
		}

		@Override
		public boolean locateMaterial(String materialId) {
			clearPreLocateMaterials();
			if(materialId == null || "".equals(materialId.trim()))
				return false;
			queryBoms = new ArrayList<Bom>();
			tiMap = new HashMap<Bom, TreeItem>();
			rootItem = null;
			getChildrenData(null, materialId);
			if(queryBoms.size() > 0 || rootItem != null)
				return true;
			return false;
		}
		
		protected void clearPreLocateMaterials() {
			if(queryBoms != null && queryBoms.size() > 0) {
				for(Bom bom : queryBoms) {
					if(tiMap.get(bom) != null) {
						tiMap.get(bom).setBackground(null);
					}
				}
				queryBoms.clear();
			}
			if(rootItem != null)
				rootItem.setBackground(null);
		}
		
		protected void getChildrenData(TreeItem parent, String materialId) {
			TreeItem[] its = null;
			if(parent == null) its = ((TreeViewer)viewer).getTree().getItems();
			else its = parent.getItems();
			if(its == null || its.length == 0)
				return;
			
			for(TreeItem ti : its) {
				if(ti.getData() instanceof Bom) {
					Bom bom = (Bom)ti.getData();
					if(bom.getChildMaterial() != null
							&& bom.getChildMaterial().getMaterialId().indexOf(materialId) != -1) {
						queryBoms.add(bom);
						tiMap.put(bom, ti);
						ti.setBackground(SWTResourceCache.getColor("Folder"));
					}
				} else if(ti.getData() instanceof Material) {
					Material m = (Material)ti.getData();
					if(m.getMaterialId().indexOf(materialId) != -1) {
						rootItem = ti;
						ti.setBackground(SWTResourceCache.getColor("Folder"));
					}
				}
				getChildrenData(ti, materialId);
			}
		}

		@Override
		public void locateNext(String materialId, int index) {
			if(materialId == null || "".equals(materialId.trim()))
				return;
			if(queryBoms == null || queryBoms.size() == 0 || index < 0 || index > queryBoms.size() - 1)
				return;
			TreeItem ti = null;
			if(tiMap.get(queryBoms.get(index)) != null) {
				ti = tiMap.get(queryBoms.get(index));
			} else if(rootItem != null && ((Material)rootItem.getData()).getMaterialId().indexOf(materialId) != -1) {
				ti = rootItem;
			}
			if(ti != null) {
				((TreeViewer)viewer).getTree().setSelection(ti);
				ti.setBackground(SWTResourceCache.getColor("Folder"));
			}
		}
	}
	
	public Long getBomTypeParentRrn(Bom bom) {
		return field.getBomTypeParentRrn(bom);
	}
	
	protected class BomExpendAll implements EnableExpendAll {
		public BomExpendAll() {
			isExpendAll = false;
		}
		
		@Override
		public boolean isExpendAll() {
			return isExpendAll;
		}
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public MaterialTreeManager getTreeManager() {
		return treeManager;
	}

	public void setTreeManager(MaterialTreeManager treeManager) {
		this.treeManager = treeManager;
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

	public MaterialField getField() {
		return field;
	}

	public void setField(MaterialField field) {
		this.field = field;
	}
	
}
