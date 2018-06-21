package com.graly.erp.wip.partlydisassemble;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class MoLineListDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(MoLineListDialog.class);
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 300;
	private IManagedForm managedForm;
	private ADTable moLineTable;
	private TableViewerManager viewerManager;
	private StructuredViewer moLineViewer;
	private ManufactureOrderLine selectedMoLine;
	private final String MOLINE_TABLE = "WIPManufactureOrderLine";
	private String whereClause;
	
	public MoLineListDialog(Shell parentShell,IManagedForm managedForm, String whereClause) {
		super(parentShell);
		this.managedForm = managedForm;
		this.whereClause = whereClause;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle("选择一个工作令");
		setMessage("以所选的工作令的BOM结构作为拆分时的物料还原的依据");
		FormToolkit toolkit = managedForm.getToolkit();
        Composite composite = toolkit.createComposite(parent, SWT.BORDER);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createTableContent(composite, toolkit);
        
        return composite;
    }
	
	protected void createTableContent(Composite parent, FormToolkit toolkit) {
		Composite client = toolkit.createComposite(parent, SWT.NULL);
        client.setLayout(new GridLayout(1, false));
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        moLineTable = getADTableByName(MOLINE_TABLE);
		viewerManager = new EntityTableManager(moLineTable);
		moLineViewer = viewerManager.createViewer(parent, toolkit);
		moLineViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectedMoLine((ManufactureOrderLine) ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			
		});
		moLineViewer.setInput(new EntityItemInput(moLineTable, whereClause, ""));		
		viewerManager.updateView(moLineViewer);
	}
	
	protected ADTable getADTableByName(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("InLineEntityBlock : getADTableOfRequisition()", e);
		}
		return null;
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
	protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		Message.getString("common.ok"), false);
        createButton(parent, IDialogConstants.CANCEL_ID,
        		Message.getString("common.cancel"), false);
    }
	
	public ManufactureOrderLine getMoLine(){
		return selectedMoLine;
	}

	public void setSelectedMoLine(ManufactureOrderLine selectedMoLine) {
		this.selectedMoLine = selectedMoLine;
	}
	
	@Override
	protected void okPressed() {
		if(selectedMoLine == null){
			UI.showInfo("请选择一个工作令");
			return;
		}
		super.okPressed();
	}
}
