package com.graly.erp.pdm.bomhistory;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.materiallocate.MaterialLocateCompent;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.BomTreeDialog;
import com.graly.erp.pdm.bomedit.BomTreeForm;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.ui.util.Message;

public class BomHisTreeForm extends BomTreeForm {
	//protected TreeViewer viewer;
	protected BomHisTreeManager treeManager;
	protected Control[] mControls;
	public BomHisTreeForm(Composite parent, int style, ADBase adBase,
			IMessageManager mmng, BomTreeDialog btd, Long version) {
		super(parent, style, adBase, mmng, btd,version);
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
	
	public void refreshBomOnly(Bom bom) {
		isExpendAll = false;
		viewer.refresh(bom);
	}
	
	
	public class MaterialField extends BomTreeForm.MaterialField{

		public MaterialField(String id) {
			super(id);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void createContent(Composite composite, FormToolkit toolkit) {
			GridLayout gl = new GridLayout();
			gl.marginHeight = 0;
			gl.marginWidth = 0;
			composite.setLayout(gl);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
	        mControls = new Control[1];
	        new MaterialLocateCompent(this).createMaterialLocateComposite(composite, toolkit);
	       String string=Message.getString("inv.version");
	        Label versionText = new Label(composite,SWT.BORDER);
	        versionText.setText(string+getVersion().toString());
	        createTreeContent(composite);
			refresh();
			mControls[0] = viewer.getControl();

			Tree tree = viewer.getTree();
//			viewer.addSelectionChangedListener(getSelectionChangedListener());
			tree.addSelectionListener(getTreeSelectionListener(tree));
		}
		
		@Override
		public void createTreeContent(Composite composite) {
			treeManager = new BomHisTreeManager(SWT.NULL, new BomExpendAll(),version);
	        viewer = (TreeViewer)treeManager.createViewer(new Tree(composite,
	        		SWT.LINE_DASHDOTDOT | SWT.FULL_SELECTION | SWT.BORDER |SWT.H_SCROLL | SWT.V_SCROLL),
	        		toolkit);
		}
		

		@Override
		public void refresh() {
			treeManager.setInput(getInput());
			viewer.expandToLevel(2);
		}
			
		
	}
	
	public TreeViewer getViewer() {
		return viewer;
	}
	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}
	
	


	
	

	
}
