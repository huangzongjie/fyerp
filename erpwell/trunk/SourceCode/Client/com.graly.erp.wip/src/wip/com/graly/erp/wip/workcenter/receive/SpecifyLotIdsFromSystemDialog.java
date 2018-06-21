package com.graly.erp.wip.workcenter.receive;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class SpecifyLotIdsFromSystemDialog extends InClosableTitleAreaDialog {
	private Logger logger = Logger.getLogger(SpecifyLotIdsFromSystemDialog.class);
	
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 400;
	
	protected StructuredViewer viewer;
	protected TableViewerManager lotManager;
	protected ADTable adTable;
	protected Lot lot;
	protected List selectLots;
	protected MoLineReceiveSection parentSection;
	protected int needNums;
	
	public SpecifyLotIdsFromSystemDialog(Shell parentShell, ADTable adTable, Lot lot, MoLineReceiveSection parentSection, int needNums) {
		super(parentShell);
		this.adTable = adTable;
		this.lot = lot;
		this.parentSection = parentSection;
		this.needNums = needNums;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		setTitle("请选择指定的批号");
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite buttonBar = toolkit.createComposite(composite);
		buttonBar.setLayout(new GridLayout(2, false));
		buttonBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button selectAllBtn = toolkit.createButton(buttonBar, "全选", SWT.PUSH);
		selectAllBtn.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(viewer != null && viewer instanceof CheckboxTableViewer){
					CheckboxTableViewer ctv = (CheckboxTableViewer)viewer;
					TableItem[] tis = ctv.getTable().getItems();
					for(TableItem ti : tis){
						ti.setChecked(true);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
			
		});
		Button revertBtn = toolkit.createButton(buttonBar, "反选", SWT.PUSH);
		revertBtn.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(viewer != null && viewer instanceof CheckboxTableViewer){
					CheckboxTableViewer ctv = (CheckboxTableViewer)viewer;
					TableItem[] tis = ctv.getTable().getItems();
					for(TableItem ti : tis){
						ti.setChecked(!ti.getChecked());
					}
				}				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		createStructuredViewer(composite);
		return composite;
	}

	private void createStructuredViewer(Composite ctl) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		lotManager = new TableListManager(adTable, SWT.CHECK);
		viewer = (TableViewer)lotManager.createViewer(ctl, toolkit);
		try {
			WipManager wipManager = Framework.getService(WipManager.class);
			List specifyLots = wipManager.getGenLotIds(lot);
			viewer.setInput(specifyLots);
			if(parentSection.getCurrentLots() != null && parentSection.getCurrentLots().size() > 0){
				CheckboxTableViewer ctv = (CheckboxTableViewer)viewer;
				for(Object element : parentSection.getCurrentLots()){
					ctv.setChecked(element, true);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	@Override
	protected void okPressed() {
		if(viewer instanceof CheckboxTableViewer){
			CheckboxTableViewer ctv = (CheckboxTableViewer)viewer;
			Object[] checkedObjs = ctv.getCheckedElements();
			if(!checkObjsNum(checkedObjs)){
				return;
			}
			if(checkedObjs != null && checkedObjs.length > 0){
				selectLots = new ArrayList();
				for(Object obj : checkedObjs){
					selectLots.add(obj);
				}
			}
		}
		super.okPressed();
	}

	private boolean checkObjsNum(Object[] checkedObjs) {
//		if(checkedObjs == null || checkedObjs.length != needNums){
//			int nums = 0;
//			if(checkedObjs != null){
//				nums = checkedObjs.length;
//			}
//			UI.showError("需要" + needNums + "个批次,实际选择了" + nums + "个");
//			return false;
//		}
//		return true;
		//只允许空选或者满足条件的选择 ，不允许存在选取其中的某一个
		if(checkedObjs.length == 0 || checkedObjs.length == needNums){
			return true;
		} else {
			int nums = 0;
			nums = checkedObjs.length;
			UI.showError("需要" + needNums + "个批次,实际选择了" + nums + "个");
			return false;
		}
	}

	public List getSelectLots() {
		return selectLots;
	}

	public void setSelectLots(List selectLots) {
		this.selectLots = selectLots;
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 5;
		layout.marginBottom = 5;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
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
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	public StructuredViewer getViewer() {
		return viewer;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}
	
	
}
