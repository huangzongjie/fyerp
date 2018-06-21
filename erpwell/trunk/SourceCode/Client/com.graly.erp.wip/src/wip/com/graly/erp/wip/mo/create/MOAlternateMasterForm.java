package com.graly.erp.wip.mo.create;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.MaterialAlternate;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class MOAlternateMasterForm extends Form {
	private static final Logger logger = Logger.getLogger(MOAlternateMasterForm.class);
	private static final String PREFIX = " materialRrn = ";
	protected ADTable table;
	protected TableViewer viewer;
	protected MOAlternateDialog parentDialog;
	
	protected String whereClause;
	protected MaterialAlternate currentAlternate;
	private Long rootMaterialRrn;
	private ADManager adManager;
	private WipManager wipManager;
	
	protected List<ADField> tableFields = new ArrayList<ADField>();
	
	public MOAlternateMasterForm(Composite parent, int style, Material material,
			ManufactureOrderBom moBom, IMessageManager mmng) {
    	super(parent, style, moBom);
    	createForm();
    }
	
	public MOAlternateMasterForm(Composite parent, int style, ADTable table,
			Long rootMaterialRrn, ManufactureOrderBom moBom, MOAlternateDialog parentDialog) {
		super(parent, style, moBom);
		this.rootMaterialRrn = rootMaterialRrn;
    	this.table = table;
    	this.parentDialog = parentDialog;
    	createForm();
    }
	
	@Override
    public void createForm(){
        try {
        	if(object != null) {
        		if(object != null) {
            		/* 找出根物料为rootMaterialRrn, 物料为alternateRrn的可替代料列表 */
            		whereClause = PREFIX + rootMaterialRrn
            		+ " AND path = '" + getActualPath()
            		+ "' AND childRrn = " + ((ManufactureOrderBom)getObject()).getMaterialRrn() + " ";
            	}
        	}
        } catch (Exception e) {
        	logger.error("MOAlternateMasterForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        super.createForm();
    }
	
	private String getActualPath() {
		String root = String.valueOf(parentDialog.getRoot().getObjectRrn());
		String path = ((ManufactureOrderBom)getObject()).getRealPath();
		// 如果没有参考Bom大类，则直接返回path;
		if(root.equals(String.valueOf(rootMaterialRrn))) {
			return path;
		}
		StringBuffer actualPath = new StringBuffer("");
		String[] strs = path.split("/");
		for(int i = 0; i < strs.length; i++) {
			if(strs[i].equals(root)) {
				strs[i] = String.valueOf(rootMaterialRrn);
				break;
			}
		}
		for(int i = 0; i < strs.length; i++) {
			actualPath.append(strs[i]);
			actualPath.append("/");
		}
		return actualPath.toString();
	}
	
	@Override
	public void addFields() {
	}
	
	@Override
	protected void createContent() {
		toolkit = new FormToolkit(getDisplay());
        setLayout(new FillLayout());
        form = toolkit.createScrolledForm(this);
        
        Composite body = form.getBody();
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        body.setLayout(layout);
        
        TableListManager em = new TableListManager(table);
        viewer = (TableViewer)em.createViewer(body, toolkit);
	    viewer.getTable().addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = viewer.getTable().getSelection();
				Object obj = items[0].getData();
				if(obj instanceof MaterialAlternate) {
					currentAlternate = (MaterialAlternate)obj;
				} else {
					currentAlternate = null;
				}
				parentDialog.alternateChanaged(currentAlternate);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	    initTableContent(em);
	}
	
	protected void initTableContent(TableListManager em) {
		try {
			if(adManager == null)
				adManager = Framework.getService(ADManager.class);
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), table.getObjectRrn(), 
            		Env.getMaxResult(), whereClause, null);
			
			MaterialSum ms = null;
			MaterialAlternate ma = null;
			for(ADBase adBase : list) {
				if(adBase instanceof MaterialAlternate) {
					ma = (MaterialAlternate)adBase;
					if(wipManager == null)
						wipManager = Framework.getService(WipManager.class);
					ms = wipManager.getMaterialSum(Env.getOrgRrn(),
							((MaterialAlternate)adBase).getAlternateRrn(), false, false);
					if(ms != null) {
						ma.setQtyMin(ms.getQtyMin());
						ma.setQtyAllocation(ms.getQtyAllocation());
						ma.setQtyOnHand(ms.getQtyOnHand());
						ma.setQtyTransit(ms.getQtyTransit());
						ma.setQtyMoLineWip(ms.getQtyMoLineWip());
						ma.setQtySo(ms.getQtySo());
					}
				}
			}
			
			viewer.setInput(list);
			em.updateView(viewer);
		} catch(Exception e) {
			
		}
	}

	@Override
	public boolean validate() {
		return true;
	}

	public MaterialAlternate getCurrentAlternate() {
		return currentAlternate;
	}

	public void setCurrentAlternate(MaterialAlternate currentAlternate) {
		this.currentAlternate = currentAlternate;
	}
}
