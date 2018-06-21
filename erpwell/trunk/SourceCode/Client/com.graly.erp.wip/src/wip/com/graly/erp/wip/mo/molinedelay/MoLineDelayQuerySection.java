package com.graly.erp.wip.mo.molinedelay;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.wip.model.ManufactureOrderLineDelay;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.ADUserGroup;

public class MoLineDelayQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MoLineDelayQuerySection.class);

	protected ToolItem itemComments;
	private ManufactureOrderLineDelay selectMMoLineDelay;

	protected TableListManager listTableManager;
//	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	
	public MoLineDelayQuerySection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionMoLineDelay(ss.getFirstElement());
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionMoLineDelay(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void setSelectionMoLineDelay(Object obj) {
		if(obj instanceof ManufactureOrderLineDelay) {
			selectMMoLineDelay = (ManufactureOrderLineDelay)obj;
		} else {
			selectMMoLineDelay = null;
		}
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemComments(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		createToolItemExport(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemComments(ToolBar tBar) {
		itemComments = new AuthorityToolItem(tBar, SWT.PUSH,"WIP.MoLineDelay.Comments");
		itemComments.setText("备注");
		itemComments.setImage(SWTResourceCache.getImage("save"));
		itemComments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveCommentsAdapter();
			}
		});
	}
	protected void saveCommentsAdapter() {
		try {
			if(selectMMoLineDelay != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				ADUser user =(ADUser) adManager.getEntity(Env.getUser());
				boolean hasPurchase = false;
				if("采购物料".equals(selectMMoLineDelay.getDelayReason())){
					for(ADUserGroup userGroup :user.getUserGroups()){
						if(userGroup.getObjectRrn()==59698734L){
							hasPurchase= true;
							break;
						}
					}
					if(!hasPurchase){
						UI.showError("采购物料只允许采购人员添加备注");
						return;
					}
				}
				
				
				ADTable adTable = adManager.getADTable(0L, "WIPMoLineDelay");
				
				MoLineDelayCommentsDialog  commentsDialog = new MoLineDelayCommentsDialog(UI.getActiveShell(),selectMMoLineDelay,adTable);
				if(commentsDialog.open() == Dialog.OK) {
					ManufactureOrderLineDelay commentMoLineDelay = commentsDialog.getSelectMMoLineDelay();
					if(commentMoLineDelay.getComments()!=null){
						commentMoLineDelay = (ManufactureOrderLineDelay) adManager.saveEntity(commentMoLineDelay, Env.getUserRrn());
						viewer.refresh();
//						refresh();
					}
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}


	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	 
	
	protected ADTable getADTableOfInLineDialog(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
		}
		return null;
	}
	
	protected ADTable getADTableOfMovement(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("InSection : getADTableOfRequisition()", e);
		}
		return null;
	}
	
	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
		}
		return null;
	}
	
	//总共多少条记录
	protected void createSectionDesc(List<ManufactureOrderLineDelay> moLineDelays){
		try{ 
			String text = Message.getString("common.totalshow");
			long count = moLineDelays.size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("MasterSection : createSectionDesc ", e);
		}
	}
	
	
	
	@Override
	public void refresh() {
		List<ManufactureOrderLineDelay> moLineDelays =new ArrayList<ManufactureOrderLineDelay>();
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			moLineDelays = adManager.getEntityList(Env.getOrgRrn(), ManufactureOrderLineDelay.class,Integer.MAX_VALUE,
					getWhereClause(),"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewer.setInput(moLineDelays);
		tableManager.updateView(viewer);
		this.createSectionDesc(moLineDelays);
	}
	
	
}

